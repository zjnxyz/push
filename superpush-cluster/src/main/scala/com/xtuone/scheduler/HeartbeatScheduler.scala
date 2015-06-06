package com.xtuone.scheduler

import akka.actor.ActorRef
import com.xtuone.message.Heartbeat
import com.xtuone.util.AkkaOps
import com.xtuone.util.{ Const}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
 * Created by Zz on 2015/4/8.
 * 像master发送一个心跳
 * 心跳频率是5分钟一次
 */
class HeartbeatScheduler {

  implicit val ec = ExecutionContext.Implicits.global

  def scheduler( worker: ActorRef):Unit={
    val system = AkkaOps.getActorSystem()
    system.scheduler.schedule(1000 milliseconds, 1 minutes){
      worker ! new Heartbeat(Const.HOST,Const.PORT,Const.CONNECT_TYPE_WORKER)
    }
  }

}

