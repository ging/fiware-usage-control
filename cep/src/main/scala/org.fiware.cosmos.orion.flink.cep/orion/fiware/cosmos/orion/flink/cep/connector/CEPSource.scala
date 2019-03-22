/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fiware.cosmos.orion.flink.cep.connector
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext

/**
  *
 * Http receiver source is used for receiving pushed http request.
 * {{{
 *   // for example:
 *   val env = StreamExecutionEnvironment.getExecutionEnvironment
 *   env.addSource(new CEPSource(7070))
 * }}}
 * @author @sonsoleslp
 * @param tryPort     try to use this point, if this point is used then try a new port
 */
final class CEPSource(
  tryPort: Int
) extends RichParallelSourceFunction[Either[NgsiEvent,ExecutionGraph]] {
  private var server: HttpServer = _

  override def cancel(): Unit = server.close()

  override def run(ctx: SourceContext[Either[NgsiEvent,ExecutionGraph]]): Unit = {
    server = new HttpServer(ctx)
    server.start(tryPort, None)
  }
}
