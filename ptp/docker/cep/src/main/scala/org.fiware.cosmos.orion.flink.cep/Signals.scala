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
    performPunishment(punishment, content)
    null
  }

  private def performPunishment(punishment: Punishment.Value, content: Map[String,Iterable[Any]]): Unit = {
    punishment match {
      case Punishment.UNSUBSCRIBE => {
        println(content)
        JobId.subscriptionIds.map(sId=>{
          CBRequests.unsubscribe("172.18.1.10:1026", sId)
        })
        println("Unsubscribe")
      }
      case Punishment.KILL_JOB => {
        CBRequests.killJob("138.4.7.94", JobId.jobId )
        println("Kill Job: " + JobId.jobId)
      }
      case Punishment.MONETIZE => {
        println("$$$$$$$$$$")
      }
    }
  }
}
