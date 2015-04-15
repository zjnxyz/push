package com.xtuone.client.util

import akka.actor.Props
import com.xtuone.client.actor.{PushRouter, ClientActor}

/**
 * Created by Zz on 2015/4/8.
 */
private [xtuone] object AkkaUtil {

  lazy val clientActor = AkkaOps.getActorSystem().actorOf(Props[ClientActor],name = Const.CLIENT_ACTOR_NAME)

  lazy val pushRouter = AkkaOps.getActorSystem().actorOf(Props[PushRouter],name = "PushRouter")

}
