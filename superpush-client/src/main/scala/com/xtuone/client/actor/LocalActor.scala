package com.xtuone.client.actor

import java.util.concurrent.TimeUnit

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import com.xtuone.message.ChatMsg

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Created by Zz on 2015/1/13.
 */
class LocalActor extends Actor with ActorLogging {


  implicit val askTimeout = Timeout(60,TimeUnit.SECONDS)

  implicit val ec = ExecutionContext.Implicits.global

  val remoteActor = context.actorSelection("akka.tcp://RemoteNodeApp@192.168.0.36:2552/user/PushDispatcher") resolveOne()
  remoteActor.onComplete{
    case Success(actor) => {
//      actor ! msg
      println("~~~")
    }
    case Failure(ex) =>{
      ex.printStackTrace()

      //        createActor(i+1,msg)
      println("error")
    }
  }

  override def receive: Receive = {

    case cahtMsg:ChatMsg =>{
//      remoteActor ! cahtMsg
    }
    case str:String =>{
      println("-------------")
    }


  }
}
