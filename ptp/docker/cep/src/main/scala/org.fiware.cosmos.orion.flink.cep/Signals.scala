package org.fiware.cosmos.orion.flink.cep

import org.fiware.cosmos.orion.flink.cep.connector.JobId
import org.slf4j.LoggerFactory

import scala.collection._

object Signals {
  private lazy val logger = LoggerFactory.getLogger(getClass)

  /**
    * Create alert signal depending on policy rule
    * @param rule Name of the rule
    * @param content List of events that trigger the rule
    * @param punishment Punishment for not complying
    * @return
    */
  def createAlert(rule: Policy.Value, content: Map[String,Iterable[Any]], punishment: Punishment.Value) : Any = {
    rule match {
      case Policy.COUNT_POLICY => {
          val size = content("events").size
          logger.info(s"Ya has recibido ${size} eventos. El máximo permitido es ${Policies.numMaxEvents} eventos en ${Policies.facturationTime} segundos")
      }
      case Policy.AGGREGATION_POLICY => {
        logger.info(s"El procesado de los datos debe hacerse de forma agregada y utilizando una ventana mayor o igual que ${Policies.aggregateTime}")
      }
    }
    performPunishment(punishment, content)
    null
  }

  /**
    * Executes the punishment for not complying with a given rule
    * @param punishment Punishment for not complying
    * @param content List of events that trigger the rule
    */
  private def performPunishment(punishment: Punishment.Value, content: Map[String,Iterable[Any]]): Unit = {
    punishment match {
      case Punishment.UNSUBSCRIBE => {
        JobId.subscriptionIds.map(sId=>{
          CBRequests.unsubscribe("172.18.1.10:1026", sId)
        })
        logger.warn("Unsubscribe")
      }
      case Punishment.KILL_JOB => {
        CBRequests.killJob("138.4.7.94:8081", JobId.jobId )
        logger.warn("Kill Job: " + JobId.jobId)
      }
      case Punishment.MONETIZE => {
        logger.warn("$$$$$$$$$$")
      }
    }
  }
}
