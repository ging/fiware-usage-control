package org.fiware.cosmos.orion.flink.cep.connector

import io.netty.buffer.ByteBufUtil
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.util.CharsetUtil
import org.fiware.cosmos.orion.flink.cep.connector.{ExecutionGraph, Log, NgsiEvent}
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse

import scala.util.matching.Regex

object CEPParser {
  implicit val formats = DefaultFormats

  val ngsiPattern : Regex = ".*?org.fiware.cosmos.orion.flink.connector.OrionHttpHandler *-* *(.*)".r
  val executionGraphPattern : Regex = ".*?org.apache.flink.runtime.executiongraph.ExecutionGraph *-* *(.*) \\(\\d+/\\d+\\).*".r
  val endExecutionGraphPattern : Regex = ".*?org.apache.flink.runtime.jobmaster.JobMaster *-* *(.*)".r
  val jobIdPattern : Regex = ".*?org.apache.flink.runtime.jobmaster.JobManagerRunner *-* *JobManager runner for job .* \\((\\w*)\\) was .*".r
  /**
    * Converts HTTP message into Log
    * @param req FullHttpRequest object
    * @return parsed Log
    */
  final def parseGenericMessage(req : FullHttpRequest) : Log = {
    val headerEntries = req.headers().entries()
    // Retrieve body content and convert from Byte array to String
    val content = req.content()
    val byteBufUtil = ByteBufUtil.readBytes(content.alloc, content, content.readableBytes)
    val jsonBodyString = byteBufUtil.toString(0,content.capacity(),CharsetUtil.US_ASCII)
    content.release()
    parse(jsonBodyString).extract[Log]
  }

  /**
    * Parse log content
    * @param log Received Log
    * @return either a NgsiEvent or an ExecutionGraph Object
    */
  final def parseMessage(log : Log) : Either[NgsiEvent, ExecutionGraph] =  {

    try {
      val msg = log.message
      println(msg)
      msg match {
        case ngsiPattern(ng) =>  {
          val event = parse(ng).extract[NgsiEvent]
          if (!JobId.subscriptionIds.contains(event.subscriptionId) ) {
            JobId.subscriptionIds = JobId.subscriptionIds :+ event.subscriptionId
          }
          Left(event)
        }
        case executionGraphPattern(eg) => Right(new ExecutionGraph(eg))
        case endExecutionGraphPattern(eg) => Right(new ExecutionGraph("END"))
        case jobIdPattern(id) => {println(id);JobId.jobId = id; null}
        case _ => null
      }
    } catch {
      case _: Exception => null
      case _: Error => null
    }
  }
}
