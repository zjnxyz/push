package com.xtuone.client.test

import java.util.concurrent.TimeUnit

import com.google.common.cache.{CacheLoader, CacheBuilder}
import com.xtuone.client.util.{MethodHelper, PushUtil}
import com.xtuone.message.{Paper, ChatMsg}

/**
 * Created by Zz on 2015/4/15.
 */
object Test2 extends App{

  val cache = CacheBuilder.newBuilder().expireAfterWrite(10,TimeUnit.SECONDS)
    .concurrencyLevel(8)
    .build[String,Option[String]]()
val value = Some("hello")

  cache.put("hello",value)

  val op1 = cache.getIfPresent("hello")
  if(op1.isDefined){
    println(op1.get)
  }


  Thread.sleep(5000)
  println(cache.getIfPresent("hello").get)
  cache.invalidate("hello")
  val option = cache.getIfPresent("hello")
  if(option.isDefined){
    println("invalidate:"+cache.getIfPresent("hello").get)
  }
  println("invalidate:"+option.get)

  Thread.sleep(6000)
  println(cache.getIfPresent("hello"))


}
