package com.xtuone.dispatcher

import java.util

import akka.actor._
import akka.event.Logging
import com.xtuone.actor._
import com.xtuone.message._
import org.slf4j.LoggerFactory
import collection.JavaConversions._

/**
 * Created by Zz on 2015/1/13.
 */
class PushDispatcher extends Actor with ActorLogging{

  val logBack = LoggerFactory.getLogger(classOf[PushDispatcher])

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
    context watch paperRouter
    context watch treeholeNewsRouter
    context watch treeholeMessageRouter
    context watch publicMessageRouter
    context watch feedbackRouter
    context watch linkRounter
    context watch purviewRounter
    context watch otherRouter
  }

  override def receive: Receive = {
    case chatMsg:ChatMsg =>{//推送聊天消息
      logBack.info("请求:"+chatMsg.chatIdStr+":"+chatMsg.pager.chatIdStr+":"+chatMsg.pager.contentStr+":"+chatMsg.pager.imageUrlStr+":"+System.currentTimeMillis()+":"+1)
      paperRouter ! chatMsg
      sender() ! new Result(chatMsg.confirmId)
    }
    case treeholeNewsMsg:TreeholeNewsMsg =>{//推送下课聊消息
      logBack.info("请求:"+treeholeNewsMsg.studentId+":"+System.currentTimeMillis()+":"+2)
      treeholeNewsRouter ! treeholeNewsMsg
      sender() ! new Result(treeholeNewsMsg.confirmId)
    }
    case treeholeMessageMsg:TreeholeMessageMsg =>{//推送下课聊主题
      logBack.info("请求:"+treeholeMessageMsg.studentIds+":"+System.currentTimeMillis()+":"+3)
      treeholeMessageRouter ! treeholeMessageMsg
    }

    case publicMessageMsg:PublicMessageMsg =>{//推送公众账号信息
      logBack.info("请求:"+publicMessageMsg.studentIds+":"+System.currentTimeMillis()+":"+4)
      publicMessageRouter ! publicMessageMsg
    }

    case accountMessage:AccountMessage =>{//推送公众账号信息
      logBack.info("请求:"+accountMessage.studentIds+":"+System.currentTimeMillis()+":"+5)
//      val studentIds = new util.ArrayList[Int]()
//      accountMessage.studentIds.foreach{
//        case idString:String => studentIds.add(idString.toInt)
//      }
//       val publicMessageMsg = new PublicMessageMsg(studentIds,accountMessage.alert,
//         accountMessage.superPushMessageDetail,accountMessage.superAccountInfo)
//      publicMessageRouter ! publicMessageMsg
    }

    case feedbackMessage:FeedbackMessage =>{
      logBack.info("请求:"+feedbackMessage.studentId+":"+System.currentTimeMillis()+":"+6)
      sender() ! new Result(feedbackMessage.confirmId)

      feedbackRouter ! feedbackMessage

    }

    case accountMessageV2:AccountMessageV2 =>{
      logBack.info("请求:"+accountMessageV2.studentIds+":"+System.currentTimeMillis()+":"+7)
      sender() ! new Result(accountMessageV2.confirmId)

      val studentIds = new util.ArrayList[Int]()
      accountMessageV2.studentIds.foreach{
        case idString:String => studentIds.add(idString.toInt)
      }
      val publicMessageMsg = new PublicMessageMsg(studentIds,accountMessageV2.alert,
        accountMessageV2.superPushMessageDetail,accountMessageV2.superAccountInfo,accountMessageV2.getConfirmId,accountMessageV2.getExpireTime)
      publicMessageRouter ! publicMessageMsg
    }
    case linkMessageMsg:LinkMessageMsg =>{
      logBack.info("请求:"+linkMessageMsg.studentIds+":"+System.currentTimeMillis()+":"+8)
      sender() ! new Result(linkMessageMsg.confirmId)

      linkRounter ! linkMessageMsg
    }
    case purviewMsg:PurviewMsg =>{
      logBack.info("请求:"+purviewMsg.studentId+":"+System.currentTimeMillis()+":"+9)
      sender() ! new Result(purviewMsg.confirmId)
      purviewRounter ! purviewMsg
    }
    case otherMsg:OtherMsg =>{
      logBack.info("请求:"+otherMsg.studentIds+":"+System.currentTimeMillis()+":"+10)
      sender() ! new Result(otherMsg.confirmId)
      otherRouter ! otherMsg
    }

    case Terminated(a) =>{
      if(a.compareTo(paperRouter) == 0){
        context.stop(paperRouter)
        paperRouter = context.actorOf(Props[PaperRouter])
        context watch paperRouter
      }
      if(a.compareTo(treeholeNewsRouter) == 0){
        context.stop(treeholeNewsRouter)
        treeholeNewsRouter = context.actorOf(Props[TreeholeNewsRouter])
        context watch treeholeNewsRouter
      }

      if(a.compareTo(treeholeMessageRouter) == 0){
        context.stop(treeholeMessageRouter)
        treeholeMessageRouter = context.actorOf(Props[TreeholeMessageRounter])
        context watch treeholeMessageRouter
      }
      if(a.compareTo(publicMessageRouter) == 0){
        context.stop(publicMessageRouter)
        publicMessageRouter = context.actorOf(Props[PublicMessageRouter])
        context watch publicMessageRouter
      }
      if(a.compareTo(feedbackRouter) == 0){
        context.stop(feedbackRouter)
        feedbackRouter = context.actorOf(Props[FeedbackRouter])
        context watch feedbackRouter
      }
      if(a.compareTo(linkRounter) == 0){
        context.stop(linkRounter)
        linkRounter = context.actorOf(Props[LinkRounter])
        context watch linkRounter
      }
      if(a.compareTo(purviewRounter) == 0){
        context.stop(purviewRounter)
        purviewRounter = context.actorOf(Props[PurviewRouter])
        context watch purviewRounter
      }
      if(a.compareTo(otherRouter) == 0){
        context.stop(otherRouter)
        otherRouter = context.actorOf(Props[OtherRouter])
        context watch otherRouter
      }
    }

    case _ =>{
      logBack.info("参数错误")
    }


  }

}
