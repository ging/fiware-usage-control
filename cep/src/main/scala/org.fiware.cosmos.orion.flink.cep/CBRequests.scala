import org.json4s.JsonAST.{JArray, JField, JObject, JString}
import org.json4s.jackson.JsonMethods.parse
import scalaj.http.Http

object CBRequests {

  def unsubscribe(contextBrokerURL: String, notificationURL:String ) {
    try {
      val id=getSubscriptionId(contextBrokerURL,notificationURL)
      val msg = Http(contextBrokerURL+"/"+id).method("DELETE").asString.code
      println(msg)
    } catch {
      case _: Exception => null
      case _: Error => null
    }
  }

  def getSubscriptionId(contextBrokerURL: String, notificationURL:String ): String = {
    try {
      val msg = parse(Http(contextBrokerURL).asString.body)
      val id=for {
        JArray(objList) <- msg
        JObject(obj) <- objList
        JField("notification",JObject(notification))<-obj
        JField("http",JObject(http))<-notification
        JField("url",JString(url))<-http
        if notificationURL.equals(url)
        JField("id",JString(id))<-obj
      }yield id(0)

      return id(0)
    } catch {
      case _: Exception => null
      case _: Error => null
    }
  }
  def main(args: Array[String]): Unit = {
    unsubscribe("http://138.4.22.138:1026/v2/subscriptions","http://138.4.7.110:9001/notify")
  }

}
