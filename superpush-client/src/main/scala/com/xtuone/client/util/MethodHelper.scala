package com.xtuone.client.util

import java.util.concurrent.TimeUnit

import com.google.common.cache.CacheBuilder

/**
 * Created by Zz on 2015/4/8.
 */
object MethodHelper {

  //上一个使用的缓存key
  var preCacheKey = 0L

  val MessageCache =  CacheBuilder.newBuilder()
    .expireAfterWrite(Const.RE_SEND_MESSAGE_TIME*3,TimeUnit.MILLISECONDS)
    .concurrencyLevel(8)
    .build[String, AnyRef]()

  /**
   * 判断字符
   * @param str
   * @return
   */
  def isNotEmpty(str: String):Boolean = {
    if(str == null || "".equals(str)){
      false
    }else{
      true
    }
  }

  /**
   * 获取用来缓存的key
   * @return
   */
  def getCacheKey():Long = {
    var currCacheKey = System.currentTimeMillis()
    if(currCacheKey == preCacheKey){
      currCacheKey = currCacheKey +1
      preCacheKey = currCacheKey
    }
    currCacheKey
  }

  /**
   * 将消息存入缓存中
   * @param message
   */
  def putMessageToCache(key:String, message:AnyRef):Unit = {
//    if(getMessageToCache(key) == null){
//      MessageCache.put(key,message)
//    }
  }

  def getMessageToCache(key:String):AnyRef={
    MessageCache.getIfPresent(key)
  }

  def removeMessageCache(key: String):Unit = {
    MessageCache.invalidate(key)
  }



}
