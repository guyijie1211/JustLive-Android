package com.sunnyweather.android.logic.model

import android.graphics.Color

data class LiveRoomResponse(val code: String, val message: String, val data: List<RoomInfo>)
data class UrlsResponse(val code: String, val message: String, val data: Map<String, String>)
data class RoomInfoResponse(val code: String, val message: String, val data: RoomInfo)
data class SearchResponse(val code: String, val message: String, val data: List<Owner>)
data class AreaAllResponse(val code: String, val message: String, val data: List<List<AreaInfo>>)
data class UserInfoResponse(val code: String, val message: String, val data: UserInfo)

data class AreaInfo(val platform: String,
                    val areaType: String,
                    val typeName: String,
                    val areaId: String,
                    val areaName: String,
                    val areaPic: String,
                    val shortName: String)
data class RoomInfo(val roomId: String,
                    val platForm: String,
                    val roomPic: String,
                    val ownerHeadPic: String,
                    val ownerName: String,
                    val roomName: String,
                    val categoryId: String,
                    val categoryName: String,
                    val online: Int,
                    val isLive: Int,
                    val isFollowed: Int,
                    val eGameToken: String)
data class Owner(val platform: String,
                 val nickName: String,
                 val roomId: String,
                 var headPic: String,
                 val cateName: String,
                 val isLive: String,
                 val followers: Int,
                 val isFollowed: Int)
data class UserInfo(val uid: String,
                    val userName: String,
                    val nickName: String,
                    val password: String,
                    val head: String,
                    val isActived: String,
                    val allContent: String,
                    val selectedContent: String,
                    val douyuLevel: String,
                    val bilibiliLevel: String,
                    val huyaLevel: String,
                    val ccLevel: String,
                    val egameLevel: String)
data class DanmuSetting(
    var showArea: Float,
    var alpha: Float,
    var speed: Float,
    var size: Float,
    var border: Float,
    var merge: Boolean,
    var bold: Boolean,
    var fps: Boolean)
data class SendDanmuBean(
    var position: Long,
    var text: String = "",
    var isSmallSize: Boolean = false,
    var isScroll : Boolean = true,
    var isTop: Boolean = false,
    var color: Int = Color.WHITE,
)
