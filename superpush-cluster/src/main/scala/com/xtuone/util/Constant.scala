package com.xtuone.util

import java.sql.{Connection, ResultSet, PreparedStatement}
import java.util
import java.util.concurrent.TimeUnit

import com.google.common.cache.CacheBuilder
import com.xtuone.util.jdbc.JdbcUtil
import com.xtuone.util.redis.RedisUtil213
import org.slf4j.LoggerFactory

/**
 * Created by Zz on 2015/1/13.
 */
object Constant {

  //gopushKey秘钥
  val gopushKey = "_.super_cn_"
  //客服
  val contactsTypeInt_kufu = 1
  //学生
  val contactsTypeInt_student = 2
  //公众账号
  val contactsTypeInt_publicAcount = 3

  //一天
  val expire_redis = 86400
 //用户apns未读消息数量
  val KEY_APNS_NO_READ_NUM ="push:apns:studentId:"
  //接收短信通知的号码
  val mobileNumbers = "18027326692,18102215296"
  //数量
  val num = 2
  //短信内容
  val content = "十分钟内推送到gopush都失败了，快去检查下吧"

  /**
   * 定时重发时间
   */
  val RE_SEND_MESSAGE_TIME = 5*60*1000L

  /**
   * 分割标识符
   */
  val SPLIT_FLAG = "#>zjn<#"


}

object MethodHelper{

  var preCacheKey = 0L

  /**
   * 获取用来缓存的key
   * @return
   */
  def getCacheKey():Long = {
    var currCacheKey = System.currentTimeMillis()
    if(currCacheKey == preCacheKey){
      currCacheKey = currCacheKey +1
      preCacheKey = currCacheKey
    }
    currCacheKey
  }

  val MessageCache =  CacheBuilder.newBuilder()
    .expireAfterWrite(Constant.RE_SEND_MESSAGE_TIME*3,TimeUnit.MILLISECONDS)
    .concurrencyLevel(8)
    .build[String, String]()

  val logBack = LoggerFactory.getLogger(classOf[String])

  /**
   *  得到推送的键值
   * @param key
   * @return
   */
  def getPushKey(key:String):String = {
    val pushKey =  MD5.getMD5(key+Constant.gopushKey)
    pushKey
  }

  def getPushKey(key:Int):String = {
    val pushKey =  MD5.getMD5(key+Constant.gopushKey)
    pushKey
  }

  /**
   * 获取jpush的别名
   * @param studentId
   * @return
   */
  def getAliasName(studentId:Int):String = {
    val aliasName = MD5.getMD5("student_" + studentId)
    aliasName
  }
  def getAliasName(studentId:String):String = {
    val aliasName = MD5.getMD5("student_" + studentId)
    aliasName
  }

  def findWpPushUrl(studentId: Int): String = {

    var statement: PreparedStatement = null
    var rs:ResultSet = null
    var conn:Connection = null

    var pushUrl = ""
    val key = "WpPushRelation:"+studentId
    val map =  RedisUtil213.init().hashGetAll(key)
    if(map != null && !map.isEmpty ){
      pushUrl = map.get("pushUrl")
    }else{
      val sql = " SELECT * FROM `wppushrelation` WHERE id = ? "
      try{
        conn = JdbcUtil.getConntion
        statement = conn.prepareStatement(sql)
        statement.setInt(1,studentId)
        rs = statement.executeQuery()
        if(rs != null){
          while(rs.next()){
            val map = new util.HashMap[String,String]()
            pushUrl = rs.getString("pushUrl")
            val addTime = rs.getDate("addTime")
            map.put("pushUrl",pushUrl)
            map.put("addTime",DateUtil.formateDate(addTime))
            RedisUtil213.init().hashMultipleSet(key,map)
            //设置过期时间
            RedisUtil213.init().expire(key,Constant.expire_redis)
          }
        }
      }catch {
        case e:Exception => e.printStackTrace()
      }finally {
        JdbcUtil.closeRs(rs)
        JdbcUtil.colsePstmt(statement)
        JdbcUtil.closeConn(conn)
      }
    }
    pushUrl
  }

  /**
   * 移除失败的key
   * @param studentId
   */
  def removeFailerDeviceToken(studentId:String):Unit={
//    val key = "ApnsRelation:"+studentId
//    RedisUtil213.init().delKey(key)

  }

  def findUserDeviceToken(studentId:String): String ={

    var statement: PreparedStatement = null
    var rs:ResultSet = null
    var conn:Connection = null

    var deviceToken = ""

    val key = "ApnsRelation:"+studentId
    val map =  RedisUtil213.init().hashGetAll(key)
    if(map != null && !map.isEmpty){
        deviceToken = map.get("deviceToken")
    }else{
//      //从数据库中取值
//      val sql = " SELECT * FROM `apnsrelation` WHERE id = ? AND isLogin = 1 "
//      try{
//        conn = JdbcUtil.getConntion
//        statement = conn.prepareStatement(sql)
//        statement.setInt(1,studentId.toInt)
//        rs = statement.executeQuery()
//        if(rs != null){
//          while(rs.next()){
//            val map = new util.HashMap[String,String]()
//            deviceToken = rs.getString("deviceToken")
//            val addTime = rs.getDate("addTime")
//            map.put("deviceToken",deviceToken)
//            map.put("addTime",DateUtil.formateDate(addTime))
//            map.put("isLogin",rs.getInt("isLogin")+"")
//            RedisUtil213.init().hashMultipleSet(key,map)
//            //设置过期时间
//            RedisUtil213.init().expire(key,Constant.expire_redis)
//          }
//        }
//      }catch {
//        case e:Exception => e.printStackTrace()
//      } finally {
//        JdbcUtil.closeRs(rs)
//        JdbcUtil.colsePstmt(statement)
//        JdbcUtil.closeConn(conn)
//      }
    }

    deviceToken
  }

  /**
   * 判断字符是为空
   * @param str
   * @return
   */
  def isNotEmpty(str: String):Boolean = {
    var flag = false
    if(str != null && !"".equals(str)){
      flag = true
    }
    flag
  }

  //监控，10分钟都发送失败后，发短信报警
  var currTime = System.currentTimeMillis()
  //失败
  var failureNum = 0

  def monitorStatus(pushStatus: Boolean):Unit = {

     if(!pushStatus){
       //推送失败(十分钟内都推送失败，发短信)
       if(System.currentTimeMillis() - currTime > 10*60*1000){
         SmsUtil.sendTimeoutMsg()
         currTime = System.currentTimeMillis()
       }

       failureNum = failureNum +1
       if(failureNum > 10){
         // 发短信
         SmsUtil.sendOverFailureLimit()
         failureNum = 0
       }

     }else{
       currTime = System.currentTimeMillis()
       failureNum = 0
     }
   }

  /**
   * 将消息存入缓存中
   * @param message
   */
  def putMessageToCache(key:String, message:String):Unit = {
//    if(getMessageToCache(key) == null){
//      MessageCache.put(key,message)
//    }
  }

  def getMessageToCache(key:String):String ={
    MessageCache.getIfPresent(key)
  }

  def removeMessageCache(key: String):Unit = {
    MessageCache.invalidate(key)
  }

}

object MessageType{
  //下课聊消息
  val TREEHOLE_MESSAGE = 6
  //下课聊回复
  val TREEHOLE_REPLY = 2
  //软件更新
  val SOFTWARE_UPDATE = 7
  //软件通知
  val SOFTWARE_NOTICE = 9
  //公众账号
  val PUBLIC_ACCOUNTS = 8
  //聊天的消息
  val CHAT = 10
  //包含链接的消息
  val LINK = 12
  //用户权限
  val PURVIEW = 13
  //匹配
  val MATCH = 14

}
