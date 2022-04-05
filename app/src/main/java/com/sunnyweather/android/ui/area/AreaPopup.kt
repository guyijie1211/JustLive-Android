package com.sunnyweather.android.ui.area

import android.content.Context
import android.content.SharedPreferences
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.lxj.xpopup.core.BottomPopupView
import com.paulrybitskyi.persistentsearchview.adapters.model.SuggestionItem
import com.paulrybitskyi.persistentsearchview.listeners.OnSearchConfirmedListener
import com.paulrybitskyi.persistentsearchview.listeners.OnSearchQueryChangeListener
import com.paulrybitskyi.persistentsearchview.listeners.OnSuggestionChangeListener
import com.paulrybitskyi.persistentsearchview.utils.SuggestionCreationUtil
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.AreaInfo
import com.sunnyweather.android.ui.roomList.SpaceItemDecoration
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_area.view.*
import kotlinx.android.synthetic.main.fragment_arealist.*
import java.lang.StringBuilder
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupPosition
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.logic.model.AreaFollow

class AreaPopup(context: Context) : BottomPopupView(context), View.OnClickListener {
    private var areaMap = HashMap<String, ArrayList<JSONObject>>()
    private var areaTypeList = ArrayList<String>()
    private lateinit var viewPager: ViewPager2
    private lateinit var sharedPref: SharedPreferences
    private lateinit var historySearchList: ArrayList<String>
    private var searchList = ArrayList<AreaInfo>()
    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: AreaSearchAdapter

    override fun getImplLayoutId(): Int {
        return R.layout.fragment_area
    }

    override fun onCreate() {
        super.onCreate()
        recyclerView = findViewById(R.id.recyclerView_search_area)
        var cardNum = ScreenUtils.getAppScreenWidth()/ ConvertUtils.dp2px(129F)
        if (cardNum < 2) cardNum = 2
        val layoutManager = GridLayoutManager(context, cardNum)
        recyclerView.addItemDecoration(SpaceItemDecoration(10))
        recyclerView.layoutManager = layoutManager
        recyclerView.grid(cardNum).setup {
            addType<AreaInfo>(R.layout.area_item)
            onBind {
                findView<TextView>(R.id.areaName).text = getModel<AreaInfo>().areaName
                Glide.with(context).load(getModel<AreaInfo>().areaPic)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(findView(R.id.areaPic))
            }
            onClick(R.id.area_item) {
                val areaInfo = searchList[absoluteAdapterPosition]
                (context as MainActivity).onFragment(areaInfo.typeName, areaInfo.areaName)
            }
            onLongClick(R.id.area_item) {
                val areaInfo = searchList[absoluteAdapterPosition]
                XPopup.Builder(getContext())
                    .atView(findView(R.id.area_item)) // 依附于所点击的View，内部会自动判断在上方或者下方显示
                    .popupPosition(PopupPosition.Top)
                    .asAttachList(
                        arrayOf("收藏:" + areaInfo.areaName), null
                    ) { _, _ ->
                        var followListString = sharedPref.getString("areaFollow","[{\"areaName\":\"全部推荐\",\"areaType\":\"all\"}]")
                        var followList = JSONArray.parseArray(followListString, AreaFollow::class.java)
                        followList.forEach { follow ->
                            if (follow.areaName == areaInfo.areaName) {
                                ToastUtils.showShort("已收藏")
                                return@asAttachList
                            }
                        }
                        var areaFollow = AreaFollow()
                        areaFollow.areaName = areaInfo.areaName
                        areaFollow.areaType = areaInfo.typeName
                        followList.add(areaFollow)
                        sharedPref.edit().putString("areaFollow", JSON.toJSONString(followList)).commit()
                        ToastUtils.showShort("${areaInfo.areaName} 加入收藏")
                    }.show()
            }
        }.models = searchList

        recyclerView_search_area.visibility = View.GONE
        tab_area.visibility = View.VISIBLE
        viewpage_area.visibility = View.VISIBLE
        //ViewPager2
        viewPager = viewpage_area
        initSearch()
        val searchQueries = if(area_search.isInputQueryEmpty) {
            getInitialSearchQueries()
        } else {
            getSuggestionsForQuery(area_search.inputQuery)
        }
        setSuggestions(searchQueries, false)

        area_follow_button.setOnClickListener {
            XPopup.Builder(context)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .atView(area_follow_button)
                .isViewMode(true)
                .maxHeight(ScreenUtils.getAppScreenHeight() / 2)
                .asCustom(AreaFollowAttach(context))
                .show()
        }
        scopeNetLife { // 创建作用域
            val url = "http://yj1211.work:8013/api/live/getAllAreas"
            val data = Get<String>(url) // 发起GET请求并返回`String`类型数据
            var resultJson: JSONArray = JSONObject.parseObject(data.await()).getJSONArray("data")
            var areas: List<List<*>> = JSON.parseArray(resultJson.toJSONString(), List::class.java)
            if (areas != null) {
                sortArea(areas)
                val pagerAdapter = ScreenSlidePagerAdapter(context as FragmentActivity)
                for (areaType in areaTypeList) {
                    val textView = TextView(context)
                    textView.text = areaType
                    textView.gravity = Gravity.CENTER
                    tab_area.addView(textView)
                }
                viewPager.adapter = pagerAdapter
                //tabLayout
                ViewPager2Delegate.install(viewPager, tab_area)
            } else {
                ToastUtils.showShort("没有更多直播间")
            }
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return areaMap.size
        }
        override fun createFragment(position: Int): Fragment {
            return  AreaSingleFragment(areaMap[areaTypeList[position]]!!)
        }
    }

    private fun initSearch() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val tempString = sharedPref.getString("historySearchArea", "")
        historySearchList = if (tempString == "") {
            ArrayList()
        } else {
            val temp = java.util.ArrayList(tempString.toString().trim().split(","))
            temp
        }
        initSearchView()
//        initRecyclerView()
    }

    private fun initSearchView() = with(area_search) {
        setOnLeftBtnClickListener(this@AreaPopup)
        setOnClearInputBtnClickListener(this@AreaPopup)
        setOnSearchConfirmedListener(mOnSearchConfirmedListener) //提交搜索监听
        setOnSearchQueryChangeListener(mOnSearchQueryChangeListener)//输入监听
        setOnSuggestionChangeListener(mOnSuggestionChangeListener)  //选择历史记录或删除
        setDismissOnTouchOutside(true)
        setDimBackground(true)
        isProgressBarEnabled = true
        isClearInputButtonEnabled = true
        setSuggestionsDisabled(false)
        setQueryInputGravity(Gravity.START or Gravity.CENTER)
        setQueryInputHint("分区搜索(长按分区可收藏)")
    }

    private val mOnSearchConfirmedListener = OnSearchConfirmedListener { searchView, query ->
        if (query.isBlank()) return@OnSearchConfirmedListener
        saveQuery(query)//保存历史记录
        searchView.collapse()//折叠历史记录
        performSearch(query)//搜索
    }

    private val mOnSearchQueryChangeListener = OnSearchQueryChangeListener { searchView, oldQuery, newQuery ->
        setSuggestions(
            if(newQuery.isBlank()) {
                getInitialSearchQueries()
            } else {
                getSuggestionsForQuery(newQuery)
            },
            true
        )
    }

    //选择历史记录或删除
    private val mOnSuggestionChangeListener = object : OnSuggestionChangeListener {
        override fun onSuggestionPicked(suggestion: SuggestionItem) {
            val query = suggestion.itemModel.text
            setSuggestions(getSuggestionsForQuery(query), false)
            performSearch(query)
        }
        override fun onSuggestionRemoved(suggestion: SuggestionItem) {
            removeSearchQuery(suggestion.itemModel.text)
        }
    }

    //删除历史记录
    private fun removeSearchQuery(query: String) {
        historySearchList.remove(query)
        sharedPref.edit().putString("historySearchArea", listToString(historySearchList, ',')).commit()
    }

    private fun getInitialSearchQueries(): List<String> {
        return historySearchList
    }

    private fun getSuggestionsForQuery(query: String): List<String> {
        val resultList = ArrayList<String>()

        if(query.isEmpty()) {
            return historySearchList
        } else {
            historySearchList.forEach {
                if(it.lowercase().startsWith(query.lowercase())) {
                    resultList.add(it)
                }
            }
        }

        return resultList
    }

    //设置搜索结果
    private fun setSuggestions(queries: List<String>, expandIfNecessary: Boolean) {
        val suggestions: List<SuggestionItem> = SuggestionCreationUtil.asRecentSearchSuggestions(queries)
        area_search.setSuggestions(suggestions, expandIfNecessary)
    }

    //保存历史记录
    private fun saveQuery(query: String) {
        if (!historySearchList.contains(query)) {
            historySearchList.add(0, query)
        }
        if (historySearchList.size > 8) {
            historySearchList.removeLast()
        }
        val historyString = listToString(historySearchList, ',')
        sharedPref.edit().putString("historySearchArea", historyString).commit()
    }

    //搜索
    private fun performSearch(query: String) {
        searchList.clear()
        recyclerView.adapter?.notifyDataSetChanged()
        //隐藏分类
        tab_area.visibility = View.GONE
        viewpage_area.visibility = View.GONE
        //搜索view
        recyclerView_search_area.visibility = View.VISIBLE
        area_search.hideProgressBar(false)
        area_search.showLeftButton()
        areaMap.values.forEach { list ->
            list.forEach { area ->
                if (area.getString("areaName").lowercase().contains(query.lowercase())) {
                    var areaInfo = AreaInfo(
                        area.getString("platform"),
                        area.getString("areaType"),
                        area.getString("typeName"),
                        area.getString("areaId"),
                        area.getString("areaName"),
                        area.getString("areaPic"),
                        ""
                    )
                    searchList.add(areaInfo)
                }
            }
        }
    }

    private fun sortArea(areaList: List<List<*>>){
        var areaInfoListTemp: ArrayList<JSONObject>
        var areaType: String
        for (areaInfoList in areaList) {
            areaInfoList as ArrayList<JSONObject>
            areaInfoListTemp = ArrayList()
            areaType = areaInfoList[0].getString("typeName")
            areaTypeList.add(areaType)
            areaInfoListTemp.addAll(areaInfoList)
            areaMap[areaType] = areaInfoListTemp
        }
    }

    private fun listToString(list: List<String>, separator: Char): String? {
        if (list.isEmpty()) {
            return ""
        }
        val sb = StringBuilder()
        for (i in list.indices) {
            sb.append(list[i]).append(separator)
        }
        return sb.toString().substring(0, sb.toString().length - 1)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.leftBtnIv -> {
                if (tab_area.visibility == View.VISIBLE) {
                    this.dismiss()
                } else {
                    tab_area.visibility = View.VISIBLE
                    viewpage_area.visibility = View.VISIBLE
                    recyclerView_search_area.visibility = View.GONE
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        if (tab_area.visibility == View.VISIBLE) {
            this.dismiss()
        } else {
            tab_area.visibility = View.VISIBLE
            viewpage_area.visibility = View.VISIBLE
            recyclerView_search_area.visibility = View.GONE
        }
        return true
    }
}