package com.xtuone.actor

import akka.actor.{ActorRef, ActorLogging, Actor}
import com.xtuone.message.{GetWorkers, Workers, Heartbeat, Register}
import com.xtuone.model.{Worker, Client}
import com.xtuone.util.{MethodHelper, AkkaOps, Const}
import org.slf4j.LoggerFactory

/**
 * Created by Zz on 2015/4/7.
 *
 */
class MasterActor extends Actor with ActorLogging{

  val logBack = LoggerFactory.getLogger(classOf[MasterActor])

  override def receive: Receive = {

    case register: Register =>{
      //注册机制
      if(register.connectType == Const.CONNECT_TYPE_CLIENT){
        //注册client
        Const.clientMap .+= (register.ip +":"+ register.port ->
          new Client(register.ip, register.port, System.currentTimeMillis()))
        //返回worker给client
        sendWorkerToClient(sender())

      }else if(register.connectType == Const.CONNECT_TYPE_WORKER){

        //注册worker
        Const.workerMap .+= (register.ip +":"+ register.port ->
          new Worker(register.ip, register.port, System.currentTimeMillis()))

        noticeAllClient()
      }else{
        // TODO 不存在的类型
      }

    }
    case heartbeat: Heartbeat =>{
      //心跳机制
      if(heartbeat.connectType == Const.CONNECT_TYPE_CLIENT){
        //注册client
        Const.clientMap .+= (heartbeat.ip +":"+ heartbeat.port ->
          new Client(heartbeat.ip, heartbeat.port, System.currentTimeMillis()))

      }else if(heartbeat.connectType == Const.CONNECT_TYPE_WORKER){

        //注册worker
        Const.workerMap .+= (heartbeat.ip +":"+ heartbeat.port ->
          new Worker(heartbeat.ip, heartbeat.port, System.currentTimeMillis()))

      }else{
        // TODO 不存在的类型
      }

    }

    case GetWorkers =>{
      sendWorkerToClient(sender())
    }
    case _ =>{
      println("参数有问题")
    }

  }

  private def sendWorkerToClient( actor:ActorRef): Unit ={
    actor ! MethodHelper.getActiveWorkers()
  }

  /**
   * 通知所有的client
   */
  private def noticeAllClient(): Unit ={
    Const.clientMap.foreach{
      case (key,value) =>{
        val clinetUrl = AkkaOps.toAkkaUrl(value.ip,value.port,Const.CLIENT_AKKA_SYSTEM_NAME,Const.CLIENT_ACTOR_NAME)
        val client = context.actorSelection(clinetUrl.toString)
        client ! MethodHelper.getActiveWorkers()
      }
    }
  }


}
