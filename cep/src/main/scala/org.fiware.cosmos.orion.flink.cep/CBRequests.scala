package org.fiware.cosmos.orion.flink.cep

import org.json4s.JsonAST.{JArray, JField, JObject, JString}
import org.json4s.jackson.JsonMethods.parse
import scalaj.http.Http

object CBRequests {

  /**
    * Method for delelting a Subscription in Orion context Broker
    * @param contextHost Context Broker host in the form of IP:Port Ex(127.0.0.1:1026)
    * @param notificationURL URL of the notification fiel of the subscription
    */
  def unsubscribe(contextBrokerHost: String, notificationURL:String ) {
    try {
      val id=getSubscriptionId(contextBrokerHost,notificationURL)
      val msg = Http("http://"+contextBrokerHost+"/subscriptions/"+id).method("DELETE").asString.code
      println(msg)
    } catch {
      case _: Exception => null
      case _: Error => null
    }
  }

  def getSubscriptionId(contextBrokerHost: String, notificationURL:String ): String = {
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
      case _: Exception => null
      case _: Error => null
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
      println(msg)
    } catch {
      case _: Exception => null
      case _: Error => null
    }
  }
}
