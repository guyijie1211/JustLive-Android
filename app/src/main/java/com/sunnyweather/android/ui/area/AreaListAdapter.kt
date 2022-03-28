package com.sunnyweather.android.ui.area

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.sunnyweather.android.R
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupPosition



class AreaListAdapter(private val fragment: AreaSingleFragment, private val areaList: List<JSONObject>) :
    RecyclerView.Adapter<AreaListAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val areaPic: ImageView = view.findViewById(R.id.areaPic)
        val areaName: TextView = view.findViewById(R.id.areaName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.area_item, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val positionIndex = holder.layoutPosition
            val areaInfo = areaList[positionIndex]
            fragment.selectArea(areaInfo.getString("typeName"), areaInfo.getString("areaName"))
        }
        val builder = XPopup.Builder(fragment.context)
            .watchView(holder.itemView)
            .popupPosition(PopupPosition.Top)
        holder.itemView.setOnLongClickListener {
            val positionIndex = holder.layoutPosition
            val areaInfo = areaList[positionIndex]
            builder
                .asAttachList(
                arrayOf("收藏:" + areaInfo.getString("areaName")), null
            ) { _, _ ->
                fragment.saveArea(areaInfo.getString("typeName"), areaInfo.getString("areaName"))
            }.show()
            false
        }
        return holder
    }

    override fun onBindViewHolder(holder: AreaListAdapter.ViewHolder, position: Int) {
        val areaInfo = areaList[position]
        holder.areaName.text = areaInfo.getString("areaName")
        Glide.with(fragment).load(areaInfo.getString("areaPic")).transition(DrawableTransitionOptions.withCrossFade()).into(holder.areaPic)
    }

    override fun getItemCount(): Int {
        return areaList.size
    }
}