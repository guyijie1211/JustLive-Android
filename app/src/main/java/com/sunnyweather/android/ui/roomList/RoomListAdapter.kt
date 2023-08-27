package com.sunnyweather.android.ui.roomList

import android.content.Intent
import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.chad.library.adapter.base.module.LoadMoreModule
import com.stx.xhb.xbanner.XBanner
import com.stx.xhb.xbanner.entity.BaseBannerInfo
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.RoomInfo
import com.sunnyweather.android.ui.customerUIs.BlackWhiteTransformation
import com.sunnyweather.android.ui.liveRoom.LiveRoomActivity

class RoomListAdapter(private val fragment: Fragment, private val roomList: ArrayList<RoomInfo>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), LoadMoreModule{

    private val TYPE_HEAD = 0
    private val TYPE_ITEM = 1

    private var mHeaderView: View? = null

    fun setHeaderView(headerView: View) {
        mHeaderView = headerView
        notifyItemInserted(0)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && mHeaderView != null) TYPE_HEAD else TYPE_ITEM
    }

    private fun isHeadView(position: Int): Boolean {
        return position == 0
    }

    class HeadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 在这里定义headview布局中的各个控件
        val banner: XBanner = itemView.findViewById(R.id.banner)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomPic: ImageView = view.findViewById(R.id.roomPic)
        val ownerPic: ImageView = view.findViewById(R.id.ownerPic)
        val ownerName: TextView = view.findViewById(R.id.ownerName)
        val roomName: TextView = view.findViewById(R.id.roomName)
        val roomCategory: TextView = view.findViewById(R.id.roomCategory)
        val liveNum: TextView = view.findViewById(R.id.liveNum)
        val notLive: TextView = view.findViewById(R.id.room_not_live)
        val isRecord: RelativeLayout = view.findViewById(R.id.record)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEAD && mHeaderView != null) {
            // 创建headview的ViewHolder并返回
            val headView = LayoutInflater.from(parent.context).inflate(R.layout.ad_head, parent, false)
            HeadViewHolder(headView)
        } else {
            // 创建普通item的ViewHolder并返回
//            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
//            ItemViewHolder(itemView)
            val view = LayoutInflater.from(parent.context).inflate(R.layout.room_item, parent, false)
            val holder = ViewHolder(view)
            holder.isRecord.background.alpha = 150
            holder.isRecord.run {
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(0,0,view.width,view.height, 20f)
                    }
                }
                clipToOutline = true
            }
            holder.itemView.setOnClickListener {
                val position = holder.layoutPosition
                val roomInfo = roomList[position]
                val intent = Intent(parent.context, LiveRoomActivity::class.java).apply {
                    putExtra("platform", roomInfo.platForm)
                    putExtra("roomId", roomInfo.roomId)
                }
                fragment.startActivity(intent)
            }
            return holder
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHeadView(position) && mHeaderView != null) {
            holder as HeadViewHolder
            // 设置XBanner的属性和数据
            holder.banner.loadImage { banner, model, view, position ->
                model as BaseBannerInfo
                Glide.with(fragment.context!!)
                    .load(model.xBannerUrl)
                    .into(view as ImageView)
            }

            val picList: MutableList<BaseBannerInfo> = mutableListOf()
            // 添加轮播图数据
            picList.add(object : BaseBannerInfo {
                override fun getXBannerUrl(): Any {
                    return "https://lmg.jj20.com/up/allimg/4k/s/02/2109250006343S5-0-lp.jpg"
                }
                override fun getXBannerTitle(): String {
                    return "标题"
                }
            })
            holder.banner.setBannerData(picList)
        } else {
            holder as ViewHolder
            val roomInfo = roomList[position]
            holder.ownerName.text = SunnyWeatherApplication.platformName(roomInfo.platForm) + "·" + roomInfo.ownerName
            holder.roomCategory.text = roomInfo.categoryName
            holder.liveNum.text = getWan(roomInfo.online)
            holder.roomName.text = roomInfo.roomName

            if (roomInfo.isLive == 1) {
                Glide.with(fragment).load(roomInfo.ownerHeadPic).transition(withCrossFade()).into(holder.ownerPic)
                Glide.with(fragment).load(roomInfo.roomPic).transition(withCrossFade()).into(holder.roomPic)
            } else {
                holder.notLive.visibility = View.VISIBLE
                //黑白图
                Glide.with(fragment).load(roomInfo.ownerHeadPic).transforms(BlackWhiteTransformation()).transition(withCrossFade()).into(holder.ownerPic)
            }

            if (roomInfo.isRecord) {
                holder.isRecord.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return roomList.size
//        return if (mHeaderView == null) roomList.size else roomList.size + 1
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