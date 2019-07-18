package org.fiware.cosmos.orion.flink.cep.examples.example1

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.fiware.cosmos.orion.flink.connector._
import org.apache.flink.streaming.api.windowing.time.Time


/**
  * Example average expense per purchase in a grocery store
  * FIWARE Data Usage Control
  *
  */

//org.fiware.cosmos.orion.flink.cep.examples.example1.AveragePrice
object AveragePrice {
  final val CONTENT_TYPE = ContentType.JSON
  final val METHOD = HTTPMethod.POST
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // Create Orion Source. Receive notifications on port 9001
    val eventStream = env.addSource(new OrionSource(9001))

    // Process event stream
    val processedDataStream = eventStream
      .flatMap(event => event.entities)
      .map(entity => {
        val id = entity.attrs("_id").value.toString
        val items = entity.attrs("items").value.asInstanceOf[List[Map[String,Any]]]
        items.map(product => {
          val productName = product("desc").asInstanceOf[String]
          val unitPrice =  product("net_am").asInstanceOf[Number].floatValue()
          val unitNumber = product("n_unit").asInstanceOf[Number].floatValue()
          val price = unitPrice * unitNumber
          SupermarketProduct(id, productName, price)
        })
      })
      .map(_.map(_.price).sum)
      .timeWindowAll(Time.seconds(15))
      .aggregate(new AverageAggregate)

    // Print the results with a single thread, rather than in parallel
    processedDataStream.print().setParallelism(1)
    env.execute("Socket Window NgsiEvent")
  }

}