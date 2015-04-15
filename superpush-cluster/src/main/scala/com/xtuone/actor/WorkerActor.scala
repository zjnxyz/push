package com.xtuone.actor

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSelection, ActorLogging, Actor}
import akka.event.Logging
import akka.util.Timeout
import com.xtuone.message.{Heartbeat, Register}
import com.xtuone.util.{MethodHelper, Const}
import collection.JavaConversions._
import scala.concurrent.ExecutionContext

import scala.util.{Failure, Success}

/**
 * Created by Zz on 2015/4/14.
 */
class WorkerActor extends Actor with ActorLogging{
  val logBack = Logging(context.system,classOf[WorkerActor])

  var master: ActorSelection = null

  //当前活跃
  var activeMasterUrl:String =_

  implicit val askTimeout = Timeout(60,TimeUnit.SECONDS)

  implicit val ec = ExecutionContext.Implicits.global

  override def receive: Receive = {

    case register:Register =>{
      //注册
      tryRegisterAllMasters(register)
    }

    case heartbeat: Heartbeat =>{
      //心跳
      sendMessageToMaster(heartbeat)
    }

  }

  /**
   * 注册到master
   */
  def tryRegisterAllMasters(register: Register): Unit ={
    for(masterUrl <- Const.masterUrls){
      logBack.info("注册到Master:"+masterUrl)
      val master = context.actorSelection(masterUrl.toString)
      val masterFuture = master resolveOne

      masterFuture.onComplete{
        case Success(actor) => {
          actor ! register
          activeMasterUrl = masterUrl
        }
        case Failure(ex) =>{
          //移除失效的masterUrl
          Const.masterUrls.remove(masterUrl)
        }
      }
    }
  }

  /**
   * 向master发送消息
   * 1.心跳消息
   * 2.请求worker列表
   *
   * @param msg
   */
  def sendMessageToMaster( msg:AnyRef):Unit={
    trySendMessageToMaster(0,0,msg)
  }

  private [xtuone] def trySendMessageToMaster(counter:Int, num: Int, msg:AnyRef):Unit={

    if(counter >  Const.masterUrls.size() ){
      logBack.info("找不到可用的master")
      throw new Exception("找不到可用的master")
    }

    val count = counter+1

    var numTemp = 0

    if(num > Const.masterUrls.size()){
      numTemp = 0
    }

    if(numTemp > 0){
      activeMasterUrl = Const.masterUrls.get(numTemp)
    }

    if(!MethodHelper.isNotEmpty(activeMasterUrl)){
      println("---")
      activeMasterUrl = Const.masterUrls.get(numTemp)
    }

    if(master == null){
      master = context.actorSelection(activeMasterUrl)
    }

    val masterFuture = master resolveOne()

    masterFuture.onComplete{
      case Success(actor) => {
        actor ! msg
      }
      case Failure(ex) =>{
        master = null
        trySendMessageToMaster(count,numTemp+1,msg)
      }
    }
  }
}
