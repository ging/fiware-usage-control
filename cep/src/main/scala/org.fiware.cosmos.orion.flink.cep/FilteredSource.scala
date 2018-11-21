package org.fiware.cosmos.orion.flink.cep

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.fiware.cosmos.orion.flink.connector.{Entity, NgsiEvent, OrionHttpServer}

final class FilteredSource( ) extends RichParallelSourceFunction[Entity] {
  private var sc: SourceContext[Entity] =_
  override def cancel(): Unit = {}

  override def run(ctx: SourceContext[Entity]): Unit = {
    sc = ctx
  }

  def write(logs: List[Entity]): Unit = {
    sc.collect(new Entity("","",null))
  }
}