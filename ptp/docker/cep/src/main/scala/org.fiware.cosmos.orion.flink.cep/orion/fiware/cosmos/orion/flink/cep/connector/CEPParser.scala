package org.fiware.cosmos.orion.flink.cep.orion.fiware.cosmos.orion.flink.cep.connector

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
  val jobIdPattern : Regex = ".*?org.apache.flink.runtime.jobmaster.JobManagerRunner *-* *JobManager runner for job Socket Window NgsiEvent \\((\\w*)\\) .*".r

  final def parseGenericMessage(req : FullHttpRequest) : Log = {
    val headerEntries = req.headers().entries()
    // Retrieve body content and convert from Byte array to String
    val content = req.content()
    val byteBufUtil = ByteBufUtil.readBytes(content.alloc, content, content.readableBytes)
    val jsonBodyString = byteBufUtil.toString(0,content.capacity(),CharsetUtil.US_ASCII)
    content.release()
    parse(jsonBodyString).extract[Log]
  }


  final def parseMessage(log : Log) : Either[NgsiEvent, ExecutionGraph] =  {

    try {
      val msg = log.message

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
        case jobIdPattern(id) => {JobId.jobId = id; null}
        case _ => null
      }
    } catch {
      case _: Exception => null
      case _: Error => null
    }
  }
}
