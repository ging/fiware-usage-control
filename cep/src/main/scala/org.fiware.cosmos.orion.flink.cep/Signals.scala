package org.fiware.cosmos.orion.flink.cep

import org.fiware.cosmos.orion.flink.cep.orion.fiware.cosmos.orion.flink.cep.connector.JobId

import scala.collection._

object Signals {
  def createAlert(signal: String, content: Map[String,Iterable[Any]], punishment: String) : Any = {
    signal match {
      case "COUNT_POLICY" => {
          val size = content("events").size
          println(s"Ya has recibido ${size} eventos. El máximo permitido es ${Policies.numMaxEvents} eventos en ${Policies.facturationTime} segundos")
      }
      case "AGGREGATION_POLICY" => {
          println(s"El procesado de los datos debe hacerse de forma agregada y utilizando una ventana mayor o igual que ${Policies.aggregateTime}")
      }
    }
    performPunishment(punishment)
    null
  }

  private def performPunishment(punishment: String): Unit = {
    punishment match {
      case "UNSUBSCRIBE" => println("unsubscribe")//CBRequests.unsubscribe("138.4.22.138:1026","http://138.4.7.94:9001/notify")
      case "KILL_JOB" => println("Kill Job: " + JobId.jobId) //killJob()
      case "MONETIZE"=> println("$$$$$$$$$$")
    }
  }
}
