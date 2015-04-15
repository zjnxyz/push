package com.xtuone.util

import com.xtuone.message.Workers

/**
 * Created by Zz on 2015/4/9.
 */
object MethodHelper {

  /**
   * 活跃的Worker
   * @return
   */
  def getActiveWorkers():Workers ={
    val workerList = new StringBuilder
    //返回workers列表
    Const.workerMap.foreach{
      case (key,value) => workerList.append(key).append(";")
    }
    new Workers(Const.HOST+":"+Const.PORT, workerList.toString())
  }



}
