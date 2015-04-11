package com.xtuone.actor

import java.sql.{Timestamp, Connection, PreparedStatement}

import akka.actor.{ActorLogging, Actor}
import com.xtuone.message.UserPublicMessageMsg
import com.xtuone.util.jdbc.JdbcUtil
import collection.JavaConversions._

/**
 * Created by Zz on 2015/1/15.
 * 同步用户接收到公众账号信息到数据库
 */
class SyncRevMessageActor extends Actor with ActorLogging{

  implicit  def toTimestamp(timestamp: Long) = new Timestamp(timestamp)
  override def receive: Receive = {
    case userPublicMessageMsg:UserPublicMessageMsg =>{
      log.info("同步用户接收到的公众账号消息")
      var statement: PreparedStatement = null
      var conn:Connection = null
      try{
        val insertSql = " INSERT INTO superpushusermessage(revStudentId,messageDetailId,sendTime) VALUES (?, ?,?)"
        conn = JdbcUtil.getConntion
        statement = conn.prepareStatement(insertSql)
        for(studentId <- userPublicMessageMsg.studentIds){
          statement.setInt(1,studentId)
          statement.setInt(2,userPublicMessageMsg.messageId)
          statement.setTimestamp(3,toTimestamp(userPublicMessageMsg.sendTime))
          statement.addBatch()
        }
        statement.executeBatch()
//        conn.commit()
      }finally {
        JdbcUtil.colsePstmt(statement)
        JdbcUtil.closeConn(conn)
      }
    }
  }
}
