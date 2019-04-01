package org.fiware.cosmos.orion.flink.cep.examples.example1

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time
import org.fiware.cosmos.orion.flink.connector._


/**
  * Example average expense per purchase in a grocery store
  * FIWARE Data Usage Control
  *
  */
//org.fiware.cosmos.orion.flink.cep.examples.example1.AveragePurchase
object SourceToSink {
//  final val URL_CB = "http://138.4.22.138:1026/v2/entities/home-avg/attrs"
  final val CONTENT_TYPE = ContentType.JSON
  final val METHOD = HTTPMethod.POST
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // Create Orion Source. Receive notifications on port 9001
    val eventStream = env.addSource(new OrionSource(9001))

    // Sink without processing
    eventStream.print()
    env.execute("Socket Window NgsiEvent")
  }

}