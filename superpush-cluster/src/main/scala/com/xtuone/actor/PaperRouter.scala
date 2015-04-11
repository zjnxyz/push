package com.xtuone.actor

import akka.actor.{Terminated, Props, ActorLogging, Actor}
import akka.routing.{ActorRefRoutee, Router, RoundRobinRoutingLogic}
import com.xtuone.message.ChatMsg

/**
 * Created by Zz on 2015/1/13.
 */
class PaperRouter extends Actor with ActorLogging{

  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[PaperActor])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Receive = {

    case chatMsg:ChatMsg =>{
      log.info("路由chatMsg")
      router.route(chatMsg,sender())
    }

    case Terminated(a) =>{
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[PaperActor])
      context watch r
      router = router.addRoutee(r)
    }

  }

}
