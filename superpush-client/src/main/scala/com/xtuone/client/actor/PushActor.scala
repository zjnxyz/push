package com.xtuone.client.actor

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}
import akka.util.Timeout
import com.xtuone.client.util.{MethodHelper, AkkaOps, Const}
import com.xtuone.message._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Created by Zz on 2015/4/15.
 */
class PushActor  extends Actor with ActorLogging{

  implicit val askTimeout = Timeout(60,TimeUnit.SECONDS)

  implicit val ec = ExecutionContext.Implicits.global

  var worker: ActorSelection = null

  override def receive: Receive = {
    case chatMsg: ChatMsg =>{
      sendMessageToWorker(chatMsg)
      println("W统计 "+chatMsg.chatIdStr+" "+chatMsg.confirmId+" "+chatMsg.pager.contentStr+" "+chatMsg.pager.sendTimeLong )
//      MethodHelper.putMessageToCache(chatMsg.confirmId, chatMsg )
    }

    case treeholeNewsMsg: TreeholeNewsMsg =>{
      sendMessageToWorker(treeholeNewsMsg)
      println("W统计 "+ treeholeNewsMsg.studentId+" " + treeholeNewsMsg.confirmId)
//      MethodHelper.putMessageToCache(treeholeNewsMsg.confirmId, treeholeNewsMsg )
    }

    case treeholeMessageMsg: TreeholeMessageMsg =>{
      sendMessageToWorker(treeholeMessageMsg)
      println("W统计 " + treeholeMessageMsg.confirmId)

//      MethodHelper.putMessageToCache(treeholeMessageMsg.confirmId, treeholeMessageMsg )
    }

    case accountMessage: AccountMessage =>{
      //公众账号
      sendMessageToWorker(accountMessage)
    }

    case accountMessageV2: AccountMessageV2 =>{
      println("accountMessageV2")
      //公众账号(正在使用)
      sendMessageToWorker(accountMessageV2)
//      MethodHelper.putMessageToCache(accountMessageV2.confirmId, accountMessageV2 )
    }

    case feedbackMessage:FeedbackMessage =>{
      println("feedbackMessage")
      //反馈
      sendMessageToWorker(feedbackMessage)
//      MethodHelper.putMessageToCache(feedbackMessage.confirmId, feedbackMessage )
    }

    case purviewMsg: PurviewMsg =>{
      println("purviewMsg")
      //权限
      sendMessageToWorker(purviewMsg)
//      MethodHelper.putMessageToCache(purviewMsg.confirmId, purviewMsg )
    }

    case otherMsg: OtherMsg =>{
      //其他
      sendMessageToWorker(otherMsg)
//      MethodHelper.putMessageToCache(otherMsg.confirmId, otherMsg )
    }

    case result: Result =>{
      //worker收到消息后反馈给client
      println(" feedback result:"+result.key)
      MethodHelper.removeMessageCache(result.key)
    }

    case anyRef:AnyRef =>{
      println(" W统计 重试推送")
      sendMessageToWorker(anyRef)
    }

  }

  @throws(classOf[Exception])
  override def preStart(): Unit = super.preStart()


  /**
   * 发送信息到置顶worker
   * @param msg
   */
  def sendMessageToWorker(msg:AnyRef):Unit={

    if( Const.workerUrls.size == 0){
      Thread.sleep(10000)
    }

    val num = System.currentTimeMillis() %  Const.workerUrls.size
    trySendMessageToWorker(0,num.toInt,msg)
  }

  private [xtuone] def trySendMessageToWorker(counter:Int, num: Int,  msg:AnyRef):Unit={
    val workerSize = Const.workerUrls.size
    val count = counter+1

    if( workerSize == 0 && count < 10){
      //再次去获取可用的Workers
      context.self ! GetWorkers

    }else{

      if(count > 10){
        throw new Exception("没有可用的worker")
      }

      var i = num
      if(i > workerSize){
        i = 0
      }

      if(worker == null){
        val workerUrls = Const.workerUrls.take(i+1)
        println("workerUrls:"+workerUrls)
        worker =  AkkaOps.getActorSystem().actorSelection(workerUrls.last)
      }

      val localFuture = worker resolveOne()
      localFuture.onComplete{
        case Success(actor) => {
          actor ! msg
          println("~~~"+i)
        }
        case Failure(ex) =>{
          trySendMessageToWorker(count, i+1, msg)
        }
      }
    }
  }

}

class PushRouter extends Actor with ActorLogging{

  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[PushActor])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Actor.Receive = {
    case chatMsg: ChatMsg =>{
      println("ch----->")
      router.route(chatMsg,sender())
    }
    case treeholeNewsMsg: TreeholeNewsMsg =>{
      println("treeholeNewsMsg-->")
      router.route(treeholeNewsMsg,sender())
    }

    case treeholeMessageMsg: TreeholeMessageMsg =>{
      router.route(treeholeMessageMsg,sender())
    }

    case accountMessage: AccountMessage =>{
      //公众账号
      router.route(accountMessage,sender())
    }

    case accountMessageV2: AccountMessageV2 =>{
      //公众账号
      router.route(accountMessageV2,sender())
    }

    case feedbackMessage:FeedbackMessage =>{
      //反馈
      router.route(feedbackMessage,sender())
    }

    case purviewMsg: PurviewMsg =>{
      //权限
      router.route(purviewMsg,sender())
    }

    case otherMsg: OtherMsg =>{
      //其他
      router.route(otherMsg,sender())
    }

    case Terminated(a) =>{
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[PushActor])
      context watch r
      router = router.addRoutee(r)
    }

  }

}
