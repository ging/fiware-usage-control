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

import scala.util.matching.Regex

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

    // Operation Stream
    val operationStream : DataStream[ExecutionGraph] = stream
      .filter(_.isRight)
      .map(_.right.get)
      .flatMap(_.msg.split(" -> "))
      .map(ExecutionGraph(_))

    // Entity Stream
    val entityStream : DataStream[Entity] = stream
      .filter(_.isLeft)
      .map(_.left.get )
      .flatMap(_.entities)

    operationStream.print()

    // First pattern: At least N events in T
    val countPattern = Pattern.begin[Entity]("events" )
        .timesOrMore(Policies.numMaxEvents+1).within(Time.seconds(Policies.facturationTime))

     CEP.pattern(entityStream, countPattern).select(Signals.createAlert("COUNT_POLICY", _))

    // Second pattern: Source -> Sink. Aggregation TimeWindow
    val aggregatePattern = Pattern.begin[ExecutionGraph]("start", AfterMatchSkipStrategy.skipPastLastEvent())
      .where(executionGraphChecker(_, "source"))
      .notFollowedBy("middle").where(executionGraphChecker(_, "aggregation"))
      .followedBy("end").where(executionGraphChecker(_, "sink")).timesOrMore(1)

    CEP.pattern(operationStream, aggregatePattern).select(Signals.createAlert("AGGREGATION_POLICY", _))


    env.execute("CEP Monitoring")
  }

  def executionGraphChecker(event: ExecutionGraph, flag: String) : Boolean = {
    val sourcePattern : Regex = "Source(.*)".r
    val sinkPattern : Regex = "Sink(.*)".r
    val aggPattern : Regex = ".+TumblingProcessingTimeWindows\\((\\d+)\\),.*".r
    event.msg match {
      case sourcePattern(_) => flag == "source"
      case aggPattern(window) =>  flag == "aggregation" &&  window.toFloat >= Policies.aggregateTime
      case sinkPattern(_) => flag == "sink"
      case _ => false
    }
  }

}