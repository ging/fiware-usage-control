package org.fiware.cosmos.orion.flink.cep.examples.example1

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time
import org.fiware.cosmos.orion.flink.connector.OrionSource

/**
  * Example Home Average Presence in city homes
  * FIWARE Data Usage Control
  *
  */
object AveragePresence{

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // Create Orion Source. Receive notifications on port 9001
    //    val eventStream = FiwareUsageControl.start(9001, "flink", env)
    val eventStream = env.addSource(new OrionSource(9029, "flink"))
    // Process event stream
    val processedDataStream = eventStream
      .flatMap(event => event.entities)
      //      .keyBy("id")
      .timeWindowAll(Time.seconds(60))
      .aggregate(new AveragePresenceAggregator)


    // print the results with a single thread, rather than in parallel
    processedDataStream.print().setParallelism(1)
    env.execute("Socket Window NgsiEvent")



  }

  case class Temp_Node(id: String, temperature: Float)
}