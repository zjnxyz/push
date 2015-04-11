package com.xtuone.client.util

import com.google.gson.Gson
import com.xtuone.client.util.PushUtil
import com.xtuone.message._
import java.util

import com.xtuone.model.DatumData


/**
 * Created by Zz on 2015/1/13.
 */
object LocalNodeApplication extends App{

//  val localActor = AkkaUtil.actorSystem.actorSelection("akka.tcp://RemoteNodeApp@192.168.0.225:2552/user/PushDispatcher")
//
//  localActor !  new ChatMsg(1, new Paper(102,"","","","",1,100000L,"",1))

  val chatMsg = new ChatMsg("7355402",2,new Paper(7141703,
    "http://qiniu.myfriday.cn/Avatars/71417031403273092316.jpg?imageView2/1/w/80/h/80/q/60",
    "hehe","7141703","nihaolll",2,1421503346302L,"",0))

  PushUtil.sendPaper(chatMsg)


//  println(AkkaUtil.pathNum)

//  AkkaUtil.createActor(3)

//  PushUtil.sendPaper( new ChatMsg("1",1, new Paper(102,"","","","",1,100000L,"",1)) )
//
//  PushUtil.sendPaper( new ChatMsg("2",1, new Paper(103,"","","","",1,100000L,"",1)) )

  var datumDatas = new util.ArrayList[DatumData]()
//  datumDatas.add(new DatumData("简介","啦啦啦啦啦啦啦啦"))
  val g = new Gson()
//  println(g.toJson(datumDatas))
//
//  val superAccountInfo = new SuperAccountInfo(0,"","club","1056",g.toJson(datumDatas),true,"测试的啦","http://qiniu.myfriday.cn/2_130016_8218729_1413217001461.jpg","http://qiniu.myfriday.cn/2_130016_8218729_1413217001461.jpg")
//
//  val superPushMessageDetail = new SuperPushMessageDetail(0,"club_1","测试scala推送","hello",3,0,0,0,"","","","","",0,101)
//
//  var studentIds = new util.ArrayList[String]()
//  studentIds.add(7141626+"")
//  studentIds.add(7143860+"")
//  studentIds.add(7143858+"")
//  studentIds.add(7141847+"")
//
//  val c = new AccountMessageV2
//  c.setAlert("测试测试")
//  c.setStudentIds(studentIds)
//  c.setSuperAccountInfo(null)
//  c.setSuperPushMessageDetail(superPushMessageDetail)
//  PushUtil.sendAccountMessageV2(c)


//  PushUtil.sendAccountMessage(new AccountMessage("你好",studentIds,superPushMessageDetail,superAccountInfo))



//  PushUtil.sendFeedbackMessage(new FeedbackMessage(7141621,"101,123","----"))





//  val treeholeMessageNews = new TreeholeMessageNews("ccccc",109,"tttttt")
//  val sis = new util.ArrayList[String]()
//  sis.add(123456+"")
//  sis.add(236547+"")
//  PushUtil.sendTreeholeMessage(new TreeholeMessageMsg(sis,treeholeMessageNews))
//
//g.fromJson("",TreeholeMessageMsg.getClass)


//  val publicMessageMsgV2 = new AccountMessageV2()
//  publicMessageMsgV2.setAlert("测试scala推送")
//  publicMessageMsgV2.setStudentIds(studentIds)
//  publicMessageMsgV2.setSuperAccountInfo(superAccountInfo)
//  publicMessageMsgV2.setSuperPushMessageDetail(superPushMessageDetail)
//  PushUtil.sendAccountMessageV2(publicMessageMsgV2)

}
