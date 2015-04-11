package com.xtuone.actor

import akka.actor.{ActorLogging, Actor}
import akka.event.Logging
import com.xtuone.model.Check
import com.xtuone.util.Const

/**
 * Created by Zz on 2015/4/7.
 * 检测活着的worker
 */
class WorkerCheckActor extends Actor with ActorLogging{

  val logBack = Logging(context.system,classOf[ClientCheckActor])

  override def receive: Receive = {

    case Check =>{
      val map = Const.workerMap
      map.foreach{
        case (key,client) =>{
          if(System.currentTimeMillis() - client.addTime > Const.timeout){
            //移除对应主题
            Const.workerMap =  Const.workerMap.-(key)
          }
        }
      }
      logBack.info("workerMap:"+ Const.workerMap)

    }

  }
}
