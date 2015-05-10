package com.xtuone.client.test

import akka.actor.Props
import com.xtuone.client.actor.ClientActor
import com.xtuone.client.scheduler.{ReSendMessageScheduler, HeartbeatScheduler}
import com.xtuone.client.util.{AkkaUtil, MethodHelper, PushUtil, AkkaOps}
import com.xtuone.message.{UnReadNews, TreeholeNewsMsg, Paper, ChatMsg}


/**
 * Created by Zz on 2015/4/8.
 */
object Test extends App{

  val system= AkkaOps.createActorSystem("ClientNodeApp")
  val paper = new ChatMsg("1", 1, new Paper(102,"","","","",1,100000L,"",1))
//  PushUtil.sendPaper( paper )
  MethodHelper.putMessageToCache(paper.confirmId,paper)
  val  news = new TreeholeNewsMsg(10021,new UnReadNews("tttttt",1,2,10000,"cccc"))
  MethodHelper.putMessageToCache(news.confirmId,news)

  val re = new ReSendMessageScheduler

  re.scheduler(AkkaUtil.pushRouter)
//  PushUtil.sendPaper( new ChatMsg("1", 1, new Paper(102,"","","","",1,100000L,"",1)) )

}
