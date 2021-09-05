package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.google.gson.internal.LinkedTreeMap
import com.sunnyweather.android.logic.model.RoomInfo
import com.sunnyweather.android.logic.network.LiveNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

object Repository {
    fun getRecommend(page: Int, size: Int) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.getRecommend(page, size)
            if (liveResponse.code == "200") {
                val rooms = liveResponse.data
                Result.success(rooms)
            } else {
                Result.failure(RuntimeException("response status is ${liveResponse.message}"))
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun getRealUrl(platform: String, roomId: String) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.getRealUrl(platform, roomId)
            if (liveResponse.code == "200") {
                val rooms = liveResponse.data
                val resultRooms = LinkedTreeMap<String, String>()
                if (rooms.containsKey("OD")) {
                    resultRooms["原画"] = rooms["OD"]
                }
                if (rooms.containsKey("HD")) {
                    resultRooms["超清"] = rooms["HD"]
                }
                if (rooms.containsKey("SD")) {
                    resultRooms["高清"] = rooms["SD"]
                }
                if (rooms.containsKey("LD")) {
                    resultRooms["清晰"] = rooms["LD"]
                }
                if (rooms.containsKey("FD")) {
                    resultRooms["流畅"] = rooms["FD"]
                }
                Result.success(resultRooms)
            } else {
                Result.failure(RuntimeException("response status is ${liveResponse.message}"))
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun getRoomInfo(uid: String, platform: String, roomId: String) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.getRoomInfo(uid, platform, roomId)
            if (liveResponse.code == "200") {
                val roomInfo = liveResponse.data
                Result.success(roomInfo)
            } else {
                Result.failure(RuntimeException("response status is ${liveResponse.message}"))
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun getRoomsOn(uid: String) = liveData(Dispatchers.IO){
        val result = try {
            val liveResponse = LiveNetwork.getRoomsOn(uid)
            if (liveResponse.code == "200") {
                val rooms = liveResponse.data
                Result.success(rooms)
            } else {
                Result.failure(RuntimeException("response status is ${liveResponse.message}"))
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
    fun search(platform: String, keyWords: String, isLive: String) = liveData(Dispatchers.IO){
        val result = try {
            val searchResponse = LiveNetwork.Search(platform, keyWords, isLive)
            if (searchResponse.code == "200") {
                val rooms = searchResponse.data
                Result.success(rooms)
            } else {
                Result.failure(RuntimeException("response status is ${searchResponse.message}"))
            }
        } catch (e: Exception) {
            Result.failure<List<RoomInfo>>(e)
        }
        emit(result)
    }
}