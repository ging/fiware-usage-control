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
 *
 * Modified by @sonsoleslp
 */
package org.fiware.cosmos.orion.flink.cep.connector
import io.netty.buffer.{ByteBufUtil, Unpooled}
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.handler.codec.http.HttpResponseStatus.{CONTINUE, OK}
import io.netty.handler.codec.http.HttpVersion.HTTP_1_1
import io.netty.handler.codec.http._
import io.netty.util.{AsciiString}
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.fiware.cosmos.orion.flink.cep.orion.fiware.cosmos.orion.flink.cep.connector.{CEPParser}
import org.json4s.DefaultFormats
import org.slf4j.LoggerFactory

import scala.util.matching.Regex

/**
 * HTTP server handler, HTTP http request
 *
 * @param sc       Flink source context for collect received message
 */
class HttpHandler(
  sc: SourceContext[Either[NgsiEvent,ExecutionGraph]]
) extends ChannelInboundHandlerAdapter {

  private lazy val logger = LoggerFactory.getLogger(getClass)
  private lazy val CONTENT_TYPE = new AsciiString("Content-Type")
  private lazy val CONTENT_LENGTH  = new AsciiString("Content-Length")

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = ctx.flush
  implicit val formats = DefaultFormats

  /**
    * Reads the information comming from the HTTP channel
    * @param ctx Flink source context for collect received message
    * @param msg HTTP message
    */
  override def channelRead(ctx: ChannelHandlerContext, msg: AnyRef): Unit = {
    msg match {
      case req : FullHttpRequest =>
        if (req.method() != HttpMethod.POST) {
          throw new Exception("Only POST requests are allowed")
        }

        if (sc != null) {
//          CEPParser.parseGenericMessage(req)
//            .filter(_ != null)
//            .map(CEPParser.parseMessage)
//            .filter(_ != null)
//            .foreach(sc.collect)
          val pgm = CEPParser.parseGenericMessage(req)
          if (pgm != null) {
            val pm = CEPParser.parseMessage(pgm)
            if (pm != null) {
              sc.collect(pm)
            }
          }
        }

        if (HttpUtil.is100ContinueExpected(req)) {
          ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE))
        }

        val keepAlive: Boolean = HttpUtil.isKeepAlive(req)


        // Generate Response
        if (!keepAlive) {
          ctx.writeAndFlush(buildResponse()).addListener(ChannelFutureListener.CLOSE)
        } else {
          ctx.writeAndFlush(buildResponse())
        }

      case x : Any =>
        logger.info("unsupported request format " + x)
    }
  }

  private def buildResponse(content: Array[Byte] = Array.empty[Byte]): FullHttpResponse = {
    val response: FullHttpResponse = new DefaultFullHttpResponse(
      HTTP_1_1, OK, Unpooled.wrappedBuffer(content)
    )
    response.headers.set(CONTENT_TYPE, "text/plain")
    response.headers.setInt(CONTENT_LENGTH, response.content.readableBytes)
    response
  }

  private def buildBadResponse(content: Array[Byte] = Array.empty[Byte]): FullHttpResponse = {
    val response: FullHttpResponse = new DefaultFullHttpResponse(
      HTTP_1_1, OK, Unpooled.wrappedBuffer(content)
    )
    response.headers.set(CONTENT_TYPE, "text/plain")
    response.headers.setInt(CONTENT_LENGTH, response.content.readableBytes)
    response
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    logger.error("channel exception " + ctx.channel().toString, cause)
    ctx.writeAndFlush(buildBadResponse( (cause.getMessage.toString() + "\n").getBytes()))
  }
}
