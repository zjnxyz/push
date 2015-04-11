package com.xtuone.client.util

/**
 * Created by Zz on 2015/4/8.
 */
object MethodHelper {

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

}
