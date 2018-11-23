package org.fiware.cosmos.orion.flink.cep


import org.apache.flink.streaming.api.scala._
import org.apache.flink.cep.nfa.AfterMatchSkipStrategy
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time
import org.fiware.cosmos.orion.flink.connector._
import org.json4s.DefaultFormats
import org.slf4j.LoggerFactory
import org.apache.flink.cep.scala.{CEP}
import org.apache.flink.cep.scala.pattern.Pattern
/**
  * CEP Monitoring
  *
  */
object CEPMonitoring{
  private lazy val logger = LoggerFactory.getLogger(getClass)
  implicit val formats = DefaultFormats
  val executionGraphProperties: Seq[String] =  Seq.empty
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // Create Orion Source. Receive notifications on port 9001
    val orionStream = env.addSource(new OrionSource(9001, "flink"))

    // Receive Entities
    val orionProcessedDataStream : DataStream[Entity] = orionStream
       .flatMap(event => {
      event.entities.map(
        entity=> {
          val attr  = ( "originalCreationTime" -> new Attribute("Long", event.creationTime, Map.empty[String, Any]))
          new Entity(entity.id,  entity.`type`,  entity.attrs + attr)
        }
      )
    })
    orionProcessedDataStream.print()

    // First pattern: At least 3 events in 1 minute
    val countPattern = Pattern.begin[Entity]("start",AfterMatchSkipStrategy.skipPastLastEvent()).timesOrMore(3).within(Time.seconds(10))

    // Second pattern: At least 2 different zip codes
    val zipCodePattern =countPattern.followedBy("middle")
      .where((ent: Entity,ctx)=>  {
        ctx.getEventsForPattern("start").map(e=>e.attrs("zip").value).toSet.size >= 2 })

    // Concatenate patterns
    val pattern = zipCodePattern

    // Apply pattern to dataStream
    val patternStream  = CEP.pattern( orionProcessedDataStream , pattern)


    val outputTag = OutputTag[String]("side-output")

    // Gather all matched events
    val orionDataStreamSink : DataStream[OrionSinkObject] = patternStream.select(outputTag){
        (pattern: scala.collection.Map[String, Iterable[Entity]], timestamp: Long) => {println("here")
          ""}
      }{
        (pattern : scala.collection.Map[String, Iterable[Entity]])=> {
          println(pattern)
          (pattern.get("start").toList(0) ++ pattern.get("middle").toList(0)).toList}}
       .map(entities => {
        println(entities)
        new OrionSinkObject(new BuiltEntity(entities).toString, "http://localhost:9029/", ContentType.JSON, HTTPMethod.POST)
      })

    val timeoutResult: DataStream[String] = orionDataStreamSink.getSideOutput(outputTag)
    timeoutResult.map(i=> i + "timedout").print()


    // Send matched data to Flink Job
    OrionSink.addSink(orionDataStreamSink)


    env.execute("CEP Monitoring")
  }

  case class BuiltEntity(entities: List[Entity]) extends  Serializable {
    override def toString :String = {
      val entityList = entities.map(entity=>{
        val attrs = entity.attrs.map(attr=> "\""+attr._1+"\":{\"value\": \""+attr._2.value+"\", \"type\": \""+attr._2.`type`+"\", \"metadata\":"+"{}"+"}").mkString(", ")
        "{\"id\":\""+entity.id+"\",\"type\":\""+entity.`type`+"\","+attrs+"}"
      }).mkString(", ")
      "{ \"data\": ["+entityList+"],  \"subscriptionId\": \"\" }"
    }
  }
  case class FluentLog(host: String, ident: String, message: String)
}