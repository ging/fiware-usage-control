package org.fiware.cosmos.orion.flink.cep

import java.util.Properties

import org.apache.flink.streaming.api.scala._
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.slf4j.LoggerFactory

/**
  * CEP Monitoring
  *
  */
object CEPMonitoring{
  private lazy val logger = LoggerFactory.getLogger(getClass)
  implicit val formats = DefaultFormats

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // Create Orion Source. Receive notifications on port 9001
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "138.4.7.94:9092")
    val usageControlConsumer = new FlinkKafkaConsumer011[String]("flink", new SimpleStringSchema(), properties)

    val kafkaStream = env.addSource(usageControlConsumer)
//    kafkaStream.print()
    // Process event stream


    val processedDataStream  = kafkaStream
      .map(log => try {
      parse(log).extract[FluentLog] } catch {
        case e => null
      }).filter(x => x!=null)
      .map(log=>log.message)

    // print the results with a single thread, rather than in parallel
    processedDataStream.print().setParallelism(1)
    env.execute("Socket Window NgsiEvent")



  }

  case class FluentLog(host: String, ident: String, message: String)
}