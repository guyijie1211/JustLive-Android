package com.sunnyweather.android.logic.network

import com.sunnyweather.android.logic.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LiveService {
    @GET("/api/live/getRecommend")
    fun getRecommend(@Query("page") page: Int, @Query("size") size: Int): Call<LiveRoomResponse>
    @GET("/api/live/getRecommendByPlatform")
    fun getRecommendByPlatform(@Query("platform") platform: String, @Query("page") page: Int, @Query("size") size: Int): Call<LiveRoomResponse>
    @GET("/api/live/getRecommendByPlatformArea")
    fun getRecommendByPlatformArea(@Query("platform") platform: String, @Query("area") area: String, @Query("page") page: Int, @Query("size") size: Int): Call<LiveRoomResponse>
    @GET("/api/live/getRecommendByAreaAll")
    fun getRecommendByAreaAll(@Query("areaType") areaType: String, @Query("area") area: String, @Query("page") page: Int): Call<LiveRoomResponse>
    @GET("/api/live/getRealUrl")
    fun getRealUrl(@Query("platform") platform: String, @Query("roomId") roomId: String): Call<UrlsResponse>
    @GET("/api/live/getRoomInfo")
    fun getRoomInfo(@Query("uid") uid: String, @Query("platform") platform: String, @Query("roomId") roomId: String): Call<RoomInfoResponse>
    @GET("/api/live/getRoomsOn")
    fun getRoomsOn(@Query("uid") uid: String): Call<LiveRoomResponse>
    @GET("/api/live/search")
    fun search(@Query("platform") platform: String, @Query("keyWords") keyWords: String, @Query("uid") uid: String): Call<SearchResponse>
    @GET("/api/live/getAllAreas")
    fun getAllAreas(): Call<AreaAllResponse>
    @GET("/api/live/follow")
    fun follow(@Query("platform") platform: String, @Query("roomId") roomId: String, @Query("uid") uid: String): Call<FollowResponse>
    @GET("/api/live/unFollow")
    fun unFollow(@Query("platform") platform: String, @Query("roomId") roomId: String, @Query("uid") uid: String): Call<FollowResponse>
    @GET("/api/live/versionUpdate")
    fun versionUpdate(): Call<UpdateResponse>
    @GET("/api/live/getBannerInfo")
    fun getBannerInfo(): Call<BannerInfoResponse>

    @POST("/api/login")
    fun login(@Query("username") username: String, @Query("password") password: String): Call<UserInfoResponse>
    @POST("/api/register")
    fun register(@Query("username") username: String, @Query("nickname") nickname: String,  @Query("password") password: String): Call<UserInfoResponse>
    @POST("/api/live/changeUserInfo")
    fun changeUserInfo(@Body userInfo: UserInfo): Call<UserInfoResponse>
}
