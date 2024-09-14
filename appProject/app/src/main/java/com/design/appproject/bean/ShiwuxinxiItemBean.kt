package com.design.appproject.bean

/**
 * 失物信息实体类
 */
data class ShiwuxinxiItemBean(
    var id:Long=0L,
    var wupinmingcheng:String="",
    var tupian:String="",
    var wupinleixing:String="",
    var shuliang:Int=0,
    var diushididian:String="",
    var diushishijian:String="",
    var wupinmiaoshu:String="",
    var yonghuzhanghao:String="",
    var yonghuming:String="",
    var lianxidianhua:String="",
    var storeupnum:Int=0,
    var wupinzhuangtai:String="",
    var addtime:String?=null,
)