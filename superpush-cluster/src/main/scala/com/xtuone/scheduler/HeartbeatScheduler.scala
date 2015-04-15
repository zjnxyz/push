package com.xtuone.scheduler

import akka.actor.ActorRef
import com.xtuone.message.Heartbeat
import com.xtuone.util.AkkaOps
import com.xtuone.util.{ Const}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
 * Created by Zz on 2015/4/8.
 */
class HeartbeatScheduler {

  implicit val ec = ExecutionContext.Implicits.global

  def scheduler( worker: ActorRef):Unit={
    val system = AkkaOps.getActorSystem()
    system.scheduler.schedule(1000 milliseconds,20000 milliseconds){
      println("定时任务")
      worker ! new Heartbeat(Const.HOST,Const.PORT,Const.CONNECT_TYPE_WORKER)
    }
  }

}

