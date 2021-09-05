package com.sunnyweather.android.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.RoomInfo
import com.sunnyweather.android.ui.roomList.RoomListAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_roomlist.*

class HomeFragment : Fragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }
    private lateinit var adapter: RoomListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("test","homeCreate")
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = RoomListAdapter(this, viewModel.roomList)
        recyclerView.adapter = adapter
        swiperefresh.setOnRefreshListener {
            Log.i("test", "refresh")
            viewModel.getRecommend()
        }
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.platforms,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            platform_spinner.adapter = adapter
        }
        if (viewModel.roomList.size < 5) {
            viewModel.roomListLiveDate.observe(viewLifecycleOwner, { result ->
                val rooms: ArrayList<RoomInfo> = result.getOrNull() as ArrayList<RoomInfo>
                if (rooms != null) {
                    Log.i("test", "getRecommendFinish")
                    if (swiperefresh.isRefreshing) {
                        viewModel.clearPage()
                    }
                    viewModel.roomList.addAll(rooms)
                    adapter.notifyDataSetChanged()
                    Log.i("test", swiperefresh.isRefreshing.toString())
                    swiperefresh.isRefreshing = false
                } else {
                    Toast.makeText(activity, "没有更多直播间", Toast.LENGTH_SHORT).show()
                    result.exceptionOrNull()?.printStackTrace()
                }
            })
            swiperefresh.isRefreshing = true
            viewModel.getRecommend()
        }
    }
}