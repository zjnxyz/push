package com.xtuone.util

import java.io.IOException

import akka.event.Logging
import com.typesafe.config.ConfigFactory
import org.json.{JSONException, JSONObject}
import org.slf4j.LoggerFactory

/**
 * Created by Zz on 2015/1/12.
 */
object GopushUtil {

  val logBack = LoggerFactory.getLogger(classOf[String])
  //推给单个
  val one = ConfigFactory.load().getString("gopush.one")
  //推给多个
  val m = ConfigFactory.load().getString("gopush.m")
  //过期时间
  val expire = ConfigFactory.load().getInt("gopush.expire")

  /**
   * 推单个人
   * @param message
   * @param studentId
   * @return
   */
  def pushMessage( message:String, studentId:String):Boolean = {

    var flag:Boolean = false
    try {
      val returnStr = HttpUtils.post(one+"?key=" + studentId + "&expire="+expire, message)
      logBack.info("gopush-retVal:"+returnStr)
      if(returnStr.contains("0")){
        flag = true
      }

    }catch {
      case e:IOException  => {
        e.printStackTrace()
        logBack.info("IOException:"+e.getMessage)
      }

    }

    flag
  }

  /**
   * 推送多个人
   * @param message
   * @param studentIds
   * @return
   */
  def pushMoreMessage(message:String,studentIds:String):Boolean={
    var flag:Boolean = false
    val jsonObject_m = new JSONObject()
    jsonObject_m.put("m", message)
    jsonObject_m.put("k", studentIds)

    try{
      val returnStr = HttpUtils.post(m+"?expire="+expire,jsonObject_m.toString())
      logBack.info("gopush-retVal:"+returnStr)
      if(returnStr.contains("0")){
        flag = true
      }
    }catch {
      case json:JSONException => json.printStackTrace()
      case e:IOException => e.printStackTrace()
    }

    flag
  }



  def main(args: Array[String]) {
    println(GopushUtil.pushMessage("你好","123456"))
  }


}
