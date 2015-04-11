package com.xtuone.scheduler

import akka.actor.ActorRef
import com.xtuone.model.Check
import com.xtuone.util.AkkaOps
import org.slf4j.LoggerFactory
import scala.concurrent.duration._

/**
 * Created by Zz on 2015/4/8.
 */
class TimerScheduler {

  val logBack = LoggerFactory.getLogger(classOf[TimerScheduler])

  /**
   * 定时任务
   * @param clientCheckActor
   * @param workerCheckActor
   */
  def scheduler( clientCheckActor: ActorRef, workerCheckActor: ActorRef):Unit={
    val system = AkkaOps.getActorSystem()
    import system.dispatcher

    system.scheduler.schedule(1000 milliseconds,6000 milliseconds){
      logBack.info("定时检测client")
      clientCheckActor ! Check
    }

    system.scheduler.schedule(1000 milliseconds,6000 milliseconds){
      logBack.info("定时检测worker")
      workerCheckActor ! Check
    }

  }

}
