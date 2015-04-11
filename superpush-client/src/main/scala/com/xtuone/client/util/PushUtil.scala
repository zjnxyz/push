package com.xtuone.client.util

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSelection, ActorRef, ActorSystem}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.xtuone.message._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Created by Zz on 2015/1/13.
 */
object PushUtil {

//  val actorSystem =  ActorSystem("LocalNodeApp", ConfigFactory.load().getConfig("LocalSys"))

  val actorPaths = ConfigFactory.load().getStringList("actorPath")

  lazy val pathNum =  actorPaths.size()

  implicit val askTimeout = Timeout(60,TimeUnit.SECONDS)

  implicit val ec = ExecutionContext.Implicits.global

  var master:ActorSelection = null

  private def getRouterActor(num: Int,  msg:AnyRef):Unit = {
    var i = num
    if(i >= pathNum){
      i = 0
    }

    if(master == null){
      master =  AkkaOps.getActorSystem().actorSelection(actorPaths.get(i))
    }
    val localFuture = master resolveOne()
    localFuture.onComplete{
      case Success(actor) => {
        actor ! msg
        println("~~~"+i)
      }
      case Failure(ex) =>{
        getRouterActor(i+1,msg)
      }
    }

  }

  private def createActor(num:Int,  msg:AnyRef):Unit = {
    var i = num
    if(i >= pathNum){
      i = 0
    }

    if(master == null){
      println("~~~查找~~~")
      master =  AkkaOps.getActorSystem().actorSelection(actorPaths.get(i))
    }

    master resolveOne()

    val localFuture = AkkaOps.getActorSystem().actorSelection(actorPaths.get(i)) resolveOne()
    localFuture.onComplete{
      case Success(actor) => {
        actor ! msg
        println("~~~"+i)
      }
      case Failure(ex) =>{
//        ex.printStackTrace()
//        count = count+1
        createActor(i+1,msg)
        master = null
//        println("error")
      }
    }

  }

  /**
   * 推小纸条，聊天
   * @param chatMsg
   */
  def sendPaper(chatMsg: ChatMsg):Unit={
    val num = System.currentTimeMillis() % pathNum
//    createActor(num.toInt,chatMsg)
    getRouterActor(num.toInt,chatMsg)
  }

  /**
   * 推下课聊消息
   * @param treeholeNewsMsg
   */
  def sendTreeholeNews(treeholeNewsMsg: TreeholeNewsMsg): Unit ={
    val num = System.currentTimeMillis() % pathNum
//    createActor(num.toInt,treeholeNewsMsg)
    getRouterActor(num.toInt,treeholeNewsMsg)
  }

  /**
   * 推送下课聊主题
   * @param treeholeMessageMsg
   */
  def sendTreeholeMessage(treeholeMessageMsg: TreeholeMessageMsg):Unit={
    val num = System.currentTimeMillis() % pathNum
//    createActor(num.toInt, treeholeMessageMsg)
    getRouterActor(num.toInt,treeholeMessageMsg)
  }


  def sendAccountMessage(accountMessage: AccountMessage):Unit={
    val num = System.currentTimeMillis() % pathNum
//    createActor(num.toInt, accountMessage)
    getRouterActor(num.toInt,accountMessage)
  }


  def sendFeedbackMessage(feedbackMessage:FeedbackMessage):Unit={
    val num = System.currentTimeMillis() % pathNum
//    createActor(num.toInt, feedbackMessage)

    getRouterActor(num.toInt,feedbackMessage)
  }

  def sendAccountMessageV2(accountMessageV2: AccountMessageV2):Unit={
    val num = System.currentTimeMillis() % pathNum
    getRouterActor(num.toInt,accountMessageV2)
//    createActor(num.toInt, accountMessageV2)
  }

  def sendPurviewMessage(purviewMsg: PurviewMsg):Unit={
    val num = System.currentTimeMillis() % pathNum
    getRouterActor(num.toInt,purviewMsg)
//    createActor(num.toInt, purviewMsg)
  }

  def sendOtherMessage(otherMsg: OtherMsg): Unit ={
    val num = System.currentTimeMillis() % pathNum
    getRouterActor(num.toInt,otherMsg)
//    createActor(num.toInt, otherMsg)
  }


//  private

}
