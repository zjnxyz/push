package com.xtuone.test

import akka.actor.Props
import com.xtuone.actor.{WorkerCheckActor, ClientCheckActor, MasterActor}
import com.xtuone.scheduler.TimerScheduler
import com.xtuone.util.AkkaOps

/**
 * Created by Zz on 2015/4/8.
 */
object Test extends App {

  val system= AkkaOps.createActorSystem("MasterNodeApp")

  val master = system.actorOf(Props[MasterActor],name = "Master")

  val clientCheck = system.actorOf(Props[ClientCheckActor],name = "ClientCheck")

  val workerCheck = system.actorOf(Props[WorkerCheckActor],name = "WorkerCheck")

  val timerScheduler = new TimerScheduler

  timerScheduler.scheduler(clientCheck,workerCheck)


}
