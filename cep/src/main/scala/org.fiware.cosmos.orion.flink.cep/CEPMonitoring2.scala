package org.fiware.cosmos.orion.flink.cep

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
object CEPMonitoring2{
  private lazy val logger = LoggerFactory.getLogger(getClass)
  implicit val formats = DefaultFormats
  val executionGraphProperties: Seq[String] =  Seq.empty

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // Create Orion Source. Receive notifications on port 9001
    val stream : DataStream[Either[NgsiEvent,ExecutionGraph]] = env.addSource(new CEPSource(9200))

    // Operation Stream
    val operationStream : DataStream[ExecutionGraph] = stream
      .filter(_.isRight)
      .map(_.right.get)
      .flatMap(_.msg.split(" -> "))
      .map(ExecutionGraph)

    // Entity Stream
    val entityStream : DataStream[Entity] = stream
      .filter(_.isLeft)
      .map(_.left.get )
      .flatMap(_.entities)

    operationStream.print()

    // First pattern: At least N events in T. 12h -14h
    val countPattern = Pattern.begin[Entity]("events" )
        .timesOrMore(Policies.numMaxEvents+1).within(Time.seconds(Policies.facturationTime))
        .where(_=>Policies.checkTime(12,14,true))
    CEP.pattern(entityStream, countPattern).select(events =>
      Signals.createAlert("COUNT_POLICY", events, "MONETIZE"))


    // First pattern: At least N events in T. Any other time
    val countPattern2 = Pattern.begin[Entity]("events" )
      .timesOrMore(Policies.numMaxEvents+1).within(Time.seconds(Policies.facturationTime))
      .where(_=>Policies.checkTime(12,14,false))
    CEP.pattern(entityStream, countPattern2).select(events =>
      Signals.createAlert("COUNT_POLICY", events, "UNSUBSCRIBE"))


    // Second pattern: Source -> Sink. Aggregation TimeWindow
    val aggregatePattern = Pattern.begin[ExecutionGraph]("start", AfterMatchSkipStrategy.skipPastLastEvent())
      .where(Policies.executionGraphChecker(_, "source"))
      .notFollowedBy("middle").where(Policies.executionGraphChecker(_, "aggregation"))
      .followedBy("end").where(Policies.executionGraphChecker(_, "sink")).timesOrMore(1)
    CEP.pattern(operationStream, aggregatePattern).select(events =>
      Signals.createAlert("AGGREGATION_POLICY", events, "KILL_JOB"))

    env.execute("CEP Monitoring")
  }



}