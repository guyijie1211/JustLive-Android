package com.sunnyweather.android.ui.search

import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import co.ankurg.expressview.ExpressView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.common.io.Resources.getResource
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.SunnyWeatherApplication.Companion.context
import com.sunnyweather.android.logic.model.Owner
import com.sunnyweather.android.ui.liveRoom.LiveRoomActivity
import kotlinx.android.synthetic.main.activity_liveroom.*

class SearchAdapter(private val activity: SearchActivity, private val ownerList: List<Owner>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ownerPic: ImageView = view.findViewById(R.id.profileImageIv)
        val liveState: TextView = view.findViewById(R.id.liveState)
        val ownerName: TextView = view.findViewById(R.id.usernameTv)
        val follows: TextView = view.findViewById(R.id.fullNameTv)
        val platform: TextView = view.findViewById(R.id.platform_search)
        val ownerItem: RelativeLayout = view.findViewById(R.id.contentContainerRl)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.owner_item, parent, false)
        val holder = ViewHolder(view)
        holder.ownerItem.setOnClickListener {
            Log.i("test", "点击")
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
        if (ownerInfo.platform == "bilibili") ownerInfo.headPic = "http:" +  ownerInfo.headPic
        Glide.with(activity).load(ownerInfo.headPic).transition(DrawableTransitionOptions.withCrossFade()).into(holder.ownerPic)
        if(ownerInfo.isLive == "1")  holder.liveState.visibility = View.VISIBLE
        holder.platform.text = SunnyWeatherApplication.platformName(ownerInfo.platform) + "·"
        holder.ownerName.text = ownerInfo.nickName
        holder.follows.text = "关注人数：" + getWan(ownerInfo.followers)
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