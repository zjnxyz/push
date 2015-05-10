package com.xtuone.actor

import java.util

import akka.actor._
import akka.event.Logging
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}
import com.google.gson.Gson
import com.xtuone.bo.BaseMessageBO
import com.xtuone.message.LinkMessageMsg
import com.xtuone.model.LinkMessageBo
import com.xtuone.util.model.AnpsMessage
import com.xtuone.util.redis.RedisUtil213
import com.xtuone.util._
import org.slf4j.LoggerFactory
import collection.JavaConversions._

/**
 * Created by Zz on 2015/1/21.
 */
class LinkActor extends Actor with ActorLogging{

  val g = new Gson()
  val logBack = LoggerFactory.getLogger(classOf[LinkActor])

  var apnsLinkActor: ActorRef =_
  //失败标识
  var failureFlag = false
  var count = 0

  @throws(classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    apnsLinkActor = context.actorOf(Props[ApnsLinkActor])
    context watch apnsLinkActor
  }

  override def receive: Receive = {

    case linkMessageMsg:LinkMessageMsg =>{
      val pushMessage = new BaseMessageBO
      pushMessage.setMt(MessageType.LINK)
      val pd = new LinkMessageBo(linkMessageMsg.linkMessage.url,linkMessageMsg.linkMessage.hasVerification,linkMessageMsg.linkMessage.title,
        linkMessageMsg.linkMessage.context)
      pushMessage.setPd(g.toJson(pd))
      val studentIds = new StringBuilder

      for( studentId <- linkMessageMsg.studentIds){
        studentIds.append(MethodHelper.getPushKey(studentId)).append(",")
      }
      if(studentIds != null && studentIds.size > 0){
        val result = GopushUtil.pushMoreMessage(g.toJson(pushMessage),studentIds.substring(0,studentIds.length-1),linkMessageMsg.expireTime)
        MethodHelper.monitorStatus(result)
        logBack.info("gopush-->result:"+result+": chatId :"+ studentIds +" :message: "+ g.toJson(pushMessage) )
      }
      //推送apns
      apnsLinkActor ! linkMessageMsg
    }
    case Terminated(a) =>{
      apnsLinkActor = context.actorOf(Props[ApnsLinkActor])
      context watch apnsLinkActor
    }
  }
}

class ApnsLinkActor extends Actor with ActorLogging{
  val g = new Gson()
  val logBack = LoggerFactory.getLogger(classOf[ApnsLinkActor])

  override def receive: Actor.Receive = {
    case linkMessageMsg:LinkMessageMsg =>{
      for( studentId <- linkMessageMsg.studentIds){
        val deviceToken = MethodHelper.findUserDeviceToken(studentId+"")
        if(MethodHelper.isNotEmpty(deviceToken)){
          val apnsMessage = new AnpsMessage
          //增加badge数量
          val badge = RedisUtil213.init().incr(Constant.KEY_APNS_NO_READ_NUM+studentId)
          apnsMessage.setBadge(badge.toInt)

          //设置弹出内容
          apnsMessage.setAlert(linkMessageMsg.linkMessage.context)
          val extras = new util.HashMap[String,String]()
          extras.put("mt",MessageType.LINK+"")
          extras.put("u",linkMessageMsg.linkMessage.url)
          extras.put("hv",linkMessageMsg.linkMessage.hasVerification+"")
          apnsMessage.setExtras(extras)
          val result = ApnsPushUtil.push(apnsMessage,deviceToken)
          for( failed<- result.getFailedNotifications){
            logBack.info("failed:"+failed.getException.toString)
          }
          for(success <- result.getSuccessfulNotifications){
            logBack.info("success:"+success.getDevice.getToken)
          }
          logBack.info("apns-->result:"+": chatId :"+ studentId +" :message: "+ g.toJson(apnsMessage) )
        }
      }
    }
  }
}

class LinkRounter extends Actor with ActorLogging{

  val logBack = LoggerFactory.getLogger(classOf[LinkRounter])

  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[LinkActor])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Actor.Receive = {

    case linkMessageMsg:LinkMessageMsg =>{
      logBack.info("推送超链接消息")
      router.route(linkMessageMsg,sender())
    }

    case Terminated(a) =>{
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[LinkActor])
      context watch r
      router = router.addRoutee(r)
    }
  }

}
