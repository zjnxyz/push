package com.xtuone.kernel

import akka.actor.Props
import akka.kernel.Bootable
import com.xtuone.actor.{WorkerCheckActor, ClientCheckActor, MasterActor}
import com.xtuone.scheduler.TimerScheduler
import com.xtuone.util.AkkaOps

/**
 * Created by Zz on 2015/4/15.
 */
class MasterKernel extends Bootable {
  val system= AkkaOps.createActorSystem("MasterNodeApp")

  override def startup(): Unit = {

    println("启动Master")
    val master = system.actorOf(Props[MasterActor],name = "Master")

    val clientCheck = system.actorOf(Props[ClientCheckActor],name = "ClientCheck")

    val workerCheck = system.actorOf(Props[WorkerCheckActor],name = "WorkerCheck")

    val timerScheduler = new TimerScheduler

    timerScheduler.scheduler(clientCheck,workerCheck)

  }

  override def shutdown(): Unit = {
    system.shutdown()
  }

}
