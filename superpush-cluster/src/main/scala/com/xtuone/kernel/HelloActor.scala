package com.xtuone.kernel

import akka.actor.{ActorSystem, Props, ActorLogging, Actor}
import akka.event.Logging
import akka.kernel.Bootable
import com.typesafe.config.ConfigFactory
import com.xtuone.dispatcher.PushDispatcher
import com.xtuone.message.{Paper, ChatMsg}
import com.xtuone.util.AkkaOps

/**
 * Created by Zz on 2015/1/4.
 */

case object Start

class HelloActor extends Actor with ActorLogging{

  val worldActor = context.actorOf(Props[WorldActor])

  def receive = {
    case Start => worldActor ! "Hello"
    case message: String =>
      println("Received message '%s'" format message)
  }

}

class WorldActor extends Actor {
  def receive = {
    case message: String => sender() ! (message.toUpperCase + " world!")
  }
}

class HelloKernel extends Bootable {
//  val system = ActorSystem("hellokernel")

//  val system = ActorSystem("RemoteNodeApp", ConfigFactory.load().getConfig("RemoteSys"))
  val system  = AkkaOps.createActorSystem("")

  def startup = {
    println("startup, xxoo")
    val hello =  system.actorOf(Props[PushDispatcher],name = "PushDispatcher" )

//    hello ! new ChatMsg("1", 1, new Paper(102,"","","","",1,100000L,"",1))
  }

  def shutdown = {
    println("shutdown, xxoo")
    system.shutdown()
  }

}
