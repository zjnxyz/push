package com.xtuone.actor

import java.util
import akka.actor._
import akka.event.Logging
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}
import com.google.gson.Gson
import com.xtuone.bo.BaseMessageBO
import com.xtuone.message.{UnReadNews, TreeholeNewsMsg}
import com.xtuone.util.model.AnpsMessage
import com.xtuone.util._
import com.xtuone.util.redis.RedisUtil213
import org.slf4j.LoggerFactory

/**
 * Created by Zz on 2015/1/14.
 * 推送下课聊消息
 */
class TreeholeNewsActor extends Actor with ActorLogging{

  var apnsActor:ActorRef =_

  var wpPushActor:ActorRef =_

  val logBack = LoggerFactory.getLogger(classOf[TreeholeNewsActor])
  val g = new Gson()
  //失败标识
  var failureFlag = false
  var count = 0

  @throws(classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    apnsActor = context.actorOf(Props[ApnsTreeholeNewsActor])
    wpPushActor = context.actorOf(Props[WpPushTreeholeNewsActor])
    context watch apnsActor
    context watch wpPushActor
  }

  override def receive: Receive = {

    case treeholeNewsMsg:TreeholeNewsMsg =>{
      val pushMessage = new BaseMessageBO
      pushMessage.setMt(MessageType.TREEHOLE_REPLY)
      pushMessage.setPd(g.toJson(treeholeNewsMsg.unReadNews))

      val result = GopushUtil.pushMessage( g.toJson(pushMessage), MethodHelper.getPushKey(treeholeNewsMsg.studentId), treeholeNewsMsg.expireTime )
      MethodHelper.monitorStatus(result)
      logBack.info("gopush-->result:"+result+": chatId :"+ treeholeNewsMsg.studentId )
      //推送到apns
      apnsActor ! treeholeNewsMsg

      //推送wp
      wpPushActor ! treeholeNewsMsg

    }
    case Terminated(a) =>{
      if(a.compareTo(apnsActor) == 0){
        context.stop(apnsActor)
        apnsActor = context.actorOf(Props[ApnsTreeholeNewsActor])
        context watch apnsActor
      }

      if(a.compareTo(wpPushActor) == 0){
        context.stop(wpPushActor)
        wpPushActor = context.actorOf(Props[WpPushTreeholeNewsActor])
        context watch wpPushActor
      }

    }

  }
}

/**
 * apns推送
 */
class ApnsTreeholeNewsActor extends Actor with ActorLogging{

  var jpushActor:ActorRef =_

  val logBack = LoggerFactory.getLogger(classOf[ApnsTreeholeNewsActor])

  val g = new Gson()


  @throws(classOf[Exception])
  override def preStart(): Unit ={
    super.preStart()
    jpushActor = context.actorOf(Props[JpushTreeholeNewsActor])
    context watch jpushActor
  }

  override def receive: Actor.Receive = {

    case treeholeNewsMsg:TreeholeNewsMsg =>{
      val deviceToken = MethodHelper.findUserDeviceToken(treeholeNewsMsg.studentId+"")
      if(MethodHelper.isNotEmpty(deviceToken)){
        logBack.info("studentId:"+treeholeNewsMsg.studentId+"--deviceToken:"+deviceToken)
        val apnsMessage = new AnpsMessage
        //增加badge数量
        val badge = RedisUtil213.init().incr(Constant.KEY_APNS_NO_READ_NUM+treeholeNewsMsg.studentId)
        apnsMessage.setBadge(badge.toInt)
        //设置弹出内容
        apnsMessage.setAlert(treeholeNewsMsg.unReadNews.t)
        val extras = new util.HashMap[String,String]()
        extras.put("mt",MessageType.TREEHOLE_REPLY+"")
        apnsMessage.setExtras(extras)
        val result = ApnsPushUtil.push(apnsMessage,deviceToken)
        logBack.info("apnspush-->result:"+result+": chatId :"+ treeholeNewsMsg.studentId  )
      }else{
        //推送jpush
        jpushActor ! treeholeNewsMsg
      }
    }
    case Terminated(a) =>{

      if(a.compareTo(jpushActor) == 0){
        context.stop(jpushActor)
        jpushActor = context.actorOf(Props[JpushTreeholeNewsActor])
        context watch jpushActor
      }

    }

  }
}

/**
 * 极光推送
 */
class JpushTreeholeNewsActor extends Actor with ActorLogging{

  val logBack = LoggerFactory.getLogger(classOf[JpushTreeholeNewsActor])

  val g = new Gson()

  override def receive: Actor.Receive = {
    case treeholeNewsMsg:TreeholeNewsMsg =>{

      val aliasName = MethodHelper.getAliasName(treeholeNewsMsg.studentId)
      val pushMessage = new BaseMessageBO
      pushMessage.setMt(MessageType.TREEHOLE_REPLY)
      pushMessage.setPd(g.toJson(treeholeNewsMsg.unReadNews))
      val extras = new util.HashMap[String,String]()
      extras.put("mt",MessageType.TREEHOLE_REPLY+"")
      val result = JpushUtil.pushMessage(g.toJson(pushMessage),treeholeNewsMsg.unReadNews.t,extras,aliasName)
      if(!result){
        //TODO 保存到数据库中
        logBack.info("极光推送失败：(下课聊消息)"+treeholeNewsMsg.studentId)
      }else{
        logBack.info("极光推送成功：(下课聊消息)"+treeholeNewsMsg.studentId)
      }
    }
  }
}

class WpPushTreeholeNewsActor extends Actor with ActorLogging{

  val logBack = LoggerFactory.getLogger(classOf[WpPushTreeholeNewsActor])
  override def receive: Actor.Receive = {
    case treeholeNewsMsg:TreeholeNewsMsg =>{

      val pushUrl = MethodHelper.findWpPushUrl(treeholeNewsMsg.studentId)

      if(MethodHelper.isNotEmpty(pushUrl)){
        //拼推送链接
        val paramStr = WpPushUtil.openView+"?sti="+treeholeNewsMsg.studentId+"&amp;mt="+MessageType.TREEHOLE_REPLY+"&amp;pd="+treeholeNewsMsg.unReadNews.pd+"&amp;cc="+treeholeNewsMsg.unReadNews.cc
        //推送失败后重试3次
        var pushNum = 0
        var flag = true
        while(flag){
          val result = WpPushUtil.push(pushUrl,treeholeNewsMsg.unReadNews.c,treeholeNewsMsg.unReadNews.t,paramStr)
          if(result || pushNum > 3){
            logBack.info("wppush-->result:"+result+": chatId :"+ treeholeNewsMsg.studentId +" :paramStr: " + paramStr)
            flag = false
          }
          pushNum = pushNum + 1
        }
      }
    }
  }

}

/**
 * 下课聊消息路由
 */
class TreeholeNewsRouter extends Actor with ActorLogging{
  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[TreeholeNewsActor])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Actor.Receive = {
    case treeholeNewsMsg:TreeholeNewsMsg =>{
      router.route(treeholeNewsMsg,sender())
    }
    case Terminated(a) =>{
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[TreeholeNewsActor])
      context watch r
      router = router.addRoutee(r)
    }
  }

}