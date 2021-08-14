package com.sunnyweather.android.ui.roomList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.RoomInfo
import org.w3c.dom.Text
import com.nostra13.universalimageloader.core.ImageLoader
import com.sunnyweather.android.SunnyWeatherApplication


class RoomListAdapter(private val fragment: Fragment, private val roomList: List<RoomInfo>) :
    RecyclerView.Adapter<RoomListAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomPic: ImageView = view.findViewById(R.id.roomPic)
        val ownerPic: ImageView = view.findViewById(R.id.ownerPic)
        val ownerName: TextView = view.findViewById(R.id.ownerName)
        val roomName: TextView = view.findViewById(R.id.roomName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.room_item, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            Toast.makeText(parent.context, roomList[holder.adapterPosition].ownerName, Toast.LENGTH_SHORT)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val roomInfo = roomList[position]
        holder.ownerName.text = roomInfo.platform + "·" + roomInfo.ownerName
//        SunnyWeatherApplication.imageLoader?.displayImage(roomInfo.roomPic, holder.roomPic)
//        SunnyWeatherApplication.imageLoader?.displayImage(roomInfo.ownerPic, holder.ownerPic)
        holder.roomName.text = roomInfo.roomName
    }

    override fun getItemCount(): Int {
        return roomList.size
    }
}