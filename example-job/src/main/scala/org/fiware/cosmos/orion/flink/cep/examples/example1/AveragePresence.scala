package org.fiware.cosmos.orion.flink.cep.examples.example1

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time
import org.fiware.cosmos.orion.flink.connector._





/**
  * Example Home Average Presence in city homes
  * FIWARE Data Usage Control
  *
  */
object AveragePresence{
//  final val URL_CB = "http://138.4.22.138:1026/v2/entities/home-avg/attrs"
  final val CONTENT_TYPE = ContentType.JSON
  final val METHOD = HTTPMethod.POST
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // Create Orion Source. Receive notifications on port 9001
    val eventStream = env.addSource(new OrionSource(9001))
    // Process event stream
    // Calculate the average presence of the houses in the city each minute
    val processedDataStream = eventStream
          .flatMap(event => event.entities)
          .map(entity => {

            val id = entity.attrs("_id").value.toString
            val items = entity.attrs("items").value.asInstanceOf[List[Map[String,Any]]]
            val products = items.map(product => new Product(id, product("net_am").asInstanceOf[Number].floatValue(),product("desc").asInstanceOf[String]))
            products//.flatten
          })
          .keyBy("id")
          .timeWindow(Time.seconds(5))
         .min("price")
            // .map(item => new Product(item._1, item._2, item._3))
            /*

            .min("price")*/

//
//      .aggregate(new AveragePresenceAggregator)
//       .map(avg=>{
//          new OrionSinkObject((new AvgEntity(avg)).toString, URL_CB, CONTENT_TYPE, METHOD)
//        })
    // Post average to new entity in Orion Context Broker
//    OrionSink.addSink(processedDataStream)
    // print the results with a single thread, rather than in parallel
    processedDataStream.print().setParallelism(1)
    env.execute("Socket Window NgsiEvent")



  }

  case class Product(id: String, price: Float, desc: String)
  case class AvgEntity(  avg: Float) extends  Serializable {
    override def toString :String = { "{\"avg\": { \"value\":" + avg + ", \"type\": \"Float\"}}" }
  }
}