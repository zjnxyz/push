package com.xtuone.actor

import akka.actor.Actor.Receive
import akka.actor.{Terminated, Props, Actor, ActorLogging}
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}
import com.xtuone.message.FeedbackMessage
import com.xtuone.util.HttpClientUtils
import org.apache.commons.httpclient.NameValuePair;

/**
 * Created by Zz on 2015/1/16.
 */
class FeedbackActor extends Actor with ActorLogging{

  override def receive: Receive = {
    case feedbackMessage:FeedbackMessage =>{
      //处理反馈信息
      feedbackMessage.messageIds.split(",").foreach{
        case idStr:String =>{
          val idsParam = new NameValuePair( "ids" , idStr )
          val studentParam = new NameValuePair( "studentId" , feedbackMessage.studentId+"")
          val params = new Array[NameValuePair](2)
          params(0) = idsParam
          params(1) = studentParam
         val result =  HttpClientUtils.init().doPost(params, feedbackMessage.callBackUrl)
          if(result == null ||"".equals(result)){
            log.info("反馈失败，请检查原因---"+idStr)
          }
        }
      }
    }
  }

}

class FeedbackRouter extends Actor with ActorLogging{

  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[FeedbackActor])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Actor.Receive = {

    case feedbackMessage:FeedbackMessage =>{
      log.info("反馈收到的消息")
      router.route(feedbackMessage,sender())
    }
    case Terminated(a) =>{
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[FeedbackActor])
      context watch r
      router = router.addRoutee(r)
    }

  }

}
