package com.sunnyweather.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.RoomInfo

class HomeViewModel : ViewModel() {
    class RecommendInfo(val platform: String, val page: Int, val size: Int)

    private val pageLiveData = MutableLiveData<RecommendInfo>()
    private var page = 0

    val roomList = ArrayList<RoomInfo>()
    val roomListLiveDate = Transformations.switchMap(pageLiveData) {
            value -> getRecommendSelect(value.platform, value.page, value.size)
    }

    fun clearPage() {
        roomList.clear()
        page = 0
    }

    fun getRecommend(platform: String) {
        page ++
        pageLiveData.value = RecommendInfo(platform, page, 20)
    }

    private fun getRecommendSelect(platform: String, page: Int, size: Int): LiveData<Result<List<RoomInfo>>> {
        return if (platform == "all") {
            Repository.getRecommend(page, size)
        } else {
            Repository.getRecommendByPlatform(platform, page, size)
        }
    }
}