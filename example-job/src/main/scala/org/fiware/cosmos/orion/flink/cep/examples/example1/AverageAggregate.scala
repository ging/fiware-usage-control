package org.fiware.cosmos.orion.flink.cep.examples.example1


import org.apache.flink.api.common.functions.AggregateFunction
import org.fiware.cosmos.orion.flink.cep.examples.example1.AveragePurchase.SupermarketProduct

private class AverageAggregate extends AggregateFunction[Float, (Double, Double), Double] {
  override def createAccumulator() = (0, 0)
  override def add(expense: Float, accumulator: (Double, Double)) = {
    (accumulator._1 + expense, accumulator._2 + 1L)
  }
  override def getResult(accumulator: (Double, Double)) = accumulator._1 / accumulator._2

  override def merge(a: (Double, Double), b: (Double, Double)) =
    (a._1 + b._1, a._2 + b._2)
}


