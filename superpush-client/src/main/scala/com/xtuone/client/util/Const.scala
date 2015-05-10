package com.xtuone.client.util

import com.typesafe.config.ConfigFactory

/**
 * Created by Zz on 2015/4/7.
 */
object Const {

  /**
   * 存放worker地址列表
   */
  var workerUrls: List[String] = List()

  /**
   * master地址
   */
  var masterUrls = ConfigFactory.load().getStringList("masterPath")

  /**
   * 当前的ip
   */
  var HOST: String = ""

  /**
   * 当前使用的端口
   */
  var PORT: Int = 0
  /**
   * client链接类型
   */
  val CONNECT_TYPE_CLIENT = 1

  val WORK_AKKA_SYSTEM_NAME = "RemoteNodeApp"

  val WORK_ACTOR_NAME = "PushDispatcher"

  val CLIENT_AKKA_SYSTEM_NAME ="ClientNodeApp"

  val CLIENT_ACTOR_NAME ="Client"
  /**
   * 定时重发时间
   */
  val RE_SEND_MESSAGE_TIME = 5000







}
