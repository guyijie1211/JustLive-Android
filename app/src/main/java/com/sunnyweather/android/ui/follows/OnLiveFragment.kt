package com.sunnyweather.android.ui.follows

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.scwang.smart.refresh.header.ClassicsHeader
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.RoomInfo
import com.sunnyweather.android.ui.roomList.RoomListAdapter
import com.sunnyweather.android.ui.roomList.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_roomlist.*

class OnLiveFragment(private val isLive: Boolean) : Fragment() {
    constructor() : this(true)
    private val viewModel by lazy { ViewModelProvider(this).get(FollowViewModel::class.java) }
    private lateinit var adapterOn: RoomListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_roomlist, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        val multiStateContainer = recyclerView.bindMultiState()
//        multiStateContainer.isShown
        if (SunnyWeatherApplication.isLogin.value!!){
            progressBar_roomList.isVisible = true
        }
        val layoutManager = GridLayoutManager(context, 2)
        recyclerView.addItemDecoration(SpaceItemDecoration(10))
        recyclerView.layoutManager = layoutManager
        adapterOn = RoomListAdapter(this, viewModel.roomList)
        recyclerView.adapter = adapterOn
        //下拉刷新，加载更多
        refresh_home.setRefreshHeader(ClassicsHeader(context))
//        refresh_home_foot.visibility = View.GONE
        refresh_home.finishLoadMoreWithNoMoreData()
        refresh_home.setOnRefreshListener {
            viewModel.clearRoomList()
            viewModel.getRoomsOn(SunnyWeatherApplication.userInfo?.uid)
        }

        if (viewModel.roomList.size < 1) {
            SunnyWeatherApplication.isLogin.observe(viewLifecycleOwner, {result ->
                if (result){
                    progressBar_roomList.isVisible = true
                    viewModel.getRoomsOn(SunnyWeatherApplication.userInfo?.uid)
                } else {
                    viewModel.clearRoomList()
                    adapterOn.notifyDataSetChanged()
                }
            })
            viewModel.userRoomListLiveDate.observe(viewLifecycleOwner, { result ->
                val rooms: ArrayList<RoomInfo> = result.getOrNull() as ArrayList<RoomInfo>
                if (rooms != null) {
                    sortRooms(rooms)
                    adapterOn.notifyDataSetChanged()
                    progressBar_roomList.isGone = true
                    refresh_home.finishRefresh() //传入false表示刷新失败
                    refresh_home.finishLoadMoreWithNoMoreData()
                } else {
                    refresh_home.finishLoadMoreWithNoMoreData()
                    result.exceptionOrNull()?.printStackTrace()
                }
            })
        }
        viewModel.getRoomsOn(SunnyWeatherApplication.userInfo?.uid)
    }

    private fun sortRooms(roomList: List<RoomInfo>) {
        for (roomInfo in roomList) {
            if (this.isLive == (roomInfo.isLive == 1)) {
                val test = roomInfo.isLive
                viewModel.roomList.add(roomInfo)
            }
        }
    }
}