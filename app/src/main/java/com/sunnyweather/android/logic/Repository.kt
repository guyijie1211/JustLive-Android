package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
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
}