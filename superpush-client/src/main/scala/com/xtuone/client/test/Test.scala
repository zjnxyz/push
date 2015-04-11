package com.xtuone.client.test

import akka.actor.Props
import com.xtuone.client.actor.ClientActor
import com.xtuone.client.scheduler.HeartbeatScheduler
import com.xtuone.client.util.AkkaOps


/**
 * Created by Zz on 2015/4/8.
 */
object Test extends App{

  val system= AkkaOps.createActorSystem("ClientNodeApp")

  val clientActor = system.actorOf(Props[ClientActor],name = "client")

  val h = new HeartbeatScheduler

  h.scheduler(clientActor)

}
