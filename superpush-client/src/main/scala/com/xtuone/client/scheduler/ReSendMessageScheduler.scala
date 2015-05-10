package com.xtuone.client.scheduler

import akka.actor.ActorRef
import com.xtuone.client.util.{MethodHelper, Const, AkkaOps}
import scala.concurrent.duration._
import collection.JavaConversions._

/**
 * Created by Zz on 2015/5/10.
 * 定时任务，用来重发未成功送达到推送服务器的消息
 */
class ReSendMessageScheduler {

  def scheduler( pushRouter: ActorRef):Unit={

    println("ReSendMessageScheduler")

    val system = AkkaOps.getActorSystem()
    import system.dispatcher
    system.scheduler.schedule( Const.RE_SEND_MESSAGE_TIME milliseconds,Const.RE_SEND_MESSAGE_TIME millisecond){
      val map = MethodHelper.MessageCache.asMap()
      val iterator = map.keySet().iterator()
      while(iterator.hasNext){
        val key = iterator.next()
        if(System.currentTimeMillis() - key.toLong >= Const.RE_SEND_MESSAGE_TIME){
          if(MethodHelper.getMessageToCache(key) != null){
            pushRouter ! MethodHelper.getMessageToCache(key)
          }
        }
      }
    }
  }
}
