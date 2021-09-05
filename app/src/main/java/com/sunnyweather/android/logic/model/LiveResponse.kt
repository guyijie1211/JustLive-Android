package com.sunnyweather.android.logic.model

data class LiveRoomResponse(val code: String, val message: String, val data: List<RoomInfo>)
data class UrlsResponse(val code: String, val message: String, val data: Map<String, String>)
data class RoomInfoResponse(val code: String, val message: String, val data: RoomInfo)
data class SearchResponse(val code: String, val message: String, val data: List<Owner>)

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
                    val headPic: String,
                    val cateName: String,
                    val isLive: String,
                    val followers: Int,
                    val isFollowed: Int)
