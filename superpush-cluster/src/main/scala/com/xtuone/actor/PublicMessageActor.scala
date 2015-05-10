package com.xtuone.actor

import java.sql.{Connection, ResultSet, PreparedStatement}
import java.util
import java.util.Date

import akka.actor._
import akka.event.Logging
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}
import com.google.gson.Gson
import com.xtuone.bo.BaseMessageBO
import com.xtuone.message.{UserPublicMessageMsg, PublicMessageMsg}
import com.xtuone.message.{SuperPushMessageDetail, SuperAccountInfo}
import com.xtuone.util.model.AnpsMessage
import com.xtuone.util._
import com.xtuone.util.jdbc.JdbcUtil
import com.xtuone.util.redis.RedisUtil213
import org.slf4j.LoggerFactory
import collection.JavaConversions._

/**
 * Created by Zz on 2015/1/15.
 */
class PublicMessageActor extends Actor with ActorLogging{

  var apnsPublicMessageActor: ActorRef =_

  var syncRevMessageActor: ActorRef =_

  val logBack = LoggerFactory.getLogger(classOf[PublicMessageActor])
  //失败标识
  var failureFlag = false
  var count = 0


  @throws(classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    apnsPublicMessageActor = context.actorOf(Props[ApnsPublicMessageActor])
    syncRevMessageActor = context.actorOf(Props[SyncRevMessageActor])

    context watch apnsPublicMessageActor
  }

  override def receive: Receive = {

    case publicMessageMsg:PublicMessageMsg => {

      val g = new Gson()
      val pushMessage = new BaseMessageBO
      pushMessage.setMt(MessageType.PUBLIC_ACCOUNTS)

      //判断公众账号信息是否需要更新
      updateAccountInfo(publicMessageMsg.superAccountInfo)
      //保存消息（）
      val messageId = savePublicMessage(publicMessageMsg.superPushMessageDetail, publicMessageMsg.expireTime)
      //加入到redis中
      val sendTimeLong = System.currentTimeMillis()
      saveMessageToEveryOne(publicMessageMsg.studentIds,messageId,sendTimeLong)
      //批量推送给gopush
      val studentIds = new StringBuilder
      for( studentId <- publicMessageMsg.studentIds){
        studentIds.append(MethodHelper.getPushKey(studentId)).append(",")
      }

      if(studentIds != null && studentIds.size > 0){
        val result = GopushUtil.pushMoreMessage(g.toJson(pushMessage), studentIds.substring(0,studentIds.length-1), publicMessageMsg.expireTime)
        //测试时，暂时改为一直失败
        MethodHelper.monitorStatus(result)
        logBack.info("gopush-->result:"+result+": chatId :"+studentIds)
      }

      //推送apns
      apnsPublicMessageActor ! publicMessageMsg

      //同步数据到数据库中（使用一个actor,不保存到数据库中）
//      syncRevMessageActor ! new UserPublicMessageMsg(publicMessageMsg.studentIds,messageId,sendTimeLong)
    }

    case Terminated(a) =>{
      apnsPublicMessageActor = context.actorOf(Props[ApnsPublicMessageActor])
      context watch apnsPublicMessageActor
    }

  }
  /**
   * 更新公众账号信息
   * @param superAccountInfo
   */
  private def updateAccountInfo(superAccountInfo: SuperAccountInfo): Unit ={

    var statement: PreparedStatement = null
    var rs:ResultSet = null
    var conn:Connection = null

    if(superAccountInfo != null){
      try{
        //公众账号
        val publicAccount = superAccountInfo.clientType+"_"+superAccountInfo.clientAccountId
        val querySql = " SELECT * FROM superaccountinfo WHERE publicAccount = ? limit 1 "
        conn = JdbcUtil.getConntion
        statement = conn.prepareStatement(querySql)
        statement.setString(1,publicAccount)
        rs = statement.executeQuery()
        if(rs.next()){
          //更新数据
          val id = rs.getInt("id")
          val updateSqlBuffer = new StringBuilder
          updateSqlBuffer.append( " UPDATE superaccountinfo SET publicAccount = ?, clientAccountId = ?, clientType = ?, ")
          updateSqlBuffer.append(" vip = ?, nickname = ?, avatarUrl = ?, datumData = ?, vipIcon = ?")
          updateSqlBuffer.append(" WHERE id = ? ")
          statement = conn.prepareStatement(updateSqlBuffer.toString())
          statement.setString(1,publicAccount)
          statement.setString(2,superAccountInfo.clientAccountId)
          statement.setString(3,superAccountInfo.clientType)
          statement.setBoolean(4,superAccountInfo.vip)
          statement.setString(5,superAccountInfo.nickname)
          statement.setString(6,superAccountInfo.avatarUrl)
          statement.setString(7,superAccountInfo.datumData)
          statement.setString(8,superAccountInfo.vipIcon)
          statement.setInt(9,id)
          statement.executeUpdate()

        }else{
          //保存数据
          val insertSql = new StringBuilder
          insertSql.append(" INSERT INTO superaccountinfo( publicAccount, clientAccountId, clientType, vip, nickname, avatarUrl, ")
          insertSql.append(" datumData, vipIcon )")
          insertSql.append("VALUES  (?,?,?,?,?,?,?,?)")
          statement = conn.prepareStatement(insertSql.toString())
          statement.setString(1,publicAccount)
          statement.setString(2,superAccountInfo.clientAccountId)
          statement.setString(3,superAccountInfo.clientType)
          statement.setBoolean(4,superAccountInfo.vip)
          statement.setString(5,superAccountInfo.nickname)
          statement.setString(6,superAccountInfo.avatarUrl)
          statement.setString(7,superAccountInfo.datumData)
          statement.setString(8,superAccountInfo.vipIcon)
          statement.executeUpdate()
        }

      }finally {
        JdbcUtil.closeRs(rs)
        JdbcUtil.colsePstmt(statement)
        JdbcUtil.closeConn(conn)
      }
    }
  }

  /**
   * 保存公众账号信息
   * @param superPushMessageDetail
   */
  private def savePublicMessage( superPushMessageDetail: SuperPushMessageDetail, expireTime: Long = Constant.expire_redis): Int ={

    val idKey = "SuperPushMessageDetail"
    val idStr = RedisUtil213.init().getString(idKey)
    var id = 0L


    //保存到数据库中
    var statement: PreparedStatement = null
    var conn:Connection = null

    val insertSql = new StringBuilder
    insertSql.append(" INSERT INTO superpushmessagedetail(chatId, title, content, studentType, serverId, isOuterOpen, ")
    insertSql.append(" messageType, imgUrl, featureName, imageTextUrl, icon, callBackUrl, callType, messageId ) ")
    insertSql.append(" VALUES  (?,?,?,?,?,?,?,?,?,?,?,?,?,? )")

    try{
      conn = JdbcUtil.getConntion
      statement = conn.prepareStatement(insertSql.toString())
      statement.setString(1,superPushMessageDetail.chatId)
      statement.setString(2,superPushMessageDetail.title)
      statement.setString(3,superPushMessageDetail.content)
      statement.setInt(4,superPushMessageDetail.studentType)
      statement.setInt(5,superPushMessageDetail.serverId)
      statement.setInt(6,superPushMessageDetail.isOuterOpen)
      statement.setInt(7,superPushMessageDetail.messageType)
      statement.setString(8,superPushMessageDetail.imgUrl)
      statement.setString(9,superPushMessageDetail.featureName)
      statement.setString(10,superPushMessageDetail.imageTextUrl)
      statement.setString(11,superPushMessageDetail.icon)
      statement.setString(12,superPushMessageDetail.callBackUrl)
      statement.setInt(13,superPushMessageDetail.callType)
      statement.setInt(14,superPushMessageDetail.messageId)
      statement.executeUpdate()

      //查询刚才保存的消息
      val query = " SELECT id FROM superpushmessagedetail WHERE chatId = ? AND title = ? ORDER BY id DESC LIMIT 1"
      statement = conn.prepareStatement(query.toString())
      statement.setString(1,superPushMessageDetail.chatId)
      statement.setString(2,superPushMessageDetail.title)
      val rs = statement.executeQuery()
      while (rs.next()){
        id = rs.getInt("id")
      }

      if(id == 0){
        if(idStr == null || "nil".equals(idStr)){
          id = 10000L
          RedisUtil213.init().setString(idKey,id+"")
        }else{
          id = RedisUtil213.init().makeId(idKey)
        }
      }else{
        RedisUtil213.init().setString(idKey,id+"")
      }

      val key = "SuperPushMessageDetail:"+id
      //保存到redis中
      val map = new util.HashMap[String,String]()
      map.put("chatId",superPushMessageDetail.chatId)
      map.put("title", superPushMessageDetail.title )
      map.put("content", superPushMessageDetail.content )
      map.put("studentType", superPushMessageDetail.studentType+"")
      map.put("serverId",superPushMessageDetail.serverId+"")
      map.put("isOuterOpen",superPushMessageDetail.isOuterOpen+"")
      map.put("messageType",superPushMessageDetail.messageType+"")
      map.put("imgUrl",superPushMessageDetail.imgUrl)
      map.put("imageTextUrl",superPushMessageDetail.imageTextUrl)

      if(MethodHelper.isNotEmpty(superPushMessageDetail.icon)){
        map.put("icon",superPushMessageDetail.icon)
      }
      if(MethodHelper.isNotEmpty(superPushMessageDetail.featureName)){
        map.put("featureName",superPushMessageDetail.featureName)
      }
      map.put("sendTime",DateUtil.formateDate(new Date()))
      map.put("callBackUrl",superPushMessageDetail.callBackUrl)
      map.put("callType",superPushMessageDetail.callType+"")
      map.put("messageId",superPushMessageDetail.messageId+"")

      RedisUtil213.init().hashMultipleSet(key,map)
      //过期时间
      RedisUtil213.init().expire(key,expireTime.toInt)

    }finally {
      JdbcUtil.colsePstmt(statement)
      JdbcUtil.closeConn(conn)
    }

    id.toInt
  }

  /**
   * 推送给每个人
   * @param studentIds
   * @param messageId
   * @param sendTimeLong
   */
  private def saveMessageToEveryOne(studentIds:util.List[Int],messageId:Int,sendTimeLong:Long): Unit ={
    val key = "PushMessageTimeline:studentId:"
    for(studentId <- studentIds ){
      RedisUtil213.init().addWithSortedSet(key+studentId,sendTimeLong,messageId+"")
    }
  }
}

/**
 * 推送到极光
 */
class JpushPublicMessageActor extends Actor with ActorLogging{

  val logBack = LoggerFactory.getLogger(classOf[JpushPublicMessageActor])

  val g = new Gson()

  override def receive: Actor.Receive = {
    case  publicMessageMsg:PublicMessageMsg =>{

      val pushMessage = new BaseMessageBO
      pushMessage.setMt(MessageType.PUBLIC_ACCOUNTS)
      val extras = new util.HashMap[String,String]()
      extras.put("mt",MessageType.PUBLIC_ACCOUNTS+"")

      val aliasNames = new util.ArrayList[String]()
      for( studentId <- publicMessageMsg.studentIds){
        aliasNames.add(MethodHelper.getAliasName(studentId))
      }

      if(aliasNames.size() > 0){
        val result = JpushUtil.pushMoreMessage(g.toJson(pushMessage),
          publicMessageMsg.alert,extras,aliasNames)
        if(!result){
          //TODO 错误保存到Mysql
          logBack.info("极光失败了-----"+publicMessageMsg.studentIds.toString)
        }else{
          logBack.info("极光成功了-----")
        }
      }

    }
  }

}

class ApnsPublicMessageActor extends Actor with ActorLogging{

  val logBack = LoggerFactory.getLogger(classOf[ApnsPublicMessageActor])

  var jpushPublicMessageActor: ActorRef =_

  val g = new Gson()


  @throws(classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    jpushPublicMessageActor = context.actorOf(Props[JpushPublicMessageActor])
    context watch jpushPublicMessageActor
  }

  override def receive: Actor.Receive = {
    case  publicMessageMsg:PublicMessageMsg =>{
      val jpushStudentIds = new util.ArrayList[Int]()

      //推送到 apns
      for( studentId <- publicMessageMsg.studentIds) {
        val deviceToken = MethodHelper.findUserDeviceToken(studentId + "")
        logBack.info("deviceToken:"+deviceToken)
        if (MethodHelper.isNotEmpty(deviceToken)) {
          val apnsMessage = new AnpsMessage
          //设置弹出内容
          apnsMessage.setAlert(publicMessageMsg.alert)
          //增加badge数量
          val badge = RedisUtil213.init().incr(Constant.KEY_APNS_NO_READ_NUM+studentId)
          apnsMessage.setBadge(badge.toInt)

          val extras = new util.HashMap[String,String]()
          extras.put("mt",MessageType.PUBLIC_ACCOUNTS+"")
          apnsMessage.setExtras(extras)
         val result = ApnsPushUtil.push(apnsMessage,deviceToken)
          logBack.info("apns-->result:"+result+": chatId :"+ studentId +" :message: ")

        }else{
          jpushStudentIds.add(studentId)
        }
      }

      if(jpushStudentIds.size() > 0){

        val jpushPublicMessageMsg = new PublicMessageMsg(jpushStudentIds,publicMessageMsg.alert, publicMessageMsg.superPushMessageDetail,
          publicMessageMsg.superAccountInfo, publicMessageMsg.confirmId ,publicMessageMsg.expireTime)

        jpushPublicMessageActor ! jpushPublicMessageMsg
      }

    }
    case Terminated(a) =>{
      jpushPublicMessageActor = context.actorOf(Props[JpushPublicMessageActor])
      context watch jpushPublicMessageActor
    }
  }
}

/**
 * 公众消息推送路由
 */
class PublicMessageRouter extends Actor with ActorLogging{

  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[PublicMessageActor])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Actor.Receive = {
    case  publicMessageMsg:PublicMessageMsg =>{
      router.route(publicMessageMsg,sender())
    }
    case Terminated(a) =>{
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[PublicMessageActor])
      context watch r
      router = router.addRoutee(r)
    }

  }

}
