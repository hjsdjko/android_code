package com.design.appproject.bean

import com.blankj.utilcode.util.GsonUtils
import com.google.gson.reflect.TypeToken

var roleMenusList =
    GsonUtils.fromJson<List<RoleMenusItem>>("[{\"backMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-pay\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"轮播图\",\"menuJump\":\"列表\",\"tableName\":\"config\"},{\"appFrontIcon\":\"cuIcon-copy\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"公告资讯\",\"menuJump\":\"列表\",\"tableName\":\"news\"}],\"fontClass\":\"icon-common27\",\"menu\":\"系统管理\",\"unicode\":\"&#xee2c;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-rank\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"管理员\",\"menuJump\":\"列表\",\"tableName\":\"users\"},{\"appFrontIcon\":\"cuIcon-album\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"审核\"],\"menu\":\"用户\",\"menuJump\":\"列表\",\"tableName\":\"yonghu\"}],\"fontClass\":\"icon-common14\",\"menu\":\"管理员管理\",\"unicode\":\"&#xedfb;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-news\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"物品类型\",\"menuJump\":\"列表\",\"tableName\":\"wupinleixing\"}],\"fontClass\":\"icon-common39\",\"menu\":\"物品类型\",\"unicode\":\"&#xeeba;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"查看评论\"],\"menu\":\"拾取信息\",\"menuJump\":\"列表\",\"tableName\":\"shiquxinxi\"}],\"fontClass\":\"icon-common26\",\"menu\":\"招领失物\",\"unicode\":\"&#xee2b;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-flashlightopen\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"查看评论\"],\"menu\":\"失物信息\",\"menuJump\":\"列表\",\"tableName\":\"shiwuxinxi\"}],\"fontClass\":\"icon-common15\",\"menu\":\"发布挂失\",\"unicode\":\"&#xedfc;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-addressbook\",\"buttons\":[\"查看\",\"删除\",\"修改\"],\"menu\":\"认领记录\",\"menuJump\":\"列表\",\"tableName\":\"renlingjilu\"}],\"fontClass\":\"icon-common13\",\"menu\":\"失物认领\",\"unicode\":\"&#xedf7;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-explore\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"回复\"],\"menu\":\"留言板\",\"menuJump\":\"列表\",\"tableName\":\"messages\"}],\"fontClass\":\"icon-common38\",\"menu\":\"留言板管理\",\"unicode\":\"&#xeeb2;\"}],\"frontMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-cardboard\",\"buttons\":[\"查看\",\"查看评论\"],\"fontClass\":\"icon-common15\",\"menu\":\"失物信息\",\"menuJump\":\"列表\",\"tableName\":\"shiwuxinxi\",\"unicode\":\"&#xedfc;\"}],\"fontClass\":\"icon-common15\",\"menu\":\"发布挂失\",\"unicode\":\"&#xedfc;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-send\",\"buttons\":[\"查看\",\"查看评论\",\"认领\"],\"fontClass\":\"icon-common4\",\"menu\":\"拾取信息\",\"menuJump\":\"列表\",\"tableName\":\"shiquxinxi\",\"unicode\":\"&#xedab;\"}],\"fontClass\":\"icon-common4\",\"menu\":\"招领失物\",\"unicode\":\"&#xedab;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-clothes\",\"buttons\":[\"查看\"],\"fontClass\":\"icon-common9\",\"menu\":\"公告资讯\",\"menuJump\":\"列表\",\"tableName\":\"news\",\"unicode\":\"&#xedc9;\"}],\"fontClass\":\"icon-common9\",\"menu\":\"公告资讯\",\"unicode\":\"&#xedc9;\"}],\"hasBackLogin\":\"是\",\"hasBackRegister\":\"否\",\"hasFrontLogin\":\"否\",\"hasFrontRegister\":\"否\",\"roleName\":\"管理员\",\"tableName\":\"users\"},{\"backMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-flashlightopen\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"查看评论\"],\"menu\":\"失物信息\",\"menuJump\":\"列表\",\"tableName\":\"shiwuxinxi\"}],\"fontClass\":\"icon-common15\",\"menu\":\"发布挂失\",\"unicode\":\"&#xedfc;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-addressbook\",\"buttons\":[\"查看\",\"删除\"],\"menu\":\"认领记录\",\"menuJump\":\"列表\",\"tableName\":\"renlingjilu\"}],\"fontClass\":\"icon-common13\",\"menu\":\"失物认领\",\"unicode\":\"&#xedf7;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-explore\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"回复\"],\"menu\":\"留言板\",\"menuJump\":\"列表\",\"tableName\":\"messages\"}],\"fontClass\":\"icon-common38\",\"menu\":\"留言板管理\",\"unicode\":\"&#xeeb2;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-goodsnew\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"我的收藏\",\"menuJump\":\"1\",\"tableName\":\"storeup\"}],\"fontClass\":\"icon-common31\",\"menu\":\"我的收藏\",\"unicode\":\"&#xee48;\"}],\"frontMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-cardboard\",\"buttons\":[\"查看\",\"查看评论\"],\"fontClass\":\"icon-common15\",\"menu\":\"失物信息\",\"menuJump\":\"列表\",\"tableName\":\"shiwuxinxi\",\"unicode\":\"&#xedfc;\"}],\"fontClass\":\"icon-common15\",\"menu\":\"发布挂失\",\"unicode\":\"&#xedfc;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-send\",\"buttons\":[\"查看\",\"查看评论\",\"认领\"],\"fontClass\":\"icon-common4\",\"menu\":\"拾取信息\",\"menuJump\":\"列表\",\"tableName\":\"shiquxinxi\",\"unicode\":\"&#xedab;\"}],\"fontClass\":\"icon-common4\",\"menu\":\"招领失物\",\"unicode\":\"&#xedab;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-clothes\",\"buttons\":[\"查看\"],\"fontClass\":\"icon-common9\",\"menu\":\"公告资讯\",\"menuJump\":\"列表\",\"tableName\":\"news\",\"unicode\":\"&#xedc9;\"}],\"fontClass\":\"icon-common9\",\"menu\":\"公告资讯\",\"unicode\":\"&#xedc9;\"}],\"hasBackLogin\":\"否\",\"hasBackRegister\":\"否\",\"hasFrontLogin\":\"是\",\"hasFrontRegister\":\"是\",\"roleName\":\"用户\",\"tableName\":\"yonghu\"}]", object : TypeToken<List<RoleMenusItem>>() {}.type)

data class RoleMenusItem(
    val backMenu: List<MenuBean>,
    val frontMenu: List<MenuBean>,
    val hasBackLogin: String,
    val hasBackRegister: String,
    val hasFrontLogin: String,
    val hasFrontRegister: String,
    val roleName: String,
    val tableName: String
)

data class MenuBean(
    val child: List<Child>,
    val menu: String,
    val fontClass: String,
    val unicode: String=""
)

data class Child(
    val appFrontIcon: String,
    val buttons: List<String>,
    val menu: String,
    val menuJump: String,
    val tableName: String
)

