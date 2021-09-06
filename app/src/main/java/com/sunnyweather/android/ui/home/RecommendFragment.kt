package com.sunnyweather.android.ui.home

import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_roomlist.*

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
        val layoutManager = GridLayoutManager(context, 2)
        recyclerView.addItemDecoration(SpaceItemDecoration(10))
        recyclerView.layoutManager = layoutManager
        adapter = RoomListAdapter(this, viewModel.roomList)

        swiperefresh.isRefreshing = true
        recyclerView.adapter = adapter

        if (viewModel.roomList.size < 1) {
            viewModel.roomListLiveDate.observe(viewLifecycleOwner, { result ->
                val rooms: ArrayList<RoomInfo> = result.getOrNull() as ArrayList<RoomInfo>
                if (rooms != null) {
                    if (swiperefresh.isRefreshing) {
                        viewModel.clearPage()
                    }
                    viewModel.roomList.addAll(rooms)
                    adapter.notifyDataSetChanged()
                    swiperefresh.isRefreshing = false
                } else {
                    Toast.makeText(activity, "没有更多直播间", Toast.LENGTH_SHORT).show()
                    result.exceptionOrNull()?.printStackTrace()
                }
            })
            swiperefresh.isRefreshing = true
            viewModel.getRecommend(platform)
        }
    }
}