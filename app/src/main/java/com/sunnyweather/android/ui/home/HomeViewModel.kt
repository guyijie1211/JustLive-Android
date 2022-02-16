package com.sunnyweather.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.drake.statelayout.StateLayout
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.RoomInfo

class HomeViewModel(val platform: String) : ViewModel() {
    class RecommendInfo(val platform: String, val areaType: String, val area: String, val page: Int, val size: Int)

    private val pageLiveData = MutableLiveData<RecommendInfo>()
    private var page = 0

    val roomList = ArrayList<RoomInfo>()
    val roomListLiveDate = Transformations.switchMap(pageLiveData) {
            value -> getRecommendSelect(value.platform, value.areaType, value.area, value.page, value.size)
    }

    fun clearPage() {
        page = 0
    }

    fun clearList() {
        roomList.clear()
    }

    fun getRecommend(areaType: String, area: String, state: StateLayout) {
        state.showContent()
        page ++
        pageLiveData.value = RecommendInfo(platform, areaType, area, page, 20)
    }

    private fun getRecommendSelect(platform: String, areaType: String, area: String, page: Int, size: Int): LiveData<Result<Any>> {
        return if (area == "all") {
            if (platform == "all") {
                Repository.getRecommend(page, size)
            } else {
                Repository.getRecommendByPlatform(platform, page, size)
            }
        } else {
            if (platform == "all") {
                Repository.getRecommendByAreaAll(areaType, area, page)
            } else {
                Repository.getRecommendByPlatformArea(platform, area, page, size)
            }
        }
    }
}