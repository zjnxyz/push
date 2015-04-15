package com.xtuone.test

import java.util

import akka.actor.{Props, ActorSystem}
import com.google.gson.Gson
import com.typesafe.config.ConfigFactory
import com.xtuone.dispatcher.PushDispatcher
import com.xtuone.message._
import com.xtuone.model.DatumData
import com.xtuone.util._

/**
 * Created by Zz on 2015/1/12.
 */
object Test extends App{

  val system  = AkkaOps.createActorSystem("")
  val hello =  system.actorOf(Props[PushDispatcher],name = "PushDispatcher" )

//  println(MD5.getMD5("1279657"+Constant.gopushKey))

//  SmsUtil.sendTimeoutMsg()

//  println(ConfigFactory.load().getString("gopush.one"))
//  println(ConfigFactory.load().getString("gopush.m"))
//  println(ConfigFactory.load().getInt("gopush.expire"))

//  println(MethodHelper.getPushKey(7141621))
//  val g = new Gson()
//  var datumDatas = new util.ArrayList[DatumData]()
//  datumDatas.add(new DatumData("简介","超级课程表系统通知，请勿直接回复。使用过程中的问题及反馈请联系表表客服。"))
//  var studentIds = new util.ArrayList[String]()
//  studentIds.add(7142264+"")
//  studentIds.add(7141626+"")
//
//  val system = ActorSystem("RemoteNodeApp", ConfigFactory.load().getConfig("RemoteSys"))
//
//  val hello =  system.actorOf(Props[PushDispatcher],name = "PushDispatcher" )
  //推送链接
//  val msg = new LinkMessageMsg(studentIds,new LinkMessage("http://www.super.cn/level/index.html?identity=",1,"完成超级实习生任务送波多野结衣","你有新任务啦，快去查看，完成任务可以获得奖品哦！"))
//  val msg = new LinkMessageMsg(studentIds,new LinkMessage("http://www.super.cn",0,"完成超级实习生任务送波多野结衣","你有新任务啦，快去查看，完成任务可以获得奖品哦！"))
//  hello ! msg

//  val chatMsg = new ChatMsg("7141624",2,new Paper(7141703,
//    "http://qiniu.myfriday.cn/Avatars/71417031403273092316.jpg?imageView2/1/w/80/h/80/q/60",
//    "hehe","7141703","nihaolll",2,1421503346302L,"",0))
//
//  hello ! chatMsg
//
//  val superAccountInfo = new SuperAccountInfo(0,"super_1","super","1",g.toJson(datumDatas),true,"系统通知","http://qiniu.myfriday.cn/xiaozitiao/publicAccount/admin_02.png","")
////
//  val superPushMessageDetail = new SuperPushMessageDetail(0,"super_1","测试scala推送","title",3,0,0,0,"","","","","",0,0)
////
//
//  val accountMessageV2 = new AccountMessageV2()
//  accountMessageV2.setAlert("通知测试")
//  accountMessageV2.setStudentIds(studentIds)
//  accountMessageV2.setSuperAccountInfo(superAccountInfo)
//  accountMessageV2.setSuperPushMessageDetail(superPushMessageDetail)
//  hello ! accountMessageV2
//
//  val publicMessageMsgV2 = new AccountMessageV2()
//  publicMessageMsgV2.setAlert("测试scala推送")
//  publicMessageMsgV2.setStudentIds(studentIds)
//  publicMessageMsgV2.setSuperAccountInfo(superAccountInfo)
//  publicMessageMsgV2.setSuperPushMessageDetail(superPushMessageDetail)
//  hello ! publicMessageMsgV2

//  val publicMessageMsg = new PublicMessageMsg(studentIds,"测试scala推送",superPushMessageDetail,superAccountInfo)

//  hello ! publicMessageMsg

//  hello ! new FeedbackMessage(7142264,"101,123","http://www.baidu.com")

//  HttpClientUtils.init().doGet("http://www.baidu.com")
}
