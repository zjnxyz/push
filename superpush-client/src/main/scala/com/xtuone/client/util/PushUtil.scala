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

  /**
   * 推小纸条，聊天
   * @param chatMsg
   */
  def sendPaper(chatMsg: ChatMsg):Unit={
    AkkaUtil.pushRouter ! chatMsg
  }

  /**
   * 推下课聊消息
   * @param treeholeNewsMsg
   */
  def sendTreeholeNews(treeholeNewsMsg: TreeholeNewsMsg): Unit ={
    AkkaUtil.pushRouter ! treeholeNewsMsg
  }

  /**
   * 推送下课聊主题
   * @param treeholeMessageMsg
   */
  def sendTreeholeMessage(treeholeMessageMsg: TreeholeMessageMsg):Unit={
    AkkaUtil.pushRouter ! treeholeMessageMsg
  }


  def sendAccountMessage(accountMessage: AccountMessage):Unit={
    AkkaUtil.pushRouter ! accountMessage

  }


  def sendFeedbackMessage(feedbackMessage:FeedbackMessage):Unit={
    AkkaUtil.pushRouter ! feedbackMessage
  }

  def sendAccountMessageV2(accountMessageV2: AccountMessageV2):Unit={
    AkkaUtil.pushRouter ! accountMessageV2
  }

  def sendPurviewMessage(purviewMsg: PurviewMsg):Unit={
    AkkaUtil.pushRouter ! purviewMsg

  }

  def sendOtherMessage(otherMsg: OtherMsg): Unit ={
    AkkaUtil.pushRouter ! otherMsg
  }


//  private

}
