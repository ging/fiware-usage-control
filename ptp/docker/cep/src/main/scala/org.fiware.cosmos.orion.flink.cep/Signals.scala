package org.fiware.cosmos.orion.flink.cep

import org.fiware.cosmos.orion.flink.cep.orion.fiware.cosmos.orion.flink.cep.connector.JobId

import scala.collection._

object Signals {
  def createAlert(rule: Policy.Value, content: Map[String,Iterable[Any]], punishment: Punishment.Value) : Any = {
    rule match {
      case Policy.COUNT_POLICY => {
          val size = content("events").size
          println(s"Ya has recibido ${size} eventos. El máximo permitido es ${Policies.numMaxEvents} eventos en ${Policies.facturationTime} segundos")
      }
      case Policy.AGGREGATION_POLICY => {
          println(s"El procesado de los datos debe hacerse de forma agregada y utilizando una ventana mayor o igual que ${Policies.aggregateTime}")
      }
    }
    performPunishment(punishment)
    null
  }

  private def performPunishment(punishment: Punishment.Value): Unit = {
    punishment match {
      case Punishment.UNSUBSCRIBE => println("Unsubscribe")//CBRequests.unsubscribe("138.4.22.138:1026","http://138.4.7.94:9001/notify")
      case Punishment.KILL_JOB => println("Kill Job: " + JobId.jobId) //killJob()
      case Punishment.MONETIZE => println("$$$$$$$$$$")
    }
  }
}
