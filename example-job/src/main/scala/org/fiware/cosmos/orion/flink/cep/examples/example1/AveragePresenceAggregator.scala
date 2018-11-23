

package org.fiware.cosmos.orion.flink.cep.examples.example1

import java.lang.{Boolean => JBoolean}

import org.apache.flink.api.common.functions.AggregateFunction
import org.fiware.cosmos.orion.flink.connector.Entity

class  AveragePresenceAggregator extends AggregateFunction[Entity, (Float,Float), Float] {
  override def createAccumulator() = (0L,0L)

  override def add(entity: Entity, accumulator:  (Float,Float)) = {
    val presence =  JBoolean.valueOf( entity.attrs("presence").value.asInstanceOf[String])
    val trues = accumulator._1 + (if (presence) 1 else 0)
    val falses = accumulator._2 + (if (!presence) 1 else 0)
    (trues,falses)
  }
  override def getResult(accumulator: (Float,Float)) = {
    val divisor =  accumulator._1 + accumulator._2
    accumulator._1 / (if (divisor == 0 ) 1 else divisor  )
  }

  // TODO: Arreglar esto para las sliding windows
  override def merge(a: (Float,Float), b: (Float,Float)) =
    (a._1 + b._1 , a._2 + b._2)
}


