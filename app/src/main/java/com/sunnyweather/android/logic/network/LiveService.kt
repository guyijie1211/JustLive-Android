package com.sunnyweather.android.logic.network

import com.sunnyweather.android.logic.model.LiveRoomResponse
import com.sunnyweather.android.logic.model.RoomInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LiveService {
    @GET("/api/live/getRecommend")
    fun getRecommend(@Query("page") page: Int, @Query("size") size: Int): Call<LiveRoomResponse>
}