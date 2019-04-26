package org.fiware.cosmos.orion.flink.cep

import org.json4s.JsonAST.{JArray, JField, JObject, JString}
import org.json4s.jackson.JsonMethods.parse
import org.slf4j.LoggerFactory

import scalaj.http.Http
object CBRequests {
  private lazy val logger = LoggerFactory.getLogger(getClass)

  /**
    * Method for delelting a Subscription in Orion context Broker
    * @param contextBrokerHost Context Broker host in the form of IP:Port Ex(127.0.0.1:1026)
    * @param subscriptionId subscriptionId
    */
  def unsubscribe(contextBrokerHost: String, subscriptionId:String ) {
    try {
      val msg = Http("http://"+contextBrokerHost+"/v2/subscriptions/"+subscriptionId).method("DELETE").asString.code
      CBRequests.logger.info(msg.toString)
    } catch {
      case _: Exception => CBRequests.logger.error("There was an error")
      case _: Error => CBRequests.logger.error("There was an error")
    }
  }

  /**
    * Method for killing a Job running in a flink cluster
    * @param flinkHost Flink Job Manager host in the form of IP:Port Ex(127.0.0.1:1026)
    * @param jobId Flink Job ID
    */
  def killJob(flinkHost: String, jobId:String ) {
    try {
      val msg = Http("http://"+flinkHost+"/jobs/"+jobId)
        .method("PATCH")
        .asString.code
      CBRequests.logger.info(msg.toString)
    } catch {
      case _: Exception => logger.error("There was an error")
      case _: Error => logger.error("There was an error")
    }
  }
}
