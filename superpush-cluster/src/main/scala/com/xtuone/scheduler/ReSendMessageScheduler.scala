package com.xtuone.scheduler

import akka.actor.ActorRef
import com.xtuone.util.{GopushUtil, MethodHelper, Constant, AkkaOps}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
/**
 * Created by Zz on 2015/5/10.
 */
class ReSendMessageScheduler {

  implicit val ec = ExecutionContext.Implicits.global

  def scheduler():Unit= {
    println("ReSendMessageScheduler")
    val system = AkkaOps.getActorSystem()
    system.scheduler.schedule(Constant.RE_SEND_MESSAGE_TIME milliseconds, Constant.RE_SEND_MESSAGE_TIME milliseconds) {
      val map = MethodHelper.MessageCache.asMap()
      val iterator = map.keySet().iterator()
      while(iterator.hasNext){
        val key = iterator.next()
        if(System.currentTimeMillis() - key.toLong >= Constant.RE_SEND_MESSAGE_TIME){
          println("re send message "+key)
          val messageBody = MethodHelper.getMessageToCache(key)
          if(messageBody != null){
            val arr = messageBody.split(Constant.SPLIT_FLAG)
            val students = arr(0)
            val expireTime = arr(1).toLong
            val message = arr(2)
            if(students.contains(",")){
              GopushUtil.pushMoreMessage(message,students,expireTime)
            }else{
              GopushUtil.pushMessage(message,students,expireTime)
            }
            //重新发送后从缓存中去除
            MethodHelper.removeMessageCache(key)
          }
        }
      }
    }

  }
}
