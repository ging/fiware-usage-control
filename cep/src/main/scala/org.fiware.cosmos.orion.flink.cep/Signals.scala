package org.fiware.cosmos.orion.flink.cep

object Signals {
  def createAlert(signal: String, content: Any) : Any = {
    signal match {
      case "COUNT_POLICY" => {
          System.out.println("Te has pasado de eventos")
      }
    }
    null
  }
}
