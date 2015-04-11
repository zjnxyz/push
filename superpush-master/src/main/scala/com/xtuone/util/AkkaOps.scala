package com.xtuone.util

import java.net.{InetAddress, ServerSocket}

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

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
     actorSystem = ActorSystem("LocalNodeApp", ConfigFactory.load().getConfig("LocalSys"))
   }
   actorSystem
 }

  def setActorSystem(actorSystem:ActorSystem):Unit={
   this.actorSystem = actorSystem
  }

  def createActorSystem(actorName: String):ActorSystem = {

    //获取本机的id地址
    val host = ConfigFactory.load().getString("host")
    Const.HOST = host
    val port = ConfigFactory.load().getInt("port")
    Const.PORT = port

    actorSystem = ActorSystem("MasterNodeApp",getConfig(host,port))

    actorSystem
  }

  private def getConfig(host: String, port: Int):Config ={

    ConfigFactory.parseString(
      s"""
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

  private def isPortAvailable( port: Int): Boolean= {
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
