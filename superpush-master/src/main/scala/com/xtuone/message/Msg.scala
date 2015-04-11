package com.xtuone.message

/**
 * Created by Zz on 2015/4/7.
 * 注册信息
 */
case class Register(ip: String, port: Int, connectType: Int)

/**
 * 心跳信息
 * @param ip
 * @param port
 * @param connectType
 */
case class Heartbeat(ip: String, port: Int, connectType: Int)

/**
 * worker消息
 * @param master
 * @param workerList
 */
case class Workers(master:String,workerList: String)

/**
 * 获取当前worker
 */
case object GetWorkers
