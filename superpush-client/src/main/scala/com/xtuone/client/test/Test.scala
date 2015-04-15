package com.xtuone.client.test

import akka.actor.Props
import com.xtuone.client.actor.ClientActor
import com.xtuone.client.scheduler.HeartbeatScheduler
import com.xtuone.client.util.{PushUtil, AkkaOps}
import com.xtuone.message.{Paper, ChatMsg}


/**
 * Created by Zz on 2015/4/8.
 */
object Test extends App{

  val system= AkkaOps.createActorSystem("ClientNodeApp")
  PushUtil.sendPaper( new ChatMsg("1", 1, new Paper(102,"","","","",1,100000L,"",1)) )
  PushUtil.sendPaper( new ChatMsg("1", 1, new Paper(102,"","","","",1,100000L,"",1)) )

}
