package com.xtuone.actor

import java.util

import akka.actor.Actor.Receive
import akka.actor._
import akka.event.Logging
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}
import com.google.gson.Gson
import com.xtuone.bo.BaseMessageBO
import com.xtuone.message.{OtherMsg, PublicMessageMsg}
import com.xtuone.util.model.AnpsMessage
import com.xtuone.util.redis.RedisUtil213
import com.xtuone.util._
import org.slf4j.LoggerFactory
import collection.JavaConversions._

/**
 * Created by Zz on 2015/2/15.
 */
class OtherActor  extends Actor with ActorLogging{

  val logBack = LoggerFactory.getLogger(classOf[OtherActor])
  val g = new Gson()
  //失败标识
  var failureFlag = false
  var count = 0

  var apnsOtherActor: ActorRef =_

  @throws(classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    apnsOtherActor = context.actorOf(Props[ApnsOtherActor])
    context watch apnsOtherActor
  }

  override def receive: Receive = {

    case otherMsg:OtherMsg => {
      val pushMessage = new BaseMessageBO
      pushMessage.setMt(otherMsg.message.mt)
      pushMessage.setSi(otherMsg.message.si)
      pushMessage.setPd(otherMsg.message.pd)

      //批量推送给gopush
      val studentIds = new StringBuilder
      for( studentId <- otherMsg.studentIds){
        studentIds.append(MethodHelper.getPushKey(studentId)).append(",")
      }
      //推送apns
      apnsOtherActor ! otherMsg

      if(studentIds != null && studentIds.size > 0){
        val result = GopushUtil.pushMoreMessage(g.toJson(pushMessage), studentIds.substring(0,studentIds.length-1), otherMsg.expireTime)
        MethodHelper.monitorStatus(result)
        logBack.info("gopush-->result:"+result+": chatId :"+studentIds+" message:"+ g.toJson(pushMessage))
      }
    }
    case Terminated(a) =>{
      if(a.compareTo(apnsOtherActor) == 0){
        context.stop(apnsOtherActor)
        apnsOtherActor = context.actorOf(Props[ApnsOtherActor])
        context watch apnsOtherActor
        logBack.info(" restart apnsOtherActor ")
      }

    }

  }
}

/**
 * 推送apns
 */
class ApnsOtherActor extends Actor with ActorLogging{

  val logBack = LoggerFactory.getLogger(classOf[ApnsOtherActor])
  var jpushOtherActor: ActorRef =_

  @throws(classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    jpushOtherActor = context.actorOf(Props[JpushOtherActor])
    context watch jpushOtherActor
  }

  override def receive: Actor.Receive = {
    case otherMsg:OtherMsg => {

      val jpushStudentIds = new util.ArrayList[String]()
      //推送到 apns
      for( studentId <- otherMsg.studentIds) {
        val deviceToken = MethodHelper.findUserDeviceToken(studentId)
        if (MethodHelper.isNotEmpty(deviceToken)) {
          val apnsMessage = new AnpsMessage
          //设置弹出内容
          apnsMessage.setAlert(otherMsg.alert)
          //增加badge数量
          val badge = RedisUtil213.init().incr(Constant.KEY_APNS_NO_READ_NUM+studentId)
          apnsMessage.setBadge(badge.toInt)

          val extras = new util.HashMap[String,String]()
          extras.put("mt",otherMsg.message.mt.toString)
          extras.put("id",otherMsg.oId.toString)
          apnsMessage.setExtras(extras)
          val result = ApnsPushUtil.push(apnsMessage,deviceToken)
          logBack.info("apns-->result:"+result+": revId :"+ studentId )
        }else{
          jpushStudentIds.add(studentId)
        }
      }

      if(jpushStudentIds.size() > 0){
        val jpushOtherMsg = new OtherMsg(jpushStudentIds,otherMsg.alert,otherMsg.oId,otherMsg.message, otherMsg.confirmId, otherMsg.expireTime)
        jpushOtherActor ! jpushOtherMsg
      }

    }

    case Terminated(a) =>{
      if(a.compareTo(jpushOtherActor) == 0){
        logBack.info(" restart jpushOtherActor")
        context.stop(jpushOtherActor)
        jpushOtherActor = context.actorOf(Props[JpushOtherActor])
        context watch jpushOtherActor
      }
    }

  }
}

class JpushOtherActor extends Actor with ActorLogging{

  val logBack = LoggerFactory.getLogger(classOf[JpushOtherActor])

  val g = new Gson()

  override def receive: Actor.Receive = {
    case otherMsg:OtherMsg =>{
      val pushMessage = new BaseMessageBO
      pushMessage.setMt(otherMsg.message.mt)
      pushMessage.setPd(otherMsg.message.pd)

      val extras = new util.HashMap[String,String]()
      extras.put("mt",otherMsg.message.mt.toString)
      extras.put("id",otherMsg.oId.toString)

      val aliasNames = new util.ArrayList[String]()
      for( studentId <- otherMsg.studentIds){
        aliasNames.add(MethodHelper.getAliasName(studentId))
      }
      if(aliasNames.size() > 0){
        val result = JpushUtil.pushMoreMessage(g.toJson(pushMessage),
          otherMsg.alert,extras,aliasNames)
        logBack.info("m:"+g.toJson(pushMessage))
        if(!result){
          //TODO 错误保存到Mysql
          logBack.info("极光失败了-----"+otherMsg.studentIds.toString)
        }else{
          logBack.info("极光成功了-----")
        }
      }
    }
  }

}

class OtherRouter extends Actor with ActorLogging{
  val logBack = LoggerFactory.getLogger(classOf[OtherRouter])
  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[OtherActor])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Actor.Receive = {
    case  otherMsg:OtherMsg =>{
      router.route(otherMsg,sender())
    }
    case Terminated(a) =>{
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[OtherActor])
      context watch r
      router = router.addRoutee(r)
    }

  }
}
