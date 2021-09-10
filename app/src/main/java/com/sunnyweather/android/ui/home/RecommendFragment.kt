package com.sunnyweather.android.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.RoomInfo
import com.sunnyweather.android.ui.roomList.RoomListAdapter
import com.sunnyweather.android.ui.roomList.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_roomlist.*

import com.scwang.smart.refresh.footer.ClassicsFooter

import com.scwang.smart.refresh.header.ClassicsHeader


class RecommendFragment(val platform: String) : Fragment()  {
    private val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }
    private lateinit var adapter: RoomListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_roomlist, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i("test", platform)
        val layoutManager = GridLayoutManager(context, 2)
        recyclerView.addItemDecoration(SpaceItemDecoration(10))
        recyclerView.layoutManager = layoutManager
        adapter = RoomListAdapter(this, viewModel.roomList)
        recyclerView.adapter = adapter
        progressBar_roomList.isVisible = true
        //下拉刷新，加载更多
        refresh_home.setRefreshHeader(ClassicsHeader(context))
        refresh_home.setRefreshFooter(ClassicsFooter(context))
        refresh_home.setOnRefreshListener {
            viewModel.clearPage()
            viewModel.getRecommend(platform, SunnyWeatherApplication.areaType.value?:"all", SunnyWeatherApplication.areaName.value?:"all")
        }
        refresh_home.setOnLoadMoreListener {
            viewModel.getRecommend(platform, SunnyWeatherApplication.areaType.value?:"all", SunnyWeatherApplication.areaName.value?:"all")
        }
        //绑定LiveData监听器
        if (viewModel.roomList.size < 1) {
            SunnyWeatherApplication.areaName.observe(viewLifecycleOwner, {
                viewModel.clearPage()
                progressBar_roomList.isVisible = true
                recyclerView.isGone = true
                viewModel.getRecommend(platform, SunnyWeatherApplication.areaType.value?:"all", SunnyWeatherApplication.areaName.value?:"all")
            })
            viewModel.roomListLiveDate.observe(viewLifecycleOwner, { result ->
                Log.i("test", "listChanged")
                val temp = result.getOrNull()
                var rooms: ArrayList<RoomInfo>? = null
                if (temp != null) rooms = temp as ArrayList<RoomInfo>
                if (rooms != null) {
                    viewModel.roomList.addAll(rooms)
                    adapter.notifyDataSetChanged()
                    progressBar_roomList.isGone = true
                    recyclerView.isVisible = true
                    refresh_home.finishRefresh() //传入false表示刷新失败
                    refresh_home.finishLoadMore() //传入false表示加载失败
                } else {
                    refresh_home.finishLoadMoreWithNoMoreData()
                    result.exceptionOrNull()?.printStackTrace()
                }
            })
            viewModel.getRecommend(platform, SunnyWeatherApplication.areaType.value?:"all", SunnyWeatherApplication.areaName.value?:"all")
        }
    }
}