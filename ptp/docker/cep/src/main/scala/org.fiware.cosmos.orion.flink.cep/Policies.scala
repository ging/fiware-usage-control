package org.fiware.cosmos.orion.flink.cep

import java.text.SimpleDateFormat
import java.util.Calendar

import org.fiware.cosmos.orion.flink.cep.connector.ExecutionGraph

import scala.util.matching.Regex


object Policy extends Enumeration {
  val COUNT_POLICY, AGGREGATION_POLICY = Value
}

object Punishment extends Enumeration {
  val UNSUBSCRIBE,KILL_JOB,MONETIZE = Value
}


object Policies {
  final val numMaxEvents = 2
  final val facturationTime = 15
  final val aggregateTime = 10000

  def executionGraphChecker(event: ExecutionGraph, flag: String, aggregationTime: Long = 0) : Boolean = {
    val sourcePattern : Regex = "Source(.*)".r
    val sinkPattern : Regex = "Sink(.*)".r
    val aggPattern : Regex = ".+TumblingProcessingTimeWindows\\((\\d+)\\),.*".r
    event.msg match {
      case sourcePattern(_) => flag == "source"
      case aggPattern(window) =>  flag == "aggregation" &&  window.toFloat >= aggregationTime
      case sinkPattern(_) => flag == "sink"
      case _ => false
    }
  }

  def checkTime(from: String, to: String, within: Boolean): Boolean = {

    val today = Calendar.getInstance.getTime

    val minuteFormat = new SimpleDateFormat("mm")
    val hourFormat = new SimpleDateFormat("HH")

    val currentHour = hourFormat.format(today)
    val currentMinute = minuteFormat.format(today)
    val now = currentHour + ":" + currentMinute

    if (from < now && now < to) within else !within

  }
}
