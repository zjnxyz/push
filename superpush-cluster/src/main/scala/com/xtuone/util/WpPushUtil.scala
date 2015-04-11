package com.xtuone.util

import java.net.{HttpURLConnection, URL}

/**
 * Created by Zz on 2015/1/27.
 */
object WpPushUtil {

  val openView = "/UI/General/Views/WelcomeView.xaml"

  def push(pushUrl: String, content: String, title: String, paramStr:String):Boolean = {

    var flag = false

    val toastMsg = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
      "<wp:Notification xmlns:wp=\"WPNotification\">" +
        "<wp:Toast>" +
          "<wp:Text1>" + title + "</wp:Text1>" +
           "<wp:Text2>" + content + "</wp:Text2>" +
            "<wp:Param>" + paramStr + "</wp:Param>" +
            "</wp:Toast>" +
      "</wp:Notification>"

    var url:URL = null
    var http:HttpURLConnection = null

    try{
      url = new URL(pushUrl)
      http = url.openConnection().asInstanceOf[HttpURLConnection]
      http.setDoOutput(true)
      http.setRequestMethod("POST");
      http.setRequestProperty("Content-Type", "text/xml; charset=utf-8")
      http.setRequestProperty("X-WindowsPhone-Target", "toast")
      http.setRequestProperty("X-NotificationClass", "2")
      http.setRequestProperty("Content-Length", "1024")
      http.getOutputStream().write(toastMsg.getBytes())
      // 刷新对象输出流，将任何字节都写入潜在的流中
      http.getOutputStream().flush()
      // 关闭输出流
      http.getOutputStream().close()
    }catch {
      case e:Exception => e.printStackTrace()
    }finally {
      if(http != null){
        http.disconnect()
      }
    }
    if(http.getResponseCode() == 200){
      flag = true
    }
    flag
  }

  def main(args: Array[String]) {

    val url = "http://s.notify.live.net/u/1/sin/HmQAAAD3pPsFgQOdfNZJfbDsz_2-gmfGCLUS5Hx98vG4j6azd0benEQN53NrSZ6uqn12q74M6zVUt53HHkoZKA-3Kdzm/d2luZG93c3Bob25lZGVmYXVsdA/JAax2ivPYUWwcp0CrlOPfw/U4NmemX6F8h-Jp2afGuaJb5qms4"

    println(MD5.getMD5(url))
   println( push(url,"你好", "你好经鉴定家地",openView +"?sti="+88888888+"&amp;mt="+MessageType.TREEHOLE_REPLY+"&amp;pd="+100000000000000L))
  }

}
