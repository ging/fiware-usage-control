package org.fiware.cosmos.orion.flink.cep

import java.util.Properties

import org.apache.flink.streaming.api.scala._
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.cep.nfa.AfterMatchSkipStrategy
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011
import org.fiware.cosmos.orion.flink.connector._
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.slf4j.LoggerFactory
import org.apache.flink.cep.scala.{CEP, PatternStream}
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor
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
    // Create Kafka Source
    /* val properties = new Properties()
     properties.setProperty("bootstrap.servers", "138.4.7.94:9092")
     val usageControlConsumer = new FlinkKafkaConsumer011[String]("flink", new SimpleStringSchema(), properties)
     val kafkaStream = env.addSource(usageControlConsumer)
    */

    // Create Orion Source. Receive notifications on port 9001
    val orionStream = env.addSource(new OrionSource(9001, "flink"))
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

    //    val windowedStream =  orionProcessedDataStream
    //      .timeWindowAll(Time.seconds(10))
    //      .aggregate(new CustomAggregator)


    val countPattern = Pattern.begin[Entity]("start",AfterMatchSkipStrategy.skipPastLastEvent()).timesOrMore(3).within(Time.seconds(10))
    val zipCodePattern =countPattern.followedBy("middle")
      .where((ent: Entity,ctx)=>  {
        ctx.getEventsForPattern("start").map(e=>e.attrs("zip").value).toSet.size >= 2 })

    val pattern = zipCodePattern

    val patternStream  = CEP.pattern( orionProcessedDataStream , pattern)
    val outputTag = OutputTag[String]("side-output")

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

    OrionSink.addSink(orionDataStreamSink)

    //    val result: DataStream[String] = patternStream.select("ALERT")

    //    windowedStream.print()

    //    val processedDataStream  = kafkaStream // Get Data from Kafka
    //      .map(log => try {
    //      parse(log).extract[FluentLog]  } catch { // Take only logs
    //        case e :Throwable => new FluentLog("ERROR","ERROR","ERROR")
    //      })
    //      .filter(x => x.host != "ERROR")
    //      .map(log => log.message match {
    //        case msg if msg.contains("org.apache.flink.runtime.executiongraph.ExecutionGraph") && !msg.contains("Usage Control Signals") =>
    //          parseOperations(getOnlyMsg(log.message))
    //        case msg if msg.contains("org.fiware.cosmos.orion.flink.connector.OrionHttpHandler") =>
    //          parseEntities(getOnlyMsg(log.message))
    //        case _ => null
    //      })
    //      .filter(x => x!=null)


    // print the results with a single thread, rather than in parallel
    //    processedDataStream.print().setParallelism(1)
    env.execute("CEP Monitoring")
  }



  def parseEntities(log: String) ={
    parse(log).extract[HttpBody].data.map(entity => {
      // Retrieve entity id
      val entityId = entity("id").toString
      // Retrieve entity type
      val entityType = entity("type").toString
      // Retrieve attributes
      val attrs = entity.filterKeys(x => x != "id" & x!= "type" )
        //Convert attributes to Attribute objects
        .transform((k,v) => MapToAttributeConverter
        .unapply(v.asInstanceOf[Map[String,Any]]))
      new Entity(entityId,entityType,attrs )
    })
  }


  def parseOperations(log: String) = {
    val operations = log.split(" -> ")
    //    executionGraphProperties :+ operations
    (operations.map(p => println(p)))
  }


  def getOnlyMsg(msg: String) = {
    msg.split(" ").drop(3).mkString(" ")
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