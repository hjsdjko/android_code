package com.design.appproject.bean

/**
 * 评论实体类
 */
data class CommentsBean(
    val addtime: String,
    val avatarurl: String,
    val content: String,
    val id: Long,
    val nickname: String,
    val refid: Long,
    val reply: String,
    val userid: Long
)