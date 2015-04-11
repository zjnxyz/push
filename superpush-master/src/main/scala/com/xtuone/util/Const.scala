package com.xtuone.util

import com.xtuone.model.{Worker, Client}

/**
 * Created by Zz on 2015/4/7.
 */
object Const {

  //申明不变的map
  var clientMap: Map[String,Client] = Map()

  //申明
  var workerMap:  Map[String,Worker] = Map()

  //过期时间
  val timeout = 10*1000

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

  val CLIENT_AKKA_SYSTEM_NAME ="ClientNodeApp"

  val CLIENT_ACTOR_NAME ="Client"


}
