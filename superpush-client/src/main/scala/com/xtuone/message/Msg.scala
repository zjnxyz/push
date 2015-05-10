package com.xtuone.message

import java.util

import com.xtuone.client.util.MethodHelper

import scala.beans.BeanProperty

/**
 * Created by Zz on 2015/1/13.
 */
case class Message (si: Int,mt: Int,pd: String )

/**
 * 推送其他信息
 * @param studentIds
 * @param message
 */
@SerialVersionUID(-1L)
case class OtherMsg(studentIds:util.ArrayList[String], alert: String, oId: Int, message: Message,confirmId:String = String.valueOf(MethodHelper.getCacheKey()), expireTime:Long = 86400)

/**
 * 用户权限分数
 * @param studentId
 * @param purview
 */
@SerialVersionUID(-1L)
case class PurviewMsg(studentId: Int, purview: Purview, confirmId:String = String.valueOf(MethodHelper.getCacheKey()), expireTime:Long = 86400)

/**
 * 权限
 * @param sti 学生id
 * @param pv 用户权限值
 *  @param r 用户等级
 */
case class Purview(sti: Int, pv: Int, r: Int )

/**
 * 反馈消息
 * @param studentId
 * @param messageIds
 */
case class FeedbackMessage(studentId:Int,messageIds:String, callBackUrl: String, confirmId:String = String.valueOf(MethodHelper.getCacheKey()), expireTime:Long = 86400)

/**
 * 公众账号信息
 * @param alert
 * @param studentIds
 */
case class LaMessage(alert: String,studentIds:util.List[String])

/**
 * 公众账号消息推送
 * @param studentIds
 * @param alert
 * @param superPushMessageDetail
 * @param superAccountInfo
 */
@SerialVersionUID(-734L)
case class AccountMessage(alert: String,studentIds:util.List[String], superPushMessageDetail: SuperPushMessageDetail,superAccountInfo: SuperAccountInfo )

@SerialVersionUID(-1L)
class AccountMessageV2 extends Serializable{
  @BeanProperty var alert:String =_
  @BeanProperty var studentIds:util.ArrayList[String] =_
  @BeanProperty var superPushMessageDetail: SuperPushMessageDetail =_
  @BeanProperty var superAccountInfo: SuperAccountInfo =_
  @BeanProperty var confirmId :String = String.valueOf(MethodHelper.getCacheKey())
  @BeanProperty var expireTime:Long = 2*86400
}

/**
 * 主题推送消息
 * @param studentIds
 * @param treeholeMessageNews
 */
@SerialVersionUID(-566009590756816263L)
case class TreeholeMessageMsg(studentIds:util.ArrayList[String],treeholeMessageNews: TreeholeMessageNews, confirmId:String = String.valueOf(MethodHelper.getCacheKey()), expireTime:Long = 86400)

/**
 * 下课聊主题消息体
 * @param c
 * @param mi
 * @param t
 */
case class TreeholeMessageNews(c: String, mi: Int, t: String)


/**
 * 下课聊消息推送
 * @param studentId
 */
@SerialVersionUID(-1L)
case class TreeholeNewsMsg(studentId:Int,unReadNews:UnReadNews, confirmId:String = String.valueOf(MethodHelper.getCacheKey()), expireTime:Long = 2*86400)

/**
 * 未读的下课聊消息数量
 * @param t 标题
 * @param ud 学生id
 * @param cc 未读数量
 * @param pd 推送时间
 * @param c 内容
 */
case class UnReadNews(t: String, ud:Int, cc:Int, pd: Long, c: String)

/**
 *
 * @param chatIdStr(接收人的聊天账号)
 * @param contactsTypeInt(接收人的类型)
 * @param pager（发送人发送的数据）
 * @param confirmId(用来认证的是否成功的id)
 * @param expireTime(消息过期时间)
 */
@SerialVersionUID(-1L)
case class ChatMsg(chatIdStr:String,contactsTypeInt:Int, pager:Paper, confirmId:String = String.valueOf(MethodHelper.getCacheKey()), expireTime:Long = 10*86400)

/**
 * 小纸条消息体
 * @param studentIdInt（发送的学生id）
 * @param avatarUrlStr（头像）
 * @param nicknameStr（昵称）
 * @param chatIdStr（聊天账号）
 * @param contentStr（内容）
 * @param contactsTypeInt（类型）
 * @param sendTimeLong（发送时间）
 * @param imageUrlStr（图片地址）
 * @param msgType（消息类型）
 */
case class Paper(studentIdInt: Int, avatarUrlStr: String, nicknameStr: String,
                 chatIdStr: String, contentStr: String, contactsTypeInt: Int,
                 sendTimeLong: Long,imageUrlStr:String, msgType:Int)

case class UserPublicMessageMsg(studentIds:util.List[Int],messageId: Int,sendTime:Long)

/**
 * 公众账号信息
 * @param id
 * @param publicAccount
 * @param clientType
 * @param clientAccountId
 * @param datumData
 * @param vip
 * @param nickname
 * @param avatarUrl
 * @param vipIcon
 */
case class SuperAccountInfo(id: Int,publicAccount: String,
                            clientType: String, clientAccountId: String,datumData:String,
                            vip:Boolean, nickname: String, avatarUrl: String, vipIcon:String)

case class SuperPushMessageDetail(id: Int, chatId: String, content: String, title: String,
                                  studentType:Int, serverId:Int, isOuterOpen: Int, messageType:Int,
                                  imgUrl: String,featureName: String, imageTextUrl: String,icon: String,
                                  callBackUrl: String, callType: Int, messageId:Int)

/**
 * Created by Zz on 2015/4/7.
 * 注册信息
 */
case class Register(ip: String, port: Int, connectType: Int)

/**
 * 心跳信息
 * @param ip
 * @param port
 * @param connectType
 */
case class Heartbeat(ip: String, port: Int, connectType: Int)

/**
 * worker消息
 * @param master
 * @param workerList
 */
case class Workers(master:String,workerList: String)

/**
 * 获取当前worker
 */
case object GetWorkers

@SerialVersionUID(-1L)
case class Result(key: String)

