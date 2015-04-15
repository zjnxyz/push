package com.xtuone.util

import com.typesafe.config.ConfigFactory

/**
 * Created by Zz on 2015/4/7.
 */
object Const {

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

  /**
   * workers
   */
  val CONNECT_TYPE_WORKER = 2

  val WORK_AKKA_SYSTEM_NAME = "RemoteNodeApp"

  val WORK_ACTOR_NAME = "PushDispatcher"


}
