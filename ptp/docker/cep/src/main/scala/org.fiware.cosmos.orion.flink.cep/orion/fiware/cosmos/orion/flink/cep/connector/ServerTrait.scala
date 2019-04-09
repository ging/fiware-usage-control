package org.fiware.cosmos.orion.flink.cep.connector
import java.io.Closeable
import java.net.{InetSocketAddress, URLEncoder}

/**
 * Server trait for define server behave.
 * Port and callback url is required.
 */
trait ServerTrait extends Closeable {

  def start(tryPort: Int, callbackUrl: Option[String]): InetSocketAddress = {
    NettyUtil.startServiceOnPort(tryPort, (p: Int) => startNettyServer(p, callbackUrl))
  }

  def startNettyServer(portNotInUse: Int, callbackUrl: Option[String]): InetSocketAddress

  def register(address: InetSocketAddress, callbackUrl: Option[String]): Unit = {
    callbackUrl match {
      case Some(url) =>
        val ip = address.getAddress.getHostAddress
        val newIp = if (ip.startsWith("0") || ip.startsWith("127")) {
          NettyUtil.findLocalInetAddress().getHostAddress
        } else {
          ip
        }
        val port = address.getPort
        val param = s"ip=${URLEncoder.encode(newIp, "UTF-8")}&port=$port"
        val callUrl = if (url.endsWith("?")) param else "?" + param
        NettyUtil.sendGetRequest(url + callUrl)
      case _ =>
    }
  }
}
