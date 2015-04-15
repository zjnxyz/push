package com.xtuone.client.scheduler

import akka.actor.ActorRef
import com.xtuone.client.util.{Const, AkkaOps}
import com.xtuone.message.Heartbeat
import scala.concurrent.duration._

/**
 * Created by Zz on 2015/4/8.
 */
class HeartbeatScheduler {

  def scheduler( clientActor: ActorRef):Unit={
    val system = AkkaOps.getActorSystem()
    import system.dispatcher
    system.scheduler.schedule(1000 milliseconds,1 minutes){
      println("定时任务")
      clientActor ! new Heartbeat(Const.HOST,Const.PORT,Const.CONNECT_TYPE_CLIENT)
    }
  }
}

