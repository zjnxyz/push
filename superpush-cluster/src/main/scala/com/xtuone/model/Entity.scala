package com.xtuone.model

import java.util.Date

/**
 * Created by Zz on 2015/1/13.
 */
case class ApnsRelation(id: Int, deviceToken: String, addTime:Date)

/**
 * 存储社团简介信息
 * @param key
 * @param value
 */
case class DatumData(key: String, value: String)

/**
 * 发出去
 * @param url
 * @param hv
 * @param t
 * @param c
 */
case class LinkMessageBo(url:String,hv:Int,t:String,c:String)




