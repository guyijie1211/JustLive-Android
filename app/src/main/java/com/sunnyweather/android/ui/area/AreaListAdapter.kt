package com.sunnyweather.android.ui.area

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.AreaInfo
import com.sunnyweather.android.ui.liveRoom.LiveRoomActivity
import com.sunnyweather.android.ui.roomList.RoomListAdapter

class AreaListAdapter(private val fragment: Fragment, private val areaList: List<AreaInfo>) :
    RecyclerView.Adapter<AreaListAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val areaPic: ImageView = view.findViewById(R.id.areaPic)
        val areaName: TextView = view.findViewById(R.id.areaName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.area_item, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.layoutPosition
            val areaInfo = areaList[position]
            //打开对应分区
//            val intent = Intent(parent.context, LiveRoomActivity::class.java).apply {
//                putExtra("platform", roomInfo.platForm)
//                putExtra("roomId", roomInfo.roomId)
//            }
//            fragment.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: AreaListAdapter.ViewHolder, position: Int) {
        val areaInfo = areaList[position]
        holder.areaName.text = areaInfo.areaName
        Glide.with(fragment).load(areaInfo.areaPic).transition(DrawableTransitionOptions.withCrossFade()).into(holder.areaPic)
    }

    override fun getItemCount(): Int {
        return areaList.size
    }
}