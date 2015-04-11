package com.xtuone.util

import cn.jpush.api.JPushClient
import cn.jpush.api.push.model.audience.Audience
import cn.jpush.api.push.model.notification.{IosNotification, Notification}
import cn.jpush.api.push.model.{Message, Platform, PushPayload,Options}
import java.util

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

/**
 * Created by Zz on 2015/1/15.
 */
object JpushUtil {

  val appkey =  ConfigFactory.load().getString("jpush.appKey")
  val masterSecret = ConfigFactory.load().getString("jpush.masterSecret")

  val logBack = LoggerFactory.getLogger(JpushUtil.getClass)

  var jpushClient = new JPushClient(masterSecret,appkey,3)
  /**
   * 推送给单个用户
   * @param message
   * @param alert
   * @param extras
   * @param aliasName
   * @return
   */
  def pushMessage(message:String,alert:String, extras:util.HashMap[String,String], aliasName:String):Boolean={

    var flag = false
    //推送给单个人
    val builder = PushPayload.newBuilder()
    builder.setPlatform(Platform.all())
      .setMessage(Message.content(message))
      .setOptions(
        Options.newBuilder().setApnsProduction(true).build())
      .setAudience(Audience.alias(aliasName))
      .setNotification(Notification.newBuilder()
      .addPlatformNotification(
        IosNotification.newBuilder()
          .setAlert(alert)
          .autoBadge()
          .setSound("default")
          .addExtras(extras).build())
      .build())

    val pushPayload = builder.build()
    try{

      val result = jpushClient.sendPush(pushPayload)
      if(result.isResultOK){
        flag = true
      }
      logBack.info("msgId:"+result.msg_id)
    }catch {
      case e:Exception => e.printStackTrace()
    }


    flag
  }

  /**
   * 批量推送给用户
   * @param message
   * @param alert
   * @param extras
   * @param aliasNames
   * @return
   */
  def pushMoreMessage(message:String,alert:String, extras:util.HashMap[String,String], aliasNames:util.ArrayList[String]): Boolean ={

    var flag = false
    val builder = PushPayload.newBuilder()
    builder.setPlatform(Platform.all())
      .setMessage(Message.content(message))
      .setOptions(
        Options.newBuilder().setApnsProduction(true).build())
      .setAudience(Audience.alias(aliasNames))
      .setNotification(Notification.newBuilder()
      .addPlatformNotification(
        IosNotification.newBuilder()
          .setAlert(alert)
          .autoBadge()
          .setSound("default")
          .addExtras(extras).build())
      .build())

    val pushPayload = builder.build()

//    logBack.info("msg:"+pushPayload.toJSON.toString)
    try{
      val result = jpushClient.sendPush(pushPayload)
      if(result.isResultOK){
        logBack.info("msg_Id"+result.msg_id)
        flag = true
      }
    }catch{
      case exception:Exception => exception.printStackTrace()
    }

    flag
  }

}
