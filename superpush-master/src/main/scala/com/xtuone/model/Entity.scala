package com.xtuone.model

/**
 * 客户端的列表
 * @param ip
 * @param port
 * @param addTime
 */
case class Client(ip: String, port: Int, addTime: Long )

/**
 * worker列表
 * @param ip
 * @param port
 * @param addTime
 */
case class Worker(ip: String, port: Int, addTime: Long)

/**
 * 检查worker，client
 */
case object Check
