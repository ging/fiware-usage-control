package org.fiware.cosmos.orion.flink.cep

import org.fiware.cosmos.orion.flink.cep.CBRequests.logger
import org.fiware.cosmos.orion.flink.cep.connector.JobId
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write
import org.slf4j.LoggerFactory

import scala.collection._
import scalaj.http.Http

object Signals {
  implicit val formats = DefaultFormats

  private lazy val logger = LoggerFactory.getLogger(getClass)

  /**
    * Create alert signal depending on policy rule
    * @param rule Name of the rule
    * @param content List of events that trigger the rule
    * @param punishment Punishment for not complying
    * @return
    */
  def createAlert(rule: Policy.Value, content: Map[String,Iterable[Any]], punishment: Punishment.Value) : Any = {
    val enforced = performPunishment(punishment, content)
    val log = rule match {
      case Policy.COUNT_POLICY => {
        val size = content("events").size
        s"You have already received ${size} events. The maximum allowed is ${Policies.numMaxEvents} events in ${Policies.facturationTime} seconds"

      }
      case Policy.AGGREGATION_POLICY => {
        s"El procesado de los datos debe hacerse de forma agregada y utilizando una ventana mayor o igual que ${Policies.aggregateTime}"

      }
    }
    reportPunishment(rule, punishment, log)
    logger.warn(log + ". " + enforced)
  }

  /**
    * Executes the punishment for not complying with a given rule
    * @param punishment Punishment for not complying
    * @param content List of events that trigger the rule
    */
  private def performPunishment(punishment: Punishment.Value, content: Map[String,Iterable[Any]]): String = {
    punishment match {
      case Punishment.UNSUBSCRIBE => {
        JobId.subscriptionIds.foreach(sId=>{
          CBRequests.unsubscribe("172.18.1.10:1026", sId)
        })
        "Notification unsubscribed"
      }
      case Punishment.KILL_JOB => {
        CBRequests.killJob("138.4.7.94:8081", JobId.jobId )
        "Killed Job with id " + JobId.jobId
      }
      case Punishment.MONETIZE => {
        "Begin to charge with special fare"
      }
    }
  }

  private def reportPunishment(rule: Policy.Value, punishment: Punishment.Value, msg: String  ): Unit = {
    val body = write(ControlObject(rule, "11321", JobId.jobId, msg, punishment ))
    try {
      val req = Http("http://172.18.1.15:3001")
        .method("POST")
        .postData(body)
        .header("content-type", "application/json")
        .asString
        .code
    } catch {
      case _: Exception => logger.error("There was an error sending the log")
      case _: Error => logger.error("There was an error sending the log")
    }
  }

  case class ControlObject (`type`: Policy.Value, userId: String, jobId: String, msg: String, punishment: Punishment.Value)
}
