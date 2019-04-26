package org.fiware.cosmos.orion.flink.cep.connector
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.http.client.methods.{HttpEntityEnclosingRequestBase, HttpPatch, HttpPost, HttpPut}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.slf4j.LoggerFactory

/**
  * Message type accepted by the Orion Sink
  * @author @sonsoleslp
  * @param content Content of the body of the message
  * @param url URL to which the message will be sent
  * @param contentType Type of content. It can be: ContentType.JSON or ContentType.Plain
  * @param method HTTP Method. It can be: HTTPMethod.POST, HTTPMethod.PUT, HTTPMethod.PATCH
  */
case class OrionSinkObject(content: String, url: String, contentType: ContentType.Value, method: HTTPMethod.Value)

/**
  * Content type of the HTTP message
  */
object ContentType extends Enumeration {
  type ContentType = Value
  val JSON = Value("application/json")
  val Plain = Value("text/plain")
}

/**
  * HTTP Method of the message
  */
object HTTPMethod extends Enumeration {
  type HTTPMethod = Value
  val POST = Value("HttpPost")
  val PUT = Value("HttpPut")
  val PATCH = Value("HttpPatch")
}

/**
  * Sink for sending Flink processed data to the Orion Context Broker
  */
class OrionSink

/**
  * Singleton instance of OrionSink
  */
object OrionSink {
  private lazy val logger = LoggerFactory.getLogger(getClass)

  /**
    * Http object creator for sending the message
    * @param method HTTP Method
    * @param url Destination URL
    * @return HTTP object
    */
  def getMethod(method: HTTPMethod.Value, url: String): HttpEntityEnclosingRequestBase = {
    method match {
      case HTTPMethod.POST => new HttpPost(url)
      case HTTPMethod.PUT => new HttpPut(url)
      case HTTPMethod.PATCH => new HttpPatch(url)
    }
  }

  /**
    * Function for adding the Orion Sink
    * @param stream DataStream of the OrionSinkObject
    */
  def addSink( stream: DataStream[OrionSinkObject]): Unit = {

     stream.addSink( msg => {

       val httpEntity : HttpEntityEnclosingRequestBase= createHttpMsg(msg)

       val client = HttpClientBuilder.create.build

       try {
         val response = client.execute(httpEntity)
          logger.info("POST to " + msg.url)
       } catch {
         case e: Exception => {
           logger.error(e.toString)
         }
       }

     })

   }

  /**
    * Create the HTTP message from the specified params
    * @param msg OrionSinkObject
    * @return Built Http Entity
    */
  def createHttpMsg(msg: OrionSinkObject) : HttpEntityEnclosingRequestBase= {
    val httpEntity = getMethod(msg.method, msg.url)
    httpEntity.setHeader("Content-type", msg.contentType.toString)
    httpEntity.setEntity(new StringEntity((msg.content)))
    httpEntity
  }
}

