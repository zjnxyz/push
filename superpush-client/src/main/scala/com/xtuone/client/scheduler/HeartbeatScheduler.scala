package com.xtuone.client.scheduler

import akka.actor.ActorRef
import com.xtuone.client.util.{Const, AkkaOps}
import com.xtuone.message.Heartbeat
import scala.concurrent.duration._

/**
 * Created by Zz on 2015/4/8.
 * 心跳检查，每隔1分钟检查一次
 */
class HeartbeatScheduler {

  def scheduler( clientActor: ActorRef):Unit={
    val system = AkkaOps.getActorSystem()
    import system.dispatcher
    system.scheduler.schedule(1000 milliseconds,1 minutes){
      println("心跳检查")
      clientActor ! new Heartbeat(Const.HOST,Const.PORT,Const.CONNECT_TYPE_CLIENT)
    }
  }
}

