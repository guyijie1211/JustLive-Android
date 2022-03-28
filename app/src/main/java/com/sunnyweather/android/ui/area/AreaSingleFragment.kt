package com.sunnyweather.android.ui.area

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.AreaFollow
import com.sunnyweather.android.ui.roomList.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_arealist.*
import java.lang.IllegalArgumentException

class AreaSingleFragment(private val areaList: List<JSONObject>) : Fragment() {
    constructor() : this(ArrayList<JSONObject>())

    private lateinit var mFragmentListener: FragmentListener
    private lateinit var sharedPref: SharedPreferences

    private lateinit var adapter: AreaListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_arealist, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        var cardNum = ScreenUtils.getAppScreenWidth()/ ConvertUtils.dp2px(129F)
        if (cardNum < 2) cardNum = 2
        val layoutManager = GridLayoutManager(context, cardNum)
        recyclerView_area.addItemDecoration(SpaceItemDecoration(10))
        recyclerView_area.layoutManager = layoutManager
        adapter = AreaListAdapter(this, areaList)
        recyclerView_area.adapter = adapter
    }

    fun saveArea(typeName:String, areaName:String) {
        var followListString = sharedPref.getString("areaFollow","[{\"areaName\":\"全部推荐\",\"areaType\":\"all\"}]")
        var followList = JSONArray.parseArray(followListString, AreaFollow::class.java)
        followList.forEach { follow ->
            if (follow.areaName == areaName) {
                ToastUtils.showShort("已收藏")
                return
            }
        }
        var areaFollow = AreaFollow()
        areaFollow.areaName = areaName
        areaFollow.areaType = typeName
        followList.add(areaFollow)
        sharedPref.edit().putString("areaFollow", JSON.toJSONString(followList)).commit()
        ToastUtils.showShort("$areaName 加入收藏")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mFragmentListener = if (context is FragmentListener) {
            context
        } else {
            throw IllegalArgumentException("Activity must implements FragmentListener")
        }
    }

    fun selectArea(areaType: String, areaName: String) {
        mFragmentListener.onFragment(areaType, areaName)
    }

    interface FragmentListener {
        fun onFragment(areaType: String, areaName: String)
    }
}