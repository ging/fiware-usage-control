package org.fiware.cosmos.orion.flink.cep


import javafx.scene.control.Alert

import org.apache.flink.streaming.api.scala._
import org.apache.flink.cep.nfa.AfterMatchSkipStrategy
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time
import org.fiware.cosmos.orion.flink.cep.connector._
import org.json4s.DefaultFormats
import org.slf4j.LoggerFactory
import org.apache.flink.cep.scala.CEP
import org.apache.flink.cep.scala.pattern.Pattern

/**
  * FIWARE Data Usage Control
  * Flink Complex Event Processing
  *
  */
object CEPMonitoring{
  private lazy val logger = LoggerFactory.getLogger(getClass)
  implicit val formats = DefaultFormats
  val executionGraphProperties: Seq[String] =  Seq.empty
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // Create Orion Source. Receive notifications on port 9001
    val stream : DataStream[Either[NgsiEvent,ExecutionGraph]] = env.addSource(new CEPSource(9200))

    val operationStream : DataStream[ExecutionGraph] = stream
      .filter(_.isRight)
      .map(_.right.get)
      .flatMap(_.msg.split(" -> "))
      .map(ExecutionGraph(_))

    val entityStream : DataStream[Entity] = stream
      .filter(_.isLeft)
      .map(_.left.get )
      .flatMap(_.entities)

    operationStream.print()

    // First pattern: At least N events in T
    val countPattern = Pattern.begin[Entity]("start", AfterMatchSkipStrategy.skipPastLastEvent())
        .timesOrMore(Policies.numMaxEvents).within(Time.seconds(Policies.facturationTime))
     CEP.pattern(entityStream, countPattern).select(Signals.createAlert("COUNT_POLICY", _))

    // Second pattern:
    val

/*
    // Second pattern: At least 2 different zip codes
    val zipCodePattern =countPattern.followedBy("middle")
      .where((ent: Entity,ctx)=>  {
        ctx.getEventsForPattern("start").map(e => e.attrs("zip").value).toSet.size >= 2 })

    // Concatenate patterns
    val pattern = zipCodePattern

    // Apply pattern to dataStream
    val patternStream  = CEP.pattern(entityStream, pattern)

    val outputTag = OutputTag[String]("side-output")
*/
    // Gather all matched events
/*    val orionDataStreamSink : DataStream[OrionSinkObject] = patternStream.select(outputTag){
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

      */

  /*  val orionDataStreamSink = patternStream.select(
      (pattern : scala.collection.Map[String, Iterable[Entity]])=> {
        println(pattern)
        (pattern.get("start").toList(0) ++ pattern.get("middle").toList(0)).toList})
      .map(entities => {
        println(entities)
        new OrionSinkObject(new BuiltEntity(entities).toString, "http://localhost:9029/", ContentType.JSON, HTTPMethod.POST)
      })
*/

/*    val timeoutResult: DataStream[String] = orionDataStreamSink.getSideOutput(outputTag)
    timeoutResult.map(i=> i + "timedout").print()*/

    env.execute("CEP Monitoring")
  }

}