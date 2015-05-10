package com.xtuone.actor

import java.util

import akka.actor._
import akka.event.Logging
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}
import com.google.gson.Gson
import com.xtuone.bo.BaseMessageBO
import com.xtuone.message.{TreeholeMessageNews, TreeholeMessageMsg}
import com.xtuone.util.model.AnpsMessage
import com.xtuone.util._
import com.xtuone.util.redis.RedisUtil213
import org.slf4j.LoggerFactory
import collection.JavaConversions._

/**
 * Created by Zz on 2015/1/14.
 */
class TreeholeMessageActor extends Actor with ActorLogging{

  val logBack = LoggerFactory.getLogger(classOf[TreeholeMessageActor])

  var apnsTreeholeMessageActor: ActorRef =_

  val g = new Gson()
  //失败标识
  var failureFlag = false
  var count = 0

  @throws(classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    apnsTreeholeMessageActor = context.actorOf(Props[ApnsTreeholeMessageActor])
    context watch apnsTreeholeMessageActor
  }

  override def receive: Receive = {
    case treeholeMessageMsg:TreeholeMessageMsg =>{
      //先用gopush推送
      val pushMessage = new BaseMessageBO
      pushMessage.setMt(MessageType.TREEHOLE_MESSAGE)
      pushMessage.setPd(g.toJson(treeholeMessageMsg.treeholeMessageNews))

      //处理加密后的学生id
      val studentIds = new StringBuilder
      for( studentId <- treeholeMessageMsg.studentIds){
        studentIds.append(MethodHelper.getPushKey(studentId)).append(",")
      }
      if(studentIds != null && studentIds.size > 0){

        val result = GopushUtil.pushMoreMessage( g.toJson(pushMessage), studentIds.substring(0,studentIds.length-1), treeholeMessageMsg.expireTime)
        MethodHelper.monitorStatus(result)
        logBack.info("gopush-->result:"+result+": chatId :"+ studentIds +" :message: "+ g.toJson(pushMessage) )
      }
      //推送apns
      apnsTreeholeMessageActor ! treeholeMessageMsg

    }
    case Terminated(a) =>{
      apnsTreeholeMessageActor = context.actorOf(Props[ApnsTreeholeNewsActor])
      context watch apnsTreeholeMessageActor
    }

  }
}

/**
 * apns推送
 */
class ApnsTreeholeMessageActor extends Actor with ActorLogging{

  val logBack = LoggerFactory.getLogger(classOf[ApnsTreeholeMessageActor])
  val g = new Gson()
  var jpushTreeholeMessageActor: ActorRef =_


  @throws(classOf[Exception])
  override def preStart(): Unit ={
    super.preStart()
    jpushTreeholeMessageActor = context.actorOf(Props[JpushTreeholeMessageActor])
    context watch jpushTreeholeMessageActor
  }

  override def receive: Actor.Receive = {
    case treeholeMessageMsg:TreeholeMessageMsg =>{
      val jpushStudentIds = new util.ArrayList[String]()
      for( studentId <- treeholeMessageMsg.studentIds){
        val deviceToken = MethodHelper.findUserDeviceToken(studentId+"")
        if(MethodHelper.isNotEmpty(deviceToken)){
          val apnsMessage = new AnpsMessage
          //增加badge数量
          val badge = RedisUtil213.init().incr(Constant.KEY_APNS_NO_READ_NUM+studentId)
          apnsMessage.setBadge(badge.toInt)
          //设置弹出内容
          apnsMessage.setAlert(treeholeMessageMsg.treeholeMessageNews.c)
          val extras = new util.HashMap[String,String]()
          extras.put("mt",MessageType.TREEHOLE_MESSAGE+"")
          extras.put("id",treeholeMessageMsg.treeholeMessageNews.mi+"")
          apnsMessage.setExtras(extras)
          val result = ApnsPushUtil.push(apnsMessage,deviceToken)
          logBack.info("apns-->result:"+result+": chatId :"+ studentId +" :message: "+ g.toJson(apnsMessage) )

        }else{
          //推送极光
          jpushStudentIds.add(studentId)
        }
      }

      if(jpushStudentIds.size() > 0){
        val jpush = new TreeholeMessageMsg(jpushStudentIds,treeholeMessageMsg.treeholeMessageNews,treeholeMessageMsg.confirmId,treeholeMessageMsg.expireTime)
        jpushTreeholeMessageActor ! jpush
      }

    }
    case Terminated(a) =>{
      jpushTreeholeMessageActor = context.actorOf(Props[JpushTreeholeMessageActor])
      context watch jpushTreeholeMessageActor
    }

  }
}

/**
 * 推送到极光
 */
class JpushTreeholeMessageActor extends Actor with ActorLogging{

  val logBack = LoggerFactory.getLogger(classOf[JpushTreeholeMessageActor])

  override def receive: Actor.Receive = {
    case treeholeMessageMsg:TreeholeMessageMsg =>{
      val aliasNames = new util.ArrayList[String]()
      for( studentId <- treeholeMessageMsg.studentIds){
        aliasNames.add(MethodHelper.getAliasName(studentId))
      }

      if(aliasNames.size() > 0){
        val g = new Gson()
        val pushMessage = new BaseMessageBO
        pushMessage.setMt(MessageType.TREEHOLE_MESSAGE)
        pushMessage.setPd(g.toJson(treeholeMessageMsg.treeholeMessageNews))
        val extras = new util.HashMap[String,String]()
        extras.put("mt",MessageType.TREEHOLE_MESSAGE+"")
        val result =  JpushUtil.pushMoreMessage(g.toJson(pushMessage),treeholeMessageMsg.treeholeMessageNews.c,extras,aliasNames)
        if(!result){
          //TODO 推送失败保存到数据库
          logBack.info("推送到极光失败（下课聊主题）："+aliasNames)
        }
      }

    }

  }

}

/**
 * 下课聊主题路由
 */
class TreeholeMessageRounter extends Actor with ActorLogging{

  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[TreeholeMessageActor])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Actor.Receive = {
    case treeholeMessageMsg:TreeholeMessageMsg => {
      router.route(treeholeMessageMsg,sender())
    }
    case Terminated(a) =>{
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[TreeholeMessageActor])
      context watch r
      router = router.addRoutee(r)
    }

  }
}

