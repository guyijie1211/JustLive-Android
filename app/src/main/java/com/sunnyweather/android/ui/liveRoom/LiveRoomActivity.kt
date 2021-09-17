package com.sunnyweather.android.ui.liveRoom

import android.animation.ValueAnimator
import android.graphics.Point
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.internal.LinkedTreeMap
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication.Companion.context
import com.sunnyweather.android.logic.model.RoomInfo
import com.sunnyweather.android.util.dkplayer.YJLiveControlView
import kotlinx.android.synthetic.main.activity_liveroom.*
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videocontroller.component.*
import com.sunnyweather.android.util.dkplayer.PIPManager
import com.sunnyweather.android.util.dkplayer.YJstandardController
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.fragment_roomlist.*
import xyz.doikki.videoplayer.exo.ExoMediaPlayer
import xyz.doikki.videoplayer.player.VideoView
import xyz.doikki.videoplayer.player.VideoViewManager
import xyz.doikki.videoplayer.util.PlayerUtils
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.util.dkplayer.MyDanmakuView

class LiveRoomActivity : AppCompatActivity(), YJLiveControlView.OnRateSwitchListener {
    private val viewModel by lazy { ViewModelProvider(this).get(LiveRoomViewModel::class.java) }
    private var mDefinitionControlView: YJLiveControlView? = null
    private lateinit var adapter: LiveRoomAdapter
    private lateinit var mPIPManager: PIPManager
    private var controller: YJstandardController? = null
    private var videoView: VideoView<ExoMediaPlayer>? = null
    private var show = false
    private lateinit var mMyDanmakuView: MyDanmakuView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liveroom)
        //设置滑动到底部的动画时间
        val linearLayoutManager: LinearLayoutManager = object : LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
                val smoothScroller: LinearSmoothScroller =
                    object : LinearSmoothScroller(recyclerView.context) {
                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                            // 返回：滑过1px时经历的时间(ms)。
                            return 400f / displayMetrics.densityDpi
                        }
                        override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
                            return boxStart - viewStart
                        }
                    }
                smoothScroller.targetPosition = position
                startSmoothScroll(smoothScroller)
            }
        }
        danMu_recyclerView.layoutManager = linearLayoutManager
        adapter = LiveRoomAdapter(viewModel.danmuList)
        danMu_recyclerView.adapter = adapter
        //设置16:9的高宽
        val lp = player_container.layoutParams
        val point = Point()
        this.windowManager.defaultDisplay.getRealSize(point)
        lp.height = point.x * 9 / 16
        player_container.layoutParams = lp

        mPIPManager = PIPManager.getInstance()
        controller = YJstandardController(this)
        addControlComponents(controller!!)
        mMyDanmakuView = MyDanmakuView(this)
        controller!!.addControlComponent(mMyDanmakuView)
        controller!!.setDoubleTapTogglePlayEnabled(false)
        controller!!.setEnableInNormal(true)

        mPIPManager.actClass = LiveRoomActivity::class.java
        val platform = intent.getStringExtra("platform")?:""
        val roomId = intent.getStringExtra("roomId")?:""
        viewModel.getRealUrl(platform, roomId)
        viewModel.getRoomInfo("0eb26a33e68d4582858a74abf5a645d5", platform, roomId)
        viewModel.startDanmu(platform, roomId)

        videoView = VideoViewManager.instance().get("pip") as VideoView<ExoMediaPlayer>?
        player_container.addView(videoView)
        viewModel.danmuNum.observe(this, {
            adapter.notifyDataSetChanged()
            mMyDanmakuView.addDanmaku(viewModel.danmuList[viewModel.danmuList.size-1].content, false)
            danMu_recyclerView.smoothScrollToPosition(viewModel.danmuList.size)
        })
        viewModel.urlResponseData.observe(this, {result ->
            val urls : LinkedTreeMap<String, String> = result.getOrNull() as LinkedTreeMap<String, String>
            if (urls != null) {
                mDefinitionControlView?.setData(urls)
                videoView?.setVideoController(controller) //设置控制器
                videoView?.setUrl(urls["原画"]) //设置视频地址
                videoView?.start() //开始播放，不调用则不自动播放
            }
        })
        pipBtn.setOnClickListener {
            startFloatWindow(videoView)
        }
        viewModel.roomInfoResponseData.observe(this, {result ->
            val roomInfo : RoomInfo = result.getOrNull() as RoomInfo
            if (roomInfo != null) {
                Glide.with(this).load(roomInfo.ownerHeadPic).transition(
                    DrawableTransitionOptions.withCrossFade()
                ).into(ownerPic_roomInfo)
                ownerName_roomInfo.text = roomInfo.ownerName
                roomName_roomInfo.text = roomInfo.roomName
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (mPIPManager.isStartFloatWindow) {
            val playerTemp = VideoViewManager.instance().get("pip")
            mPIPManager.stopFloatWindow()
            controller?.setPlayerState(playerTemp.currentPlayerState)
            controller?.setPlayState(playerTemp.currentPlayState)
            playerTemp.setVideoController(controller)
            player_container.addView(playerTemp)
        }
    }

    private fun addControlComponents(controller: StandardVideoController) {
        val completeView = CompleteView(this)
        val errorView = ErrorView(this)
        val prepareView = PrepareView(this)
        prepareView.setClickStart()
        val titleView = TitleView(this)
        mDefinitionControlView = YJLiveControlView(this, this)
        mDefinitionControlView!!.setOnRateSwitchListener(this)
        val gestureView = GestureView(this)
        controller.addControlComponent(
            completeView,
            errorView,
            prepareView,
            titleView,
            mDefinitionControlView,
            gestureView
        )
    }

    fun changeRoomInfoVisible(isVisible: Boolean) {
        val height = PlayerUtils.dp2px(context, 100f)
        var va: ValueAnimator = if(isVisible){
            ValueAnimator.ofInt(0,height)
        }else{
            ValueAnimator.ofInt(height,0)
        }
        va.addUpdateListener {
            val h: Int = it.animatedValue as Int
            roomInfo.layoutParams.height = h
            roomInfo.requestLayout()
        }
        va.duration = 200
        va.start()
    }

    override fun onPause() {
        super.onPause()
        mPIPManager!!.pause()
    }

    override fun onResume() {
        super.onResume()
        mPIPManager!!.resume()
    }

    override fun onBackPressed() {
        if (mPIPManager!!.onBackPress()) return
        viewModel.stopDanmu()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPIPManager!!.reset()
    }

    override fun onRateChange(url: String?) {
        videoView?.setUrl(url)
        videoView?.replay(false)
    }

    private fun startFloatWindow(view: View?) {
        AndPermission
            .with(this)
            .overlay()
            .onGranted {
                mPIPManager!!.startFloatWindow()
                mPIPManager!!.resume()
                finish()
            }
            .onDenied { }
            .start()
    }
}