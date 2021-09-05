package com.sunnyweather.android.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.RoomInfo

class HomeViewModel : ViewModel() {
    private val pageLiveData = MutableLiveData<Int>()
    private var page = 0

    val roomList = ArrayList<RoomInfo>()
    val roomListLiveDate = Transformations.switchMap(pageLiveData) {
            page -> Repository.getRecommend(page, 20)
    }

    fun clearPage() {
        roomList.clear()
        page = 0
    }

    fun getRecommend() {
        Log.i("test", "getRecommend")
        page ++
        pageLiveData.value = page
    }
}