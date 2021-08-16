package com.sunnyweather.android.ui.roomList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.RoomInfo

class RoomListViewModel : ViewModel() {
    private val pageLiveData = MutableLiveData<Int>()
    var pageNow = 1

    val roomList = ArrayList<RoomInfo>()

    val roomListLiveDate = Transformations.switchMap(pageLiveData) {
        page -> Repository.getRecommend(page, 20)
    }

    fun getRecommend() {
        pageLiveData.value = pageNow++
    }
}