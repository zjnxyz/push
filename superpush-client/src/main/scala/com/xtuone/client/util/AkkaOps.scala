package com.xtuone.client.util

import java.net.{InetAddress, ServerSocket, InetSocketAddress, Socket}

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import com.xtuone.client.scheduler.HeartbeatScheduler

import scala.beans.BeanProperty

/**
 * Created by Zz on 2015/1/26.
 */
object AkkaOps {

  val akkaThreads   = 4
  val akkaBatchSize = 15
  val akkaTimeout = 100
  var  actorSystem:ActorSystem =_

 def getActorSystem():ActorSystem = {

   if(actorSystem == null){
     actorSystem = ActorSystem(Const.CLIENT_AKKA_SYSTEM_NAME, ConfigFactory.load().getConfig("LocalSys"))
   }
   actorSystem
 }

  def setActorSystem(actorSystem:ActorSystem):Unit={
   this.actorSystem = actorSystem
  }

  def createActorSystem(actorName: String):ActorSystem = {

    val addr = InetAddress.getLocalHost()
    //获取本机的id地址
    val host = addr.getHostAddress
    Const.HOST = host

    var port = 2552
    var flag = true
    while(flag){
      if(isPortAvailable(port)){
        Const.PORT = port
        actorSystem = ActorSystem(Const.CLIENT_AKKA_SYSTEM_NAME,getConfig(host,port))
        flag = false
      }else{
        port = port +1
      }
    }

    //启动心跳检测
    val heartbeat = new HeartbeatScheduler
    heartbeat.scheduler(AkkaUtil.clientActor)

    actorSystem
  }

  private def getConfig(host: String, port: Int):Config ={

    ConfigFactory.parseString(
      s"""
      |akka.loggers = [""akka.event.slf4j.Slf4jLogger""]
      |akka.stdout-loglevel = "INFO"
      |akka.version = "2.3.7"
      |akka.jvm-exit-on-fatal-error = off
      |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      |akka.remote.netty.tcp.transport-class = "akka.remote.transport.netty.NettyTransport"
      |akka.remote.netty.tcp.hostname = "$host"
      |akka.remote.netty.tcp.port = $port
      |akka.remote.netty.tcp.tcp-nodelay = on
      |akka.remote.netty.tcp.connection-timeout = $akkaTimeout s
      |akka.remote.netty.tcp.execution-pool-size = $akkaThreads
      |akka.actor.default-dispatcher.throughput = $akkaBatchSize
      """.stripMargin)

  }

  private def  isPortAvailable( port: Int): Boolean= {
    var flag = false
    try {
      val server = new ServerSocket(port)
      System.out.println("The port is available.")
      server.close()
      flag = true
    } catch {
      case e:Exception =>{
        flag = false
      }
    }
    flag
  }

  /** Returns an `akka.tcp://...` URL**/
  def toAkkaUrl( ip: String, port: Int, akkaSystemName: String,actorName: String): String = {
    "akka.tcp://%s@%s:%s/user/%s".format(akkaSystemName, ip, port, actorName)
  }

}
