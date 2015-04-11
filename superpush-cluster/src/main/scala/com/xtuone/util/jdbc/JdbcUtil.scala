package com.xtuone.util.jdbc

import java.sql._

import com.typesafe.config.ConfigFactory

/**
 * Created by Zz on 2015/1/13.
 */
object JdbcUtil {

  private var conn: Connection = null

  private var rs: ResultSet = null

  private var pstmt: PreparedStatement=_

  /**
   * 得到数据库连接
   * @return
   */
  def getConntion:Connection={

    try{
      Class.forName("com.mysql.jdbc.Driver")

      val url = ConfigFactory.load().getString("database.url")

      val username = ConfigFactory.load().getString("database.username")

      val password =  ConfigFactory.load().getString("database.password")

      conn = DriverManager.getConnection(url,username,password)

    }catch {
      case ex:ClassNotFoundException => ex.printStackTrace()
      case ex:SQLException => ex.printStackTrace()
    }

    conn
  }

  def getPstmt(sql: String):PreparedStatement = {
    pstmt = getConntion.prepareStatement(sql)
    pstmt
  }

  /**
   * 关闭rs
   * @param rs
   */
  def closeRs(rs:ResultSet):Unit={
    if(rs != null){
      try{
        rs.close()
      }catch {
        case e:SQLException => e.printStackTrace()
      }
    }
  }

  def colsePstmt(pstmt: PreparedStatement):Unit={
    if(pstmt!=null){
      try {
        pstmt.close()
      } catch{
        case e:SQLException => e.printStackTrace();
      }
    }
  }

  /**
   * 关闭conn
   */
  def closeConn(conn: Connection):Unit={
    if(conn!=null){
      try {
        conn.close()
      } catch {
        case e:SQLException => e.printStackTrace();
      }
    }
  }
  /**
   * 关闭数据库连接
   */
  def closeConn:Unit={
    if(rs != null){
      try{
        rs.close()
      }catch {
        case e:SQLException => e.printStackTrace()
      }
    }

    if(pstmt!=null){
      try {
        pstmt.close()
      } catch{
        case e:SQLException => e.printStackTrace();
      }
    }

    if(conn!=null){
      try {
        conn.close()
      } catch {
        case e:SQLException => e.printStackTrace();
      }
    }

  }

  /**
   * 执行查询
   * @param pstmt
   * @return
   */
  def execQuery(pstmt: PreparedStatement ):ResultSet={
    try{
      //1、使用Statement对象发送SQL语句
      rs = pstmt.executeQuery();
      //2、返回结果
      rs;
    }catch {
      case e:SQLException => e.printStackTrace()
        null
    }

  }

  /**
   * 执行其他操作
   * @param pstmt
   * @return
   */
  def  execOther(pstmt: PreparedStatement):Int={
    try{
      //1、使用Statement对象发送SQL语句
      val affectedRows = pstmt.executeUpdate();
      //2、返回结果
      affectedRows

    }catch {
      case e:SQLException => e.printStackTrace()
        -1
    }
  }


}
