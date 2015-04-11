package com.xtuone.client.util

import java.util
import java.util.Date
import java.util.concurrent.TimeUnit

import akka.actor.{Props, ActorSystem}
import akka.util.Timeout
import com.google.gson.Gson
import com.typesafe.config.ConfigFactory
import com.xtuone.client.actor.LocalActor
import com.xtuone.client.scheduler.HeartbeatScheduler
import com.xtuone.message._
import com.xtuone.model.{AnnouncementContent, UpgradedVersion, UpgradedContent, DatumData}

import scala.concurrent.ExecutionContext

/**
 * Created by Zz on 2015/1/19.
 */
object Test extends App{



//  AkkaOps.createActorSystem("superPush")
//
//
//  val upgradedContent = new UpgradedContent
//  upgradedContent.setA(1)
//  upgradedContent.setAt(new Date())
//  upgradedContent.setC("有版本升级，快去升级呀")
//  upgradedContent.setDl("http://pic.dbank.com/p07njyzko1.apk")
//  upgradedContent.setIa(true)
//  upgradedContent.setIac(true)
//  upgradedContent.setIr(true)
//  upgradedContent.setP(1)
//  upgradedContent.setVi(101)
//
//  val upgradedVersion = new UpgradedVersion
//  upgradedVersion.setT("超级课程表")
//  upgradedVersion.setC("版本")
//  upgradedVersion.setVb(upgradedContent)
//
//  val g = new Gson()
//
//  val message = new Message(0,MessageType.SOFTWARE_UPDATE,g.toJson(upgradedVersion))
//
//  val studentIds = new util.ArrayList[String]()
//  studentIds.add("12120")
//
//  PushUtil.sendOtherMessage(new OtherMsg(studentIds,"测试版本升级推送",101,message))
//
//val ac = new AnnouncementContent
//  ac.setAi(101)
//  ac.setAt(new Date())
//  ac.setC("软件通知")
//  ac.setP(1)
//  ac.setVi(101)
//  val a = new AnnouncementContent
  

  val system = ActorSystem("LocalNodeApp", ConfigFactory.load().getConfig("LocalSys"))
//
////  val localActor = system.actorOf(Props[LocalActor], name = "localActor")
//
  AkkaOps.setActorSystem(system)
//
//

  val chatMsg = new ChatMsg("7355402",2,new Paper(7141703,
    "http://qiniu.myfriday.cn/Avatars/71417031403273092316.jpg?imageView2/1/w/80/h/80/q/60",
    "hehe","7141703","nihaolll",2,1421503346302L,"",0))

  for( i <- 1 to 100 ){
    PushUtil.sendPaper(chatMsg)
  }


//
//  val purviewMsg = new PurviewMsg(7141621,new Purview(7141621,7,2))
//
//  PushUtil.sendPurviewMessage(purviewMsg)

//  localActor ! chatMsg

  //推送富文本

//  val datumDatas = new util.ArrayList[DatumData]()
//  var datumData = new DatumData
//  datumData.setKey("简介")
//  datumData.setValue("")

}
