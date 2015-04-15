package com.xtuone.actor

import akka.actor.{ActorLogging, Actor}
import akka.event.Logging
import com.xtuone.model.Check
import com.xtuone.util.{MethodHelper, AkkaOps, Const}
import org.slf4j.LoggerFactory

/**
 * Created by Zz on 2015/4/7.
 * 检测活着的worker
 */
class WorkerCheckActor extends Actor with ActorLogging{

  val logBack = LoggerFactory.getLogger(classOf[WorkerCheckActor])

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
      if(Const.workerMap.size < map.size){
        //通知client
        noticeAllClient()
      }
    }

  }

  /**
   * 通知所有的client
   */
  private def noticeAllClient(): Unit ={
    Const.clientMap.foreach{
      case (key,value) =>{
        val clinetUrl = AkkaOps.toAkkaUrl(value.ip,value.port,Const.CLIENT_AKKA_SYSTEM_NAME,Const.CLIENT_ACTOR_NAME)
        logBack.info("通知clinet："+clinetUrl)
        val client = context.actorSelection(clinetUrl.toString)
        client ! MethodHelper.getActiveWorkers()
      }
    }
  }

}
