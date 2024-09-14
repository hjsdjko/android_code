package com.union.union_basic.network

/**颁布实体类*/
class PageResultBean<T>(
    var total: Int,
    var pageSize: Int,
    var totalPage: Int,
    var currPage: Int,
    var list: List<T>,
)