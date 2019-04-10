package org.fiware.cosmos.orion.flink.cep

import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy
import org.apache.flink.cep.scala.CEP
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time
import org.fiware.cosmos.orion.flink.cep.connector._
import org.json4s.DefaultFormats
import org.slf4j.LoggerFactory

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
      .map(ExecutionGraph)

    // Entity Stream
    val entityStream : DataStream[Entity] = stream
      .filter(_.isLeft)
      .map(_.left.get )
      .flatMap(_.entities)

    operationStream.print()

    // TODO

    //env.execute("CEP Monitoring")
  }

}
