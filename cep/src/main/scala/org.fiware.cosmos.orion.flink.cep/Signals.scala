package org.fiware.cosmos.orion.flink.cep

import org.fiware.cosmos.orion.flink.cep.connector.{Entity, ExecutionGraph}

import scala.collection._

object Signals {
  def createAlert(signal: String, content: Map[String,Iterable[Any]]) : Any = {
    signal match {
      case "COUNT_POLICY" => {
          val size = content("events").size
          println(s"Ya has recibido ${size} eventos. El máximo permitido es ${Policies.numMaxEvents} eventos en ${Policies.facturationTime} segundos")
      }
      case "AGGREGATION_POLICY" => {
          println(s"El procesado de los datos debe hacerse de forma agregada y utilizando una ventana mayor o igual que ${Policies.aggregateTime}")
      }
    }
    null
  }
}
