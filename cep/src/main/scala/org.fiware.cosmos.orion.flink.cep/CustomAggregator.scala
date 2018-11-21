package org.fiware.cosmos.orion.flink.cep

import org.apache.flink.api.common.functions.AggregateFunction
import org.fiware.cosmos.orion.flink.connector.Entity

class CustomAggregator extends AggregateFunction[Entity,  List[Entity], String] {
  override def createAccumulator() =  List.empty

  override def add(value: (Entity), accumulator:  List[Entity]) =
     accumulator ++ List(value)

  override def getResult(accumulator:  List[Entity]) = {
    val policyCheck = PolicyCheck.checkPolicies(accumulator)
    "OK " + policyCheck
  }

  // TODO: Arreglar esto para las sliding windows
  override def merge(a: List[Entity], b: List[Entity]) =
     a ++ b
}


