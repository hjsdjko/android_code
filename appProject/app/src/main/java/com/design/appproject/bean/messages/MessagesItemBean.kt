package com.design.appproject.bean.messages

data class MessagesItemBean(
    var id:Long=0L,
    var userid:Long=0,
    var username:String="",
    var avatarurl:String="",
    var content:String="",
    var cpicture:String="",
    var reply:String="",
    var rpicture:String="",
    var addtime:String?=null,
)