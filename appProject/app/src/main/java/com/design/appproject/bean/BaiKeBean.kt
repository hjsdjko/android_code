package com.design.appproject.bean

data class BaiKeBean(
    var name:String,
    var year:String,
    var keyword:String,
    var root:String,
    var baike_info:BaikeInfo,
)

data class BaikeInfo(
    var baike_url:String,
    var description:String,
)