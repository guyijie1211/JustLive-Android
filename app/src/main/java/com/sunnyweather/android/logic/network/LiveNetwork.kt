package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object LiveNetwork {
    private val liveService = ServiceCreator.create(LiveService::class.java)

    suspend fun getRecommend(page: Int, size: Int) = liveService.getRecommend(page, size).await()
    suspend fun getRecommendByPlatform(platform: String, page: Int, size: Int) = liveService.getRecommendByPlatform(platform, page, size).await()
    suspend fun getRecommendByPlatformArea(platform: String, area: String, page: Int, size: Int) = liveService.getRecommendByPlatformArea(platform, area, page, size).await()
    suspend fun getRecommendByAreaAll(areaType: String, area: String, page: Int) = liveService.getRecommendByAreaAll(areaType, area, page).await()
    suspend fun getRealUrl(platform: String, roomId: String) = liveService.getRealUrl(platform, roomId).await()
    suspend fun getRoomInfo(uid: String, platform: String, roomId: String) = liveService.getRoomInfo(uid, platform, roomId).await()
    suspend fun getRoomsOn(uid: String) = liveService.getRoomsOn(uid).await()
    suspend fun Search(platform: String, keyWords: String, isLive: String) = liveService.search(platform, keyWords, isLive).await()
    suspend fun getAllAreas() = liveService.getAllAreas().await()
    suspend fun login(username: String, password: String) = liveService.login(username, password).await()
    suspend fun register(username: String, nickname: String, password: String) = liveService.register(username, nickname, password).await()
    suspend fun follow(platform: String, roomId: String, uid: String) = liveService.follow(platform, roomId, uid).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}