package com.xtuone.dispatcher

import java.util

import akka.actor.{Props, ActorRef, ActorLogging, Actor}
import akka.event.Logging
import com.xtuone.actor._
import com.xtuone.message._
import collection.JavaConversions._

/**
 * Created by Zz on 2015/1/13.
 */
class PushDispatcher extends Actor with ActorLogging{

  val logBack = Logging(context.system,classOf[PushDispatcher])

  var paperRouter:ActorRef =_

  var treeholeNewsRouter:ActorRef =_

  var treeholeMessageRouter:ActorRef =_

  var publicMessageRouter:ActorRef =_

  var feedbackRouter:ActorRef =_

  var linkRounter:ActorRef =_

  var purviewRounter:ActorRef =_

  var otherRouter:ActorRef =_

  @throws(classOf[Exception])
  override def preStart(): Unit ={
    super.preStart()
    paperRouter = context.actorOf(Props[PaperRouter], name = "paperRouter")
    treeholeNewsRouter = context.actorOf(Props[TreeholeNewsRouter], name = "treeholeNewsRouter")
    treeholeMessageRouter = context.actorOf(Props[TreeholeMessageRounter], name = "treeholeMessageRounter")
    publicMessageRouter = context.actorOf(Props[PublicMessageRouter], name = "publicMessageRouter")
    feedbackRouter = context.actorOf(Props[FeedbackRouter], name = "feedbackRouter")
    linkRounter = context.actorOf(Props[LinkRounter], name = "linkRounter")
    purviewRounter = context.actorOf(Props[PurviewRouter], name = "purviewRounter")
    otherRouter = context.actorOf(Props[OtherRouter], name = "otherRouter")
  }

  override def receive: Receive = {
    case chatMsg:ChatMsg =>{//推送聊天消息
//      logBack.debug("聊天信息")
//      logBack.info("ChatMsg:"+chatMsg.chatIdStr+":"+chatMsg.pager.contentStr)
      paperRouter ! chatMsg
    }
    case treeholeNewsMsg:TreeholeNewsMsg =>{//推送下课聊消息
//      logBack.debug("推送下课聊消息")
      logBack.info("TreeholeNewsMsg:"+treeholeNewsMsg.studentId+":"+treeholeNewsMsg.unReadNews.c)
      treeholeNewsRouter ! treeholeNewsMsg
    }
    case treeholeMessageMsg:TreeholeMessageMsg =>{//推送下课聊主题
//      logBack.debug("推送下课聊主题")
      logBack.info("TreeholeMessageMsg:"+treeholeMessageMsg.studentIds+":"+treeholeMessageMsg.treeholeMessageNews.t)
      treeholeMessageRouter ! treeholeMessageMsg
    }

    case publicMessageMsg:PublicMessageMsg =>{//推送公众账号信息
      logBack.debug("推送公众账号信息")
      publicMessageRouter ! publicMessageMsg
    }

    case accountMessage:AccountMessage =>{//推送公众账号信息
      logBack.debug("推送公众账号信息2")
      val studentIds = new util.ArrayList[Int]()
      accountMessage.studentIds.foreach{
        case idString:String => studentIds.add(idString.toInt)
      }
       val publicMessageMsg = new PublicMessageMsg(studentIds,accountMessage.alert,
         accountMessage.superPushMessageDetail,accountMessage.superAccountInfo)
      publicMessageRouter ! publicMessageMsg
    }

    case feedbackMessage:FeedbackMessage =>{
      logBack.debug("feedbackMessage")
      feedbackRouter ! feedbackMessage
    }

    case accountMessageV2:AccountMessageV2 =>{
      logBack.debug("accountMessageV2")
      val studentIds = new util.ArrayList[Int]()
      accountMessageV2.studentIds.foreach{
        case idString:String => studentIds.add(idString.toInt)
      }
      val publicMessageMsg = new PublicMessageMsg(studentIds,accountMessageV2.alert,
        accountMessageV2.superPushMessageDetail,accountMessageV2.superAccountInfo)
      publicMessageRouter ! publicMessageMsg
    }
    case linkMessageMsg:LinkMessageMsg =>{
      logBack.debug("linkMessageMsg")
      linkRounter ! linkMessageMsg
    }
    case purviewMsg:PurviewMsg =>{
      purviewRounter ! purviewMsg
    }
    case otherMsg:OtherMsg =>{
      otherRouter ! otherMsg
    }
    case _ =>{
      logBack.info("参数错误")
    }


  }

}
