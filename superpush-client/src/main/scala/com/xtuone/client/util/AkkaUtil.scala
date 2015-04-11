package com.xtuone.client.util

import akka.actor.Props
import com.xtuone.client.actor.ClientActor

/**
 * Created by Zz on 2015/4/8.
 */
private [xtuone] object AkkaUtil {

  lazy val clientActor = AkkaOps.actorSystem.actorOf(Props[ClientActor],name = Const.CLIENT_ACTOR_NAME)

}
