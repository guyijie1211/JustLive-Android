package com.sunnyweather.android.logic.network

import com.sunnyweather.android.logic.model.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LiveService {
    @GET("/api/live/getRecommend")
    fun getRecommend(@Query("page") page: Int, @Query("size") size: Int): Call<LiveRoomResponse>
    @GET("/api/live/getRecommendByPlatform")
    fun getRecommendByPlatform(@Query("platform") platform: String, @Query("page") page: Int, @Query("size") size: Int): Call<LiveRoomResponse>
    @GET("/api/live/getRealUrl")
    fun getRealUrl(@Query("platform") platform: String, @Query("roomId") roomId: String): Call<UrlsResponse>
    @GET("/api/live/getRoomInfo")
    fun getRoomInfo(@Query("uid") uid: String, @Query("platform") platform: String, @Query("roomId") roomId: String): Call<RoomInfoResponse>
    @GET("/api/live/getRoomsOn")
    fun getRoomsOn(@Query("uid") uid: String): Call<LiveRoomResponse>
    @GET("/api/live/search")
    fun search(@Query("platform") platform: String, @Query("keyWords") keyWords: String, @Query("isLive") isLive: String): Call<SearchResponse>
    @GET("/api/live/getAllAreas")
    fun getAllAreas(): Call<AreaAllResponse>
}
