package org.fiware.cosmos.orion.flink.cep

import org.fiware.cosmos.orion.flink.cep.connector.ExecutionGraph

import scala.util.matching.Regex


object Policies {
  final val numMaxEvents = 2
  final val facturationTime = 15
  final val aggregateTime = 10000

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

  def checkTime(from: Long, to: Long, within: Boolean): Boolean = {
    val now = 15
    if(from < now && now < to) {
      return within
    }
    !within

  }
}
