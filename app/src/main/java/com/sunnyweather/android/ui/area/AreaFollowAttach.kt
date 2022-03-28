package com.sunnyweather.android.ui.area

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.drake.brv.BindingAdapter
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lxj.xpopup.core.AttachPopupView
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.AreaFollow
import com.sunnyweather.android.ui.roomList.SpaceItemDecoration

class AreaFollowAttach(context: Context) : AttachPopupView(context) {
    private var sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getImplLayoutId(): Int {
        return R.layout.attach_area_follow
    }

    override fun onCreate() {
        super.onCreate()
        var followListString = sharedPref.getString("areaFollow","[{\"areaName\":\"全部推荐\",\"areaType\":\"all\"}]")
        var followList = JSONArray.parseArray(followListString, AreaFollow::class.java)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_area_follow)
        recyclerView.addItemDecoration(SpaceItemDecoration(10))
        recyclerView.linear().setup {
            addType<AreaFollow>(R.layout.area_follow_item)
            itemTouchHelper = ItemTouchHelper(object : DefaultItemTouchCallback() {
                /**
                 * 当拖拽动作完成且松开手指时触发
                 */
                override fun onDrag(source: BindingAdapter.BindingViewHolder, target: BindingAdapter.BindingViewHolder) {
                    // 这是拖拽交换后回调, 这里可以同步服务器
                    sharedPref.edit().putString("areaFollow", JSON.toJSONString(followList)).commit()
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // 这是侧滑删除后回调, 这里可以同步服务器
                    followList.removeAt(viewHolder.layoutPosition)
                    notifyItemRemoved(viewHolder.layoutPosition)
                    sharedPref.edit().putString("areaFollow", JSON.toJSONString(followList)).commit()
                }
            })
            onBind {
                findView<View>(R.id.area_follow_sort).setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) { // 如果手指按下则开始拖拽
                        itemTouchHelper?.startDrag(this)
                    }
                    true
                }
            }
            R.id.area_follow_card.onClick {
                val areaInfo = followList[adapterPosition] as AreaFollow
                (context as MainActivity).onFragment(areaInfo.areaType, areaInfo.areaName)
                dismiss()
            }
        }.models = followList
    }
}