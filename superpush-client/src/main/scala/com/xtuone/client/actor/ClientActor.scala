package com.xtuone.client.actor

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSelection, ActorLogging, Actor}
import akka.event.Logging
import akka.util.Timeout
import com.xtuone.client.util.{MethodHelper, Const, AkkaOps}
import com.xtuone.message._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import collection.JavaConversions._

/**
 * Created by Zz on 2015/3/4.
 */
class ClientActor extends Actor with ActorLogging{

  implicit val askTimeout = Timeout(60,TimeUnit.SECONDS)

  implicit val ec = ExecutionContext.Implicits.global

  var worker: ActorSelection = null

  var master: ActorSelection = null

  //当前活跃
  var activeMasterUrl:String =_

  override def receive: Receive = {
    case register:Register =>{
      //注册
      tryRegisterAllMasters(register)
    }

    case heartbeat:Heartbeat =>{
      //心跳
      sendMessageToMaster(heartbeat)

    }

    case workers:Workers =>{

      //当前可用的worker列表
      val workerList = workers.workerList
      if(MethodHelper.isNotEmpty(workerList)){
        //处理当前的worker
        Const.workerUrls = List()
        val hostAndPortArr = workerList.split(";")
        hostAndPortArr.foreach{
          case hostAndPort =>{
            val hpArr = hostAndPort.split(":")
            val host = hpArr.head
            val port = hpArr.last.toInt
            Const.workerUrls  =
            (AkkaOps.toAkkaUrl(host,port,Const.WORK_AKKA_SYSTEM_NAME,Const.WORK_ACTOR_NAME)) :: Const.workerUrls
          }
        }

      }
    }

    case _ => ""
  }

  @throws(classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
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

  private [xtuone] def trySendMessageToMaster(counter:Int, num: Int,  msg:AnyRef):Unit={

    if(counter >  Const.masterUrls.size() ){
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

  /**
   * 注册到master
   */
  def tryRegisterAllMasters(register: Register): Unit ={
    for(masterUrl <- Const.masterUrls){
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

}
