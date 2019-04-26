package org.fiware.cosmos.orion.flink.cep

import org.json4s.JsonAST.{JArray, JField, JObject, JString}
import org.json4s.jackson.JsonMethods.parse
import scalaj.http.Http

object CBRequests {

  /**
    * Method for delelting a Subscription in Orion context Broker
    * @param contextBrokerHost Context Broker host in the form of IP:Port Ex(127.0.0.1:1026)
    * @param subscriptionId subscriptionId
    */
  def unsubscribe(contextBrokerHost: String, subscriptionId:String ) {
    try {
      val msg = Http("http://"+contextBrokerHost+"/v2/subscriptions/"+subscriptionId).method("DELETE").asString.code
      println(msg)
    } catch {
      case _: Exception => println("There was an error")
      case _: Error => println("There was an error")
    }
  }

  def getSubscriptionId(contextBrokerHost: String, notificationURL: String ): String = {
    try {
      val msg = parse(Http("http://"+contextBrokerHost+"/subscriptions").asString.body)
      val id=for {
        JArray(objList) <- msg
        JObject(obj) <- objList
        JField("notification",JObject(notification))<-obj
        JField("http",JObject(http))<-notification
        JField("url",JString(url))<-http
        if notificationURL.equals(url)
        JField("id",JString(id))<-obj
      }yield id

      return id(0)
    } catch {
      case _: Exception => println("There was an error")
      case _: Error => println("There was an error")
    }
    ""
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
      println(msg)
    } catch {
      case _: Exception => println("There was an error")
      case _: Error => println("There was an error")
    }
  }
}
