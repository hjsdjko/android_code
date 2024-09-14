package com.design.appproject.bean

/**
 * 认领记录实体类
 */
data class RenlingjiluItemBean(
    var id:Long=0L,
    var shiquzhexingming:String="",
    var shiquzhedianhua:String="",
    var wupinmingcheng:String="",
    var wupinleixing:String="",
    var shuliang:Int=0,
    var renlingpingzheng:String="",
    var yonghuzhanghao:String="",
    var yonghuming:String="",
    var renlingshijian:String="",
    var addtime:String?=null,
)