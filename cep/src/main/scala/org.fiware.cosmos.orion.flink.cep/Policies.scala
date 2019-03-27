package org.fiware.cosmos.orion.flink.cep

//case class Policy(name: String, active: Boolean, punishment: String, attributes: Map[String,String])

object Policies {
  final val numMaxEvents = 9
  final val facturationTime = 60
  final val aggregateTime = 10000
}
