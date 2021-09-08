package com.sunnyweather.android.ui.search

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Owner
import com.sunnyweather.android.ui.liveRoom.LiveRoomActivity

class SearchAdapter(private val activity: SearchActivity, private val ownerList: List<Owner>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ownerPic: ImageView = view.findViewById(R.id.profileImageIv)
        val liveState: ImageView = view.findViewById(R.id.firstButtonIv)
        val ownerName: TextView = view.findViewById(R.id.usernameTv)
        val follows: TextView = view.findViewById(R.id.fullNameTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.owner_item, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.layoutPosition
            val ownerInfo = ownerList[position]
            val intent = Intent(parent.context, LiveRoomActivity::class.java).apply {
                putExtra("platform", ownerInfo.platform)
                putExtra("roomId", ownerInfo.roomId)
            }
            activity.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ownerInfo = ownerList[position]
        Glide.with(activity).load(ownerInfo.headPic).transition(DrawableTransitionOptions.withCrossFade()).into(holder.ownerPic)
//        holder.liveState.text = if(ownerInfo.isLive == "1")  "直播中" else "未开播"
        val color = if(ownerInfo.isLive == "1") 0xf000000 else 0xfffffff
        holder.liveState.setBackgroundColor(color)
//        holder.platform.text = ownerInfo.platform + "·"
        holder.ownerName.text = ownerInfo.nickName
        holder.follows.text = getWan(ownerInfo.followers)
    }

    override fun getItemCount(): Int {
        return ownerList.size
    }

    private fun getWan(num: Int): String {
        val numString = num.toString().trim()
        return if (numString.length > 4){
            val numCut = numString.substring(0, numString.length-4)
            val afterPoint = numString.substring(numString.length-4,numString.length-3)
            numCut+'.'+afterPoint+'万'
        }else {
            numString+'人'
        }
    }
}