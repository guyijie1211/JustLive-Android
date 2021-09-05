package com.sunnyweather.android.ui.liveRoom

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository

class LiveRoomViewModel : ViewModel() {
    class UrlRequest (val platform: String, val roomId: String)
    class RoomInfoRequest (val uid: String, val platform: String, val roomId: String)

    private val urlsRequestData = MutableLiveData<UrlRequest>()
    private val roomInfoRequestData = MutableLiveData<RoomInfoRequest>()

    val urlResponseData = Transformations.switchMap(urlsRequestData) {
        value -> Repository.getRealUrl(value.platform, value.roomId)
    }
    val roomInfoResponseData = Transformations.switchMap(roomInfoRequestData) {
            value -> Repository.getRoomInfo(value.uid, value.platform, value.roomId)
    }

    fun getRealUrl(platform: String, roomId: String) {
        println("ppp$platform$roomId")
        urlsRequestData.value = UrlRequest(platform, roomId)
    }

    fun getRoomInfo(uid: String, platform: String, roomId: String) {
        roomInfoRequestData.value = RoomInfoRequest(uid, platform, roomId)
    }
}