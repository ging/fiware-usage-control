package org.fiware.cosmos.orion.flink.cep.examples.example1

case class SupermarketProduct(id: String, desc: String,  price: Float) extends  Serializable {
  override def toString :String = { "\r\n{ \"id\":  \"" + id + "\", \"desc\": \"" + desc + "\", \"price\": " + price + "}" }
}