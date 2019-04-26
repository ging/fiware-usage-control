package org.fiware.cosmos.orion.flink.cep.orion.fiware.cosmos.orion.flink.cep.connector

// Pizza class
trait JobId  {
  var jobId : String
  var subscriptionIds : Seq[String]
}

// companion object
object JobId {
  var jobId : String = "No id assigned so far"
  var subscriptionIds : Seq[String] = List()
}