package com.sunnyweather.android.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.stx.xhb.xbanner.XBanner
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.BannerInfo
import com.sunnyweather.android.logic.model.RoomInfo
import com.sunnyweather.android.ui.roomList.RoomListAdapter
import com.sunnyweather.android.ui.roomList.SpaceItemDecoration
import kotlinx.android.synthetic.main.ad_head.banner
import kotlinx.android.synthetic.main.fragment_roomlist.progressBar_roomList
import kotlinx.android.synthetic.main.fragment_roomlist.recyclerView
import kotlinx.android.synthetic.main.fragment_roomlist.refresh_home
import kotlinx.android.synthetic.main.fragment_roomlist.refresh_home_foot
import kotlinx.android.synthetic.main.fragment_roomlist.state

class RecommendFragment(val platform: String) : Fragment()  {
    constructor(): this("all")
    private val viewModel by lazy { ViewModelProvider(this, HomeViewModelFactory(platform)).get(HomeViewModel::class.java) }
    private lateinit var adapter: RoomListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_roomlist, container, false)
    }

    class MySpanSizeLookup(private val layoutManager: GridLayoutManager, val platform: String) : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (position == 0 && platform == "all") layoutManager.spanCount else 1
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var cardNum = ScreenUtils.getAppScreenWidth()/ConvertUtils.dp2px(195F)
        if (cardNum < 2) cardNum = 2
        val layoutManager = GridLayoutManager(context, cardNum)
        layoutManager.spanSizeLookup = MySpanSizeLookup(layoutManager, platform)
        recyclerView.addItemDecoration(SpaceItemDecoration(10))
        recyclerView.layoutManager = layoutManager
        adapter = RoomListAdapter(this, viewModel.roomList)
        recyclerView.adapter = adapter
        //获取轮播控件
        // 初始化HeaderView
        if (platform == "all") {
            val headerView = View.inflate(context, R.layout.ad_head, null)
            adapter.setHeaderView(headerView)
        }
        //下拉刷新，加载更多
        refresh_home_foot.setFinishDuration(0)//设置Footer 的 “加载完成” 显示时间为0
        refresh_home.setOnRefreshListener {
            viewModel.clearPage()
            viewModel.getRecommend(SunnyWeatherApplication.areaType.value?:"all", SunnyWeatherApplication.areaName.value?:"all", state)
        }
        refresh_home.setOnLoadMoreListener {
            viewModel.getRecommend(SunnyWeatherApplication.areaType.value?:"all", SunnyWeatherApplication.areaName.value?:"all", state)
        }
        //绑定LiveData监听器
        SunnyWeatherApplication.areaName.observe(viewLifecycleOwner) {
            viewModel.clearPage()
            viewModel.clearList()
            progressBar_roomList.isVisible = true
            recyclerView.isGone = true
            viewModel.getRecommend(
                SunnyWeatherApplication.areaType.value ?: "all",
                SunnyWeatherApplication.areaName.value ?: "all",
                state
            )
        }
        viewModel.bannerInfoListDate.observe(viewLifecycleOwner) { result ->
            val temp = result.getOrNull()
            var bannerInfoList: ArrayList<BannerInfo>? = null
            if (temp != null) bannerInfoList = temp as ArrayList<BannerInfo>
            Log.i("====>log", bannerInfoList.toString())
            if (bannerInfoList != null && bannerInfoList.size > 0) {
                adapter.setBannerInfoList(bannerInfoList)
            }
        }
        viewModel.roomListLiveDate.observe(viewLifecycleOwner) { result ->
            val temp = result.getOrNull()
            var rooms: ArrayList<RoomInfo>? = null
            if (temp != null) rooms = temp as ArrayList<RoomInfo>
            if (rooms != null && rooms.size > 0) {
                if (refresh_home.isRefreshing) {
                    viewModel.clearList()
                }
                viewModel.roomList.addAll(rooms)
                adapter.notifyDataSetChanged()
                progressBar_roomList.isGone = true
                recyclerView.isVisible = true
                refresh_home.finishRefresh() //传入false表示刷新失败
                refresh_home.finishLoadMore() //传入false表示加载失败
            } else {
                progressBar_roomList.isGone = true
                recyclerView.isVisible = true
                refresh_home.finishLoadMoreWithNoMoreData()
                if (viewModel.roomList.size == 0) {
                    state.showEmpty()
                }
                result.exceptionOrNull()?.printStackTrace()
            }
        }
        viewModel.getRecommend(SunnyWeatherApplication.areaType.value?:"all", SunnyWeatherApplication.areaName.value?:"all", state)
        viewModel.getBannerInfo()
        progressBar_roomList.isVisible = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SunnyWeatherApplication.areaName.removeObservers(viewLifecycleOwner)
        viewModel.roomListLiveDate.removeObservers(viewLifecycleOwner)
    }
}