package com.xtuone.actor

import akka.actor.{Terminated, Props, Actor, ActorLogging}
import akka.event.Logging
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}
import com.google.gson.Gson
import com.xtuone.bo.BaseMessageBO
import com.xtuone.message.PurviewMsg
import com.xtuone.util.{MethodHelper, GopushUtil, MessageType}

/**
 * Created by Zz on 2015/1/28.
 */
class PurviewActor  extends Actor with ActorLogging{

  val g = new Gson()
  val logBack = Logging(context.system,classOf[PurviewActor])

  override def receive: Receive = {

    case purviewMsg: PurviewMsg =>{
      val pushMessage = new BaseMessageBO
      pushMessage.setMt(MessageType.PURVIEW)
      pushMessage.setPd(g.toJson(purviewMsg.purview))
      var flag = true
      var i = 0
      while(flag){
        //推送到gopush
        val result = GopushUtil.pushMoreMessage(g.toJson(pushMessage),MethodHelper.getPushKey(purviewMsg.studentId))
        MethodHelper.monitorStatus(result)
        if(result || i > 3){
          flag = false
          logBack.info("gopush-->result:"+result+": chatId :"+ purviewMsg.studentId +" :message: "+ g.toJson(pushMessage) )
        }
        i = i + 1
      }
    }
  }
}

/**
 * 权限PurviewRouter
 */
class PurviewRouter extends Actor with ActorLogging{

  val logBack = Logging(context.system,classOf[PurviewRouter])

  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[PurviewActor])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }
  override def receive: Actor.Receive = {
    case purviewMsg: PurviewMsg =>{
      logBack.info("权限推送")
      router.route(purviewMsg,sender())
    }
    case Terminated(a) =>{
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[PurviewRouter])
      context watch r
      router = router.addRoutee(r)
    }
  }
}



