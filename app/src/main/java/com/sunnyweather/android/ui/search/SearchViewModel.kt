package com.sunnyweather.android.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Owner


class SearchViewModel : ViewModel(){
    class SearchRequest (val platform: String, val keyWords: String, val isLive: String)

    private val searchWordLiveData = MutableLiveData<SearchRequest>()
    var ownersList = ArrayList<Owner>()
    val ownerListLiveData = Transformations.switchMap(searchWordLiveData) {
        value -> Repository.search(value.platform, value.keyWords, value.isLive)
    }

    fun search(platform: String, keyWords: String, isLive: String) {
        searchWordLiveData.value = SearchRequest(platform, keyWords, isLive)
    }

    fun clearList() {
        ownersList.clear()
    }
}