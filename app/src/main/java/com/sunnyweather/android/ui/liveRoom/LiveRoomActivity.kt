package com.sunnyweather.android.ui.liveRoom

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Point
import android.os.Build
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
import kotlinx.android.synthetic.main.activity_liveroom.*
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videocontroller.component.*
//import com.yanzhenjie.permission.AndPermission
import xyz.doikki.videoplayer.exo.ExoMediaPlayer
import xyz.doikki.videoplayer.player.VideoView
import xyz.doikki.videoplayer.player.VideoViewManager
import xyz.doikki.videoplayer.util.PlayerUtils
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sunnyweather.android.logic.model.DanmuSetting
import com.sunnyweather.android.util.dkplayer.*
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.ui.login.LoginActivity
import java.lang.Exception
import android.view.WindowManager

import android.app.Activity
import android.view.Window
import android.widget.ImageView

class LiveRoomActivity : AppCompatActivity(), YJLiveControlView.OnRateSwitchListener, DanmuSettingFragment.OnDanmuSettingChangedListener {
    private val viewModel by lazy { ViewModelProvider(this).get(LiveRoomViewModel::class.java) }
    private var mDefinitionControlView: YJLiveControlView? = null
    private lateinit var adapter: LiveRoomAdapterNew
    private lateinit var mPIPManager: PIPManager
    private var danmuShow = true
    private var controller: YJstandardController? = null
    private var videoView: VideoView<ExoMediaPlayer>? = null
    private lateinit var mMyDanmakuView: MyDanmakuView
    private lateinit var danmuSetting: DanmuSetting
    private lateinit var sharedPref: SharedPreferences
    private var toBottom = true
    private var updateList = true

    private var isFollowed = false
    private var platform = ""
    private var roomId = ""
    private var isFirstGetInfo = true

    fun startFullScreen() {
        updateList = false
        mMyDanmakuView.show()
    }

    fun stopFullScreen() {
        adapter.setList(viewModel.danmuList)
        danMu_recyclerView.scrollToPosition(adapter.itemCount-1)
        mMyDanmakuView.hide()
        toBottom = true
        updateList = true
    }

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
                            return 20f / displayMetrics.densityDpi
                        }
                        override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
                            return boxStart - viewStart
                        }
                    }
                smoothScroller.targetPosition = position
                startSmoothScroll(smoothScroller)
            }
        }
        setStatusBarColor(this, R.color.black)
        //获取本地弹幕设置
        sharedPref = this.getSharedPreferences("JustLive", Context.MODE_PRIVATE)
        danmuSetting = getDanmuSetting()

        danMu_recyclerView.layoutManager = linearLayoutManager
        adapter = LiveRoomAdapterNew()
        danMu_recyclerView.adapter = adapter
        danMu_recyclerView.itemAnimator = null
        //绑定回到底部按钮
        to_bottom_danmu.setOnClickListener {
            danMu_recyclerView.scrollToPosition(adapter.itemCount-1)
            to_bottom_danmu.visibility = View.GONE
            toBottom = true
        }
        //向上滚动弹幕时，显示回到底部按钮
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            danMu_recyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
                if (danMu_recyclerView.canScrollVertically(1)) {
                    toBottom = false
                    to_bottom_danmu.visibility = View.VISIBLE
                } else {
                    to_bottom_danmu.visibility = View.GONE
                    toBottom = true
                }
            }
        }
        //设置16:9的高宽
        val lp = player_container.layoutParams
        val point = Point()
        this.windowManager.defaultDisplay.getRealSize(point)
        lp.height = point.x * 9 / 16
        player_container.layoutParams = lp

        mPIPManager = PIPManager.getInstance()
        controller = YJstandardController(this)
        val display = windowManager.defaultDisplay
        val refreshRate = display.refreshRate
        mMyDanmakuView = MyDanmakuView(this, danmuSetting, refreshRate)
//        mMyDanmakuView.showFPS(true)
        mMyDanmakuView.hide()
        addControlComponents(controller!!)
        controller!!.setDoubleTapTogglePlayEnabled(false)
        controller!!.setEnableInNormal(true)

        mPIPManager.actClass = LiveRoomActivity::class.java
        platform = intent.getStringExtra("platform")?:""
        roomId = intent.getStringExtra("roomId")?:""
        var uid = ""
        if (SunnyWeatherApplication.userInfo != null) {
            uid = SunnyWeatherApplication.userInfo!!.uid
        }
        viewModel.getRoomInfo(uid, platform, roomId)
        viewModel.startDanmu(platform, roomId)

        videoView = VideoViewManager.instance().get("pip") as VideoView<ExoMediaPlayer>?


        //弹幕更新
        viewModel.danmuNum.observe(this, {
            mMyDanmakuView.addDanmaku(viewModel.danmuList.last().content)
            if (updateList) {
                adapter.addData(viewModel.danmuList.last())
                if (toBottom) {
                    danMu_recyclerView.scrollToPosition(adapter.itemCount-1)
                }
            }
        })
        //获取到直播源信息
        viewModel.urlResponseData.observe(this, {result ->
            val urls : LinkedTreeMap<String, String> = result.getOrNull() as LinkedTreeMap<String, String>
            if (urls != null && urls.size > 0) {
                mDefinitionControlView?.setData(urls)
                videoView?.setVideoController(controller) //设置控制器
                videoView?.setUrl(urls["原画"]) //设置视频地址
                videoView?.start() //开始播放，不调用则不自动播放
            }
        })
        pipBtn.setOnClickListener {
//            startFloatWindow(videoView)
        }
        tinyScreen.setOnClickListener {
            videoView!!.startTinyScreen()
        }
        viewModel.followResponseLiveDate.observe(this, {result ->
            val result = result.getOrNull()
            if (result is String) {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                if (result == "关注成功") {
                    follow_roomInfo.text = "已关注"
                    isFollowed = true
                } else if (result == "已经取消关注") {
                    follow_roomInfo.text = "关注"
                    isFollowed = false
                }
            }
        })
        //获取到房间信息
        viewModel.roomInfoResponseData.observe(this, {result ->
            val roomInfo : RoomInfo = result.getOrNull() as RoomInfo
            if (roomInfo != null) {
                changeRoomInfoVisible(roomInfo_liveRoom.layoutParams.height == 0)
                //关注按钮
                if (isFirstGetInfo) {
                    follow_roomInfo.setOnClickListener {
                        if (SunnyWeatherApplication.isLogin.value!!) {
                            if (isFollowed) {
                                viewModel.unFollow(roomInfo.platForm, roomInfo.roomId, SunnyWeatherApplication.userInfo!!.uid)
                            } else {
                                viewModel.follow(roomInfo.platForm, roomInfo.roomId, SunnyWeatherApplication.userInfo!!.uid)
                            }
                        } else {
                            MaterialAlertDialogBuilder(this)
                                .setTitle("启用关注")
                                .setMessage("登录后关注直播间")
                                .setCancelable(true)
                                .setNegativeButton("取消") { _, _ ->

                                }
                                .setPositiveButton("登录") { _, _ ->
                                    val intent = Intent(context, LoginActivity::class.java)
                                    startActivity(intent)
                                }
                                .show()
                        }
                    }
                    //提示弹幕不支持
                    if (roomInfo.platForm == "egame" || roomInfo.platForm == "cc") {
                        danmu_not_support.visibility = View.VISIBLE
                        danmu_not_support.text = "暂不支持${SunnyWeatherApplication.platformName(roomInfo.platForm)}弹幕"
                    }
                    //未开播
                    if (roomInfo.isLive == 0) {
                        liveRoom_not_live.visibility = View.VISIBLE
                        //点击播放器区域显示关注窗口
//                        liveRoom_not_live.setOnClickListener {
//                            changeRoomInfoVisible(roomInfo_liveRoom.layoutParams.height == 0)
//                        }
                    } else {
                        player_container.addView(videoView)
                        viewModel.getRealUrl(platform, roomId)
                    }
                    Glide.with(this).load(roomInfo.ownerHeadPic).transition(
                        DrawableTransitionOptions.withCrossFade()
                    ).into(ownerPic_roomInfo)
                    ownerName_roomInfo.text = roomInfo.ownerName
                    roomName_roomInfo.text = roomInfo.roomName
                    isFirstGetInfo = false
                }
                isFollowed = (roomInfo.isFollowed == 1)
                if (isFollowed) follow_roomInfo.text = "已关注"
            }
        })
    }

    override fun onStart() {
        super.onStart()
        mMyDanmakuView.resume()
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
            gestureView,
            mMyDanmakuView
        )
        controller.setCanChangePosition(false)
    }

    /**
     *
     */
    fun changeRoomInfoVisible(isVisible: Boolean) {
        val height = PlayerUtils.dp2px(context, 80f)
        var va: ValueAnimator = if(isVisible){
            ValueAnimator.ofInt(0,height)
        }else{
            ValueAnimator.ofInt(height,0)
        }
        va.addUpdateListener {
            val h: Int = it.animatedValue as Int
            roomInfo_liveRoom.layoutParams.height = h
            roomInfo_liveRoom.requestLayout()
            danMu_recyclerView.scrollToPosition(adapter.itemCount-1)
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
        var uid = ""
        if (SunnyWeatherApplication.userInfo != null) {
            uid = SunnyWeatherApplication.userInfo!!.uid
        }
        viewModel.getRoomInfo(uid, platform, roomId)
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

    override fun onDanmuShowChange() {
        danmuShow = if (danmuShow) {
            mMyDanmakuView.hide()
            !danmuShow
        } else {
            mMyDanmakuView.show()
            !danmuShow
        }
    }

    override fun onDanmuSettingShowChanged() {
        controller!!.stopFadeOut()
    }

//    private fun startFloatWindow(view: View?) {
//        AndPermission
//            .with(this)
//            .overlay()
//            .onGranted {
//                mPIPManager!!.startFloatWindow()
//                mPIPManager!!.resume()
//                finish()
//            }
//            .onDenied { }
//            .start()
//    }

    //SharedPreferences保存对象
    private fun setDanmuSetting(data: DanmuSetting) {
        if (null == data) return
        val gson = Gson()
        //change data to json
        val strJson = gson.toJson(data)
        sharedPref.edit().putString("danmuSetting", strJson).commit()
    }

    //SharedPreferences读取对象
    private fun getDanmuSetting(): DanmuSetting {
        val strJson: String? = sharedPref.getString("danmuSetting", null)
        if (strJson != null) {
            try {
                val gson = Gson()
                val jsonElement = JsonParser().parse(strJson)
                return gson.fromJson(jsonElement, DanmuSetting::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        //默认弹幕设置
        return DanmuSetting(20f,1f,2f,1.5f,8f,false, false, false)
    }

    override fun getSetting(): DanmuSetting {
        return danmuSetting
    }

    override fun changeSetting(setting: DanmuSetting, updateItem: String) {
        danmuSetting = setting
        setDanmuSetting(setting)
        mMyDanmakuView.setContext(setting, updateItem)
    }

    fun hideViews(){
        controller!!.hide()
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     * @param activity
     * @param colorId
     */
    fun setStatusBarColor(activity: Activity, colorId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(colorId)
        }
    }
}