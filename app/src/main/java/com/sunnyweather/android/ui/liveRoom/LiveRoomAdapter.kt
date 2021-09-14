package com.sunnyweather.android.ui.liveRoom

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R

class LiveRoomAdapter(private val danmuList: ArrayList<LiveRoomViewModel.DanmuInfo>?) :
    RecyclerView.Adapter<LiveRoomAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val danmuName: TextView = view.findViewById(R.id.danMu_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.danmu_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LiveRoomAdapter.ViewHolder, position: Int) {
        val danmuInfo = danmuList?.get(position)
        if (danmuInfo != null) {
            val result = "<b><font color=\"black\">" + danmuInfo.userName + "：</font></b>" + danmuInfo.content
            holder.danmuName.text = Html.fromHtml(result)
        }
    }

    override fun getItemCount(): Int {
        if (danmuList != null) {
            return danmuList.size
        }
        return 0
    }
}