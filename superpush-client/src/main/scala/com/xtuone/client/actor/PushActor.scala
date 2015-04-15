package com.xtuone.client.actor

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.event.Logging
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}
import akka.util.Timeout
import com.xtuone.client.util.{AkkaOps, Const}
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
      println("chatMsg")
      sendMessageToWorker(chatMsg)
    }

    case treeholeNewsMsg: TreeholeNewsMsg =>{
      println("treeholeNewsMsg")
      sendMessageToWorker(treeholeNewsMsg)
    }

    case treeholeMessageMsg: TreeholeMessageMsg =>{
      println("treeholeMessageMsg")
      sendMessageToWorker(treeholeMessageMsg)
    }

    case accountMessage: AccountMessage =>{
      println("accountMessage")
      //公众账号
      sendMessageToWorker(accountMessage)
    }

    case accountMessageV2: AccountMessageV2 =>{
      println("accountMessageV2")
      //公众账号
      sendMessageToWorker(accountMessageV2)
    }

    case feedbackMessage:FeedbackMessage =>{
      println("feedbackMessage")
      //反馈
      sendMessageToWorker(feedbackMessage)
    }

    case purviewMsg: PurviewMsg =>{
      println("purviewMsg")
      //权限
      sendMessageToWorker(purviewMsg)
    }

    case otherMsg: OtherMsg =>{
      //其他
      sendMessageToWorker(otherMsg)
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
      router.route(chatMsg,sender())
    }
    case treeholeNewsMsg: TreeholeNewsMsg =>{
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
