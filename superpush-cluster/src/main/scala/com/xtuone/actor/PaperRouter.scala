package com.xtuone.actor

import akka.actor.{Terminated, Props, ActorLogging, Actor}
import akka.routing.{ActorRefRoutee, Router, RoundRobinRoutingLogic}
import com.xtuone.message.ChatMsg
import org.slf4j.LoggerFactory

/**
 * Created by Zz on 2015/1/13.
 */
class PaperRouter extends Actor with ActorLogging{
  val logBack = LoggerFactory.getLogger(classOf[PaperRouter])

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
