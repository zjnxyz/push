package com.xtuone.test

import java.sql.{Connection, ResultSet, PreparedStatement}
import java.util

import com.google.gson.Gson
import com.xtuone.message.SuperAccountInfo
import com.xtuone.model.DatumData
import com.xtuone.util.jdbc.JdbcUtil

/**
 * Created by Zz on 2015/1/16.
 */
object TestJDBC extends App{

  var datumDatas = new util.ArrayList[DatumData]()
  datumDatas.add(new DatumData("简介","啦啦啦啦啦啦啦啦"))
  val g = new Gson()
  //  println(g.toJson(datumDatas))
  //
  val superAccountInfo = new SuperAccountInfo(0,"","club","1052",g.toJson(datumDatas),true,"测试的啦","http://qiniu.myfriday.cn/2_130016_8218729_1413217001461.jpg","http://qiniu.myfriday.cn/2_130016_8218729_1413217001461.jpg")


  updateAccountInfo(superAccountInfo)

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
          println("-->"+id)
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
          println("-->"+insertSql.toString())
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

}
