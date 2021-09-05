package com.sunnyweather.android.ui.follows

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.RoomInfo
import com.sunnyweather.android.ui.roomList.RoomListAdapter
import com.sunnyweather.android.ui.roomList.SpaceItemDecoration
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.android.synthetic.main.fragment_roomlist.*

class OffLiveFragment  : Fragment() {
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
        val layoutManager = GridLayoutManager(context, 2)
        recyclerView.addItemDecoration(SpaceItemDecoration(10))
        recyclerView.layoutManager = layoutManager
//        if (isLive) {
//            adapterOn = RoomListAdapter(this, viewModel.roomListOn)
//        } else {
//            adapterOn = RoomListAdapter(this, viewModel.roomListOff)
//        }

        adapterOn = RoomListAdapter(this, viewModel.roomListOff)
        swiperefresh.isRefreshing = true
        recyclerView.adapter = adapterOn
//        recyclerView.adapter = ScaleInAnimationAdapter(adapterOn).apply {
//            // Change the durations.
//            setDuration(250)
//            // Disable the first scroll mode.
//            setFirstOnly(true)
//        }

        if (viewModel.roomListOn.size + viewModel.roomListOff.size < 1) {
            viewModel.userRoomListLiveDate.observe(viewLifecycleOwner, { result ->
                val rooms: ArrayList<RoomInfo> = result.getOrNull() as ArrayList<RoomInfo>
                if (rooms != null) {
                    if (swiperefresh.isRefreshing) {
                        viewModel.clearRoomList()
                    }
                    sortRooms(rooms)
                    adapterOn.notifyDataSetChanged()
                    swiperefresh.isRefreshing = false
                } else {
                    Toast.makeText(activity, "没有更多直播间", Toast.LENGTH_SHORT).show()
                    result.exceptionOrNull()?.printStackTrace()
                }
            })
            Log.i("test","roomOn")
            viewModel.getRoomsOn("0eb26a33e68d4582858a74abf5a645d5")
        }

        swiperefresh.setOnRefreshListener {
            viewModel.getRoomsOn("0eb26a33e68d4582858a74abf5a645d5")
        }
    }

    fun sortRooms(roomList: List<RoomInfo>) {
        for (roomInfo in roomList) {
            if (roomInfo.isLive == 1) {
                viewModel.roomListOn.add(roomInfo)
            } else {
                viewModel.roomListOff.add(roomInfo)
            }
        }
    }
}