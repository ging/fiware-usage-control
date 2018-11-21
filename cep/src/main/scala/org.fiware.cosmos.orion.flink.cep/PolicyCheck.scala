package org.fiware.cosmos.orion.flink.cep

import org.fiware.cosmos.orion.flink.connector.Entity

object PolicyCheck {
  def checkPolicies(logs: List[Entity]): Boolean = {
    if (logs.length > 4)
      true
    else
      false
  }
}
