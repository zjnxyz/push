package com.xtuone.model

import java.util.Date

import scala.beans.BeanProperty

/**
 * Created by Zz on 2015/1/15.
 */

/**
 * 存储社团简介信息
 */
 class DatumData{
  @BeanProperty var key: String =_
  @BeanProperty var value: String =_
}

/**
 * 推送版本升级
 */
class UpgradedVersion{
 @BeanProperty var c: String =_
 @BeanProperty var t: String =_
 @BeanProperty var vb:UpgradedContent =_
}

class UpgradedContent{
 @BeanProperty var a: Int =_
 @BeanProperty var at: Date =_
 @BeanProperty var c: String =_
 @BeanProperty var p: Int =_
 @BeanProperty var vi: Int =_
 @BeanProperty var dl: String =_
 @BeanProperty var ia: Boolean =_
 @BeanProperty var iac: Boolean =_
 @BeanProperty var ir: Boolean =_
}

class Announcement{
 @BeanProperty var c: String =_
 @BeanProperty var t: String =_
 @BeanProperty var an: AnnouncementContent =_
}

class AnnouncementContent{
 @BeanProperty var ai: Int =_
 @BeanProperty var at: Date =_
 @BeanProperty var c: String =_
 @BeanProperty var p: Int =_
 @BeanProperty var vi: Int =_
}

