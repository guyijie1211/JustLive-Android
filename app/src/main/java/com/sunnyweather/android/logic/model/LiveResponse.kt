package com.sunnyweather.android.logic.model

data class LiveRoomResponse(val code: String, val message: String, val data: List<RoomInfo>)

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