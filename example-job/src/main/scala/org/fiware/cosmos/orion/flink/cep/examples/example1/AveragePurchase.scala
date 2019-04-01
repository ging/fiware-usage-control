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
object AveragePurchase {
//  final val URL_CB = "http://138.4.22.138:1026/v2/entities/home-avg/attrs"
  final val CONTENT_TYPE = ContentType.JSON
  final val METHOD = HTTPMethod.POST
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // Create Orion Source. Receive notifications on port 9001
    val eventStream = env.addSource(new OrionSource(9001))

    // Sink directo => Matar
    // eventStream.print()

    // Process event stream
//    val processedDataStream = eventStream
//      .flatMap(event => event.entities)
//      .map(entity => {
//        val id = entity.attrs("_id").value.toString
//        val items = entity.attrs("items").value.asInstanceOf[List[Map[String,Any]]]
//        items.map(product => {
//          val productName = product("desc").asInstanceOf[String]
//          val price = product("net_am").asInstanceOf[Number].floatValue() * product("n_unit").asInstanceOf[Number].floatValue()
//          SupermarketProduct(id, productName, price)
//        })
//      })
//      .map(products => products.map(_.price).sum)
     // .timeWindowAll(Time.seconds(15))
     // .aggregate(new AverageAggregate)

    // Post average to new entity in Orion Context Broker
    // OrionSink.addSink(processedDataStream)
    // print the results with a single thread, rather than in parallel
//    processedDataStream.print().setParallelism(1)
    val objStream = eventStream.map(_=>OrionSinkObject("{'msg':'hola'}","http://138.4.7.110:5000/fiware",ContentType.JSON, HTTPMethod.POST))
    OrionSink.addSink(objStream)
    env.execute("Socket Window NgsiEvent")
  }

  case class SupermarketProduct(id: String, desc: String,  price: Float) extends  Serializable {
    override def toString :String = { "\r\n{ \"id\":  \"" + id + "\", \"desc\": \"" + desc + "\", \"price\": " + price + "}" }
  }

}