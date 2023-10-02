package com.sunnyweather.android.ui.liveRoom

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.SunnyWeatherApplication.Companion.context
import com.sunnyweather.android.logic.model.DanmuSetting
import com.sunnyweather.android.logic.model.RoomInfo
import com.sunnyweather.android.logic.network.ServiceCreator
import com.sunnyweather.android.logic.service.ForegroundService
import com.sunnyweather.android.ui.login.LoginActivity
import com.sunnyweather.android.util.dkplayer.DanmuSettingFragment
import com.sunnyweather.android.util.dkplayer.DragGestureView
import com.sunnyweather.android.util.dkplayer.MyDanmakuView
import com.sunnyweather.android.util.dkplayer.PIPManager
import com.sunnyweather.android.util.dkplayer.YJLiveControlView
import com.sunnyweather.android.util.dkplayer.YJTitleView
import com.sunnyweather.android.util.dkplayer.YJstandardController
import kotlinx.android.synthetic.main.activity_liveroom.danMu_recyclerView
import kotlinx.android.synthetic.main.activity_liveroom.danmu_not_support
import kotlinx.android.synthetic.main.activity_liveroom.follow_roomInfo
import kotlinx.android.synthetic.main.activity_liveroom.liveRoom_bar_txt
import kotlinx.android.synthetic.main.activity_liveroom.liveRoom_leftContainer
import kotlinx.android.synthetic.main.activity_liveroom.liveRoom_main
import kotlinx.android.synthetic.main.activity_liveroom.liveRoom_not_live
import kotlinx.android.synthetic.main.activity_liveroom.ownerName_roomInfo
import kotlinx.android.synthetic.main.activity_liveroom.ownerPic_roomInfo
import kotlinx.android.synthetic.main.activity_liveroom.player_container
import kotlinx.android.synthetic.main.activity_liveroom.roomInfo_liveRoom
import kotlinx.android.synthetic.main.activity_liveroom.roomName_roomInfo
import kotlinx.android.synthetic.main.activity_liveroom.to_bottom_danmu
import kotlinx.android.synthetic.main.activity_liveroom.to_web
import xyz.doikki.videocontroller.component.CompleteView
import xyz.doikki.videocontroller.component.ErrorView
import xyz.doikki.videocontroller.component.PrepareView
import xyz.doikki.videoplayer.exo.ExoMediaPlayer
import xyz.doikki.videoplayer.player.VideoView
import xyz.doikki.videoplayer.player.VideoViewManager
import xyz.doikki.videoplayer.util.PlayerUtils
import java.util.TreeMap

class LiveRoomActivity : AppCompatActivity(), Utils.OnAppStatusChangedListener, YJLiveControlView.OnRateSwitchListener, DanmuSettingFragment.OnDanmuSettingChangedListener {
    private val viewModel by lazy { ViewModelProvider(this).get(LiveRoomViewModel::class.java) }
    private var mDefinitionControlView: YJLiveControlView? = null
    private lateinit var adapter: LiveRoomAdapterNew
    private lateinit var mPIPManager: PIPManager
    private var danmuShow = true
    private var controller: YJstandardController? = null
    private var videoView: VideoView<ExoMediaPlayer>? = null
    private var playerUrl: String = ""
    private lateinit var mMyDanmakuView: MyDanmakuView
    private lateinit var danmuSetting: DanmuSetting
    private lateinit var sharedPref: SharedPreferences
    private var toBottom = true
    private var updateList = true
    private var countDownTimer: CountDownTimer? = null

    private var isFollowed = false
    private var platform = ""
    private var roomId = ""
    private var isFirstGetInfo = true
    private val definitionArray = arrayOf("清晰", "流畅", "高清", "超清", "原画")
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    open fun getUrl(): String {
        return playerUrl;
    }

    fun startFullScreen() {
        updateList = false
        if (danmuSetting.isShow) {
            mMyDanmakuView.show()
        }
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
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var theme: Int
        val autoDark = sharedPreferences.getBoolean("autoDark", true)
        val pureDark = sharedPreferences.getBoolean("pureDark", false)
        if (autoDark) {
            if(SunnyWeatherApplication.isNightMode(this)){
                theme = R.style.nightTheme
                sharedPreferences.edit().putInt("theme", theme).commit()
            } else {
                theme = R.style.SunnyWeather
                sharedPreferences.edit().putInt("theme", theme).commit()
            }
        } else {
            theme = sharedPreferences.getInt("theme", R.style.SunnyWeather)
        }
        if (pureDark && theme == R.style.nightTheme) {
            setTheme(R.style.nightTheme_dark)
        } else {
            setTheme(theme)
        }
        setContentView(R.layout.activity_liveroom)
        val playBackGround = sharedPreferences.getBoolean("play_background", false)
        val backTiny = sharedPreferences.getBoolean("tiny_when_back", false)
        if (playBackGround || backTiny) {
            AppUtils.registerAppStatusChangedListener(this)
        }
        BarUtils.transparentStatusBar(this)
        BarUtils.addMarginTopEqualStatusBarHeight(liveRoom_main)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.black))
        startCountdown()
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
        if (ScreenUtils.isPortrait()) {
            //竖屏
            ScreenUtils.setPortrait(this)
            val lp = player_container.layoutParams
            val point = Point()
            this.windowManager.defaultDisplay.getRealSize(point)
            lp.height = point.x * 9 / 16
            player_container.layoutParams = lp
        } else {
            //横屏
            ScreenUtils.setLandscape(this)
            val lpc = liveRoom_leftContainer.layoutParams
            val pointc = Point()
            this.windowManager.defaultDisplay.getRealSize(pointc)
            if (DeviceUtils.isTablet()) {
                lpc.width = ScreenUtils.getScreenWidth() * 4 / 5
            } else {
                lpc.width = (ScreenUtils.getScreenHeight() - ConvertUtils.dp2px(60f) - BarUtils.getStatusBarHeight()) * 16 / 9
            }
            liveRoom_leftContainer.layoutParams = lpc
        }

        controller = YJstandardController(this, this)
        val display = windowManager.defaultDisplay
        val refreshRate = display.refreshRate
        mMyDanmakuView = MyDanmakuView(this, danmuSetting, refreshRate)
        mMyDanmakuView.hide()
        addControlComponents(controller!!)
        controller!!.setDoubleTapTogglePlayEnabled(false)
        controller!!.setEnableInNormal(true)

        platform = intent.getStringExtra("platform")?:""
        roomId = intent.getStringExtra("roomId")?:""
        mPIPManager = PIPManager.getInstance(platform, roomId)
        mPIPManager.actClass = LiveRoomActivity::class.java
        var uid = ""
        if (SunnyWeatherApplication.userInfo != null) {
            uid = SunnyWeatherApplication.userInfo!!.uid
            if (!viewModel.isConnecting()) {
                viewModel.startDanmu(
                    platform,
                    roomId,
                    SunnyWeatherApplication.userInfo!!.selectedContent,
                    SunnyWeatherApplication.userInfo!!.isActived == "1"
                )
            }
        } else {
            if (!viewModel.isConnecting()) {
                viewModel.startDanmu(platform, roomId, "", false)
            }
        }
        viewModel.getRoomInfo(uid, platform, roomId)
        viewModel.getRealUrl(platform, roomId)
        //去网页
        to_web.setOnClickListener {
            toWeb(platform, roomId)
        }
        //弹幕更新
        viewModel.danmuNum.observe(this) {
            if (viewModel.danmuList.size > 0) {
                mMyDanmakuView.addDanmaku(viewModel.danmuList.last()?.content)
                if (updateList) {
                    adapter.addData(viewModel.danmuList.last())
                    if (toBottom) {
                        danMu_recyclerView.scrollToPosition(adapter.itemCount - 1)
                    }
                }
            }
        }
        //获取到直播源信息
        viewModel.urlResponseData.observe(this) { result ->
            val urls: TreeMap<String, ArrayList<JSONObject>> =
                result.getOrNull() as TreeMap<String, ArrayList<JSONObject>>
            if (urls.size > 0) {
                videoView =
                    VideoViewManager.instance().get(platform + roomId) as VideoView<ExoMediaPlayer>?
                if (mPIPManager.isStartFloatWindow) {
                    mPIPManager.stopFloatWindow()
//                            controller?.setPlayerState(videoView!!.currentPlayerState)
                    mMyDanmakuView.stopFloatPrepare()
                }
                player_container.addView(videoView)
                videoView?.setVideoController(controller) //设置控制器
                val sharedPref = this.getSharedPreferences("JustLive", Context.MODE_PRIVATE)

                when (sharedPref.getInt("playerSize", R.id.radio_button_1)) {
                    R.id.radio_button_1 -> {
                        changeVideoSize(VideoView.SCREEN_SCALE_DEFAULT)
                    }

                    R.id.radio_button_2 -> {
                        changeVideoSize(VideoView.SCREEN_SCALE_MATCH_PARENT)
                    }

                    R.id.radio_button_3 -> {
                        changeVideoSize(VideoView.SCREEN_SCALE_CENTER_CROP)
                    }
                }
                handelRatesAndWifi(urls)
                videoView?.start() //开始播放，不调用则不自动播放
            }
        }
        viewModel.followResponseLiveDate.observe(this) { result ->
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
        }
        //获取到房间信息
        viewModel.roomInfoResponseData.observe(this) { result ->
            val roomInfo = result.getOrNull()
            if (roomInfo is RoomInfo) {
                //关注按钮
                if (isFirstGetInfo) {
                    if (ScreenUtils.isLandscape()) {
                        ownerName_roomInfo.text =
                            SunnyWeatherApplication.platformName(roomInfo.platForm) + "·" + roomInfo.ownerName
                        roomName_roomInfo.text = roomInfo.roomName
                    } else {
                        ownerName_roomInfo.text =
                            SunnyWeatherApplication.platformName(roomInfo.platForm)
                        roomName_roomInfo.text = roomInfo.ownerName
                        liveRoom_bar_txt.text = roomInfo.roomName
                    }
                    follow_roomInfo.setOnClickListener {
                        if (SunnyWeatherApplication.isLogin.value!!) {
                            if (isFollowed) {
                                viewModel.unFollow(
                                    roomInfo.platForm,
                                    roomInfo.roomId,
                                    SunnyWeatherApplication.userInfo!!.uid
                                )
                            } else {
                                viewModel.follow(
                                    roomInfo.platForm,
                                    roomInfo.roomId,
                                    SunnyWeatherApplication.userInfo!!.uid
                                )
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
                    if (!SunnyWeatherApplication.ifDanmuSupport(platform)) {
                        danmu_not_support.visibility = View.VISIBLE
                        danmu_not_support.text =
                            "暂不支持${SunnyWeatherApplication.platformName(roomInfo.platForm)}弹幕"
                    }
                    //未开播
                    if (roomInfo.isLive == 0) {
                        liveRoom_not_live.visibility = View.VISIBLE
                        //点击播放器区域显示关注窗口
                        liveRoom_not_live.setOnClickListener {
                            changeRoomInfoVisible(roomInfo_liveRoom.layoutParams.height == 0)
                        }
                    }
                    Glide.with(this).load(roomInfo.ownerHeadPic).transition(
                        DrawableTransitionOptions.withCrossFade()
                    ).into(ownerPic_roomInfo)
                    isFirstGetInfo = false
                }
                isFollowed = (roomInfo.isFollowed == 1)
                if (isFollowed) follow_roomInfo.text = "已关注"
            } else if (roomInfo is String) {
                Toast.makeText(this, roomInfo, Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, ForegroundService::class.java)
        stopService(intent)
        mMyDanmakuView.resume()
    }

    private fun addControlComponents(controller: YJstandardController) {
        val completeView = CompleteView(this)
        val errorView = ErrorView(this)
        val prepareView = PrepareView(this)
        prepareView.setClickStart()
        val titleView = YJTitleView(this)
        mDefinitionControlView = YJLiveControlView(this, this)
        mDefinitionControlView!!.setOnRateSwitchListener(this)
        val gestureView = DragGestureView(this)
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

    fun changeRoomInfoVisible(isVisible: Boolean) {
        if (ScreenUtils.isLandscape()) {
            return
        }
        val height = PlayerUtils.dp2px(context, 60f)
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
        val playBackGround = sharedPreferences.getBoolean("play_background", false)
        val backTiny = sharedPreferences.getBoolean("tiny_when_back", false)
        if (playBackGround || backTiny) {
            return
        }
        mPIPManager.pause()
    }

    override fun onResume() {
        super.onResume()
        var uid = ""
        if (SunnyWeatherApplication.isLogin.value!!) {
            uid = SunnyWeatherApplication.userInfo!!.uid
        }
        viewModel.getRoomInfo(uid, platform, roomId)
        if (!isFirstGetInfo && !viewModel.isConnecting()) {
            viewModel.startDanmu(platform, roomId, SunnyWeatherApplication.userInfo?.selectedContent, SunnyWeatherApplication.userInfo?.isActived == "1")
        }
        mPIPManager.resume()
    }

    override fun onBackPressed() {
        if (mPIPManager.onBackPress()) return
        val playBackGround = sharedPreferences.getBoolean("play_background", false)
        val backTiny = sharedPreferences.getBoolean("tiny_when_back", false)
        if (playBackGround || backTiny) {
            AppUtils.unregisterAppStatusChangedListener(this)
        }
        if (backTiny) {
            startFloatWindow()
            return
        }
        viewModel.stopDanmu()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppUtils.unregisterAppStatusChangedListener(this)
        mPIPManager!!.reset()
        if (countDownTimer != null) {
            countDownTimer?.cancel()
            ToastUtils.showShort("定时计时结束")
        }
    }

    fun setCountDown(countDownTimer: CountDownTimer) {
        if (this.countDownTimer != null) {
            this.countDownTimer?.cancel()
        }
        this.countDownTimer = countDownTimer
        this.countDownTimer?.start()
    }

    override fun onRateChange(url: String?, isInit: Boolean) {
        playerUrl = url!!
        videoView?.setUrl(url)
        if (!isInit) {
            videoView?.replay(true)
        }
    }

    override fun onDanmuShowChange() {
        danmuShow = if (danmuShow) {
            mMyDanmakuView.hide()
            danmuSetting.isShow = !danmuShow
            setDanmuSetting(danmuSetting)
            !danmuShow
        } else {
            mMyDanmakuView.show()
            danmuSetting.isShow = !danmuShow
            setDanmuSetting(danmuSetting)
            !danmuShow
        }
    }

    override fun onDanmuSettingShowChanged() {
        controller!!.stopFadeOut()
    }

    override fun onMultiRateShowChanged() {
        controller!!.stopFadeOut()
    }

    override fun startFloat() {
        startFloatWindow()
    }

    fun stopFloat() {
        mPIPManager.stopFloatWindow()
    }

    private fun startFloatWindow() {
        XXPermissions.with(this)
            // 申请悬浮窗权限
            .permission(Permission.SYSTEM_ALERT_WINDOW)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: List<String>, all: Boolean) {
                    if (all) {
                        if (videoView!!.isFullScreen) {
                            controller!!.changeFullScreen()
                        }
                        mPIPManager.startFloatWindow()
                        mPIPManager.resume()
                        finish()
                    }
                }
                override fun onDenied(permissions: List<String>, never: Boolean) {
                    if (never) {
                        Toast.makeText(context, "获取悬浮窗权限失败", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "获取悬浮窗权限失败", Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    //SharedPreferences保存对象
    private fun setDanmuSetting(data: DanmuSetting) {
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
        return DanmuSetting(true,20f,1f,2f,1.5f,8f,false, false, false)
    }

    override fun getSetting(): DanmuSetting {
        return danmuSetting
    }

    override fun changeSetting(setting: DanmuSetting, updateItem: String) {
        danmuSetting = setting
        setDanmuSetting(setting)
        mMyDanmakuView.setContext(setting, updateItem)
    }

    override fun changeVideoSize(size: Int) {
        videoView?.setScreenScaleType(size)
    }

    fun hideViews(){
        controller!!.hide()
    }

    fun banChanged(isActiveArray: ArrayList<String>){
        viewModel.banChanged(isActiveArray)
    }

    fun changeBanActive(isActive: Boolean) {
        viewModel.activeChange(isActive)
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     * @param activity
     * @param colorId
     */
    private fun setStatusBarColor(activity: Activity, colorId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(colorId)
        }
    }

    private fun toWeb(platform: String, roomId: String) {
        var url = when (platform) {
            "bilibili" -> "https://live.bilibili.com/$roomId"
            "douyu" -> "https://www.douyu.com/$roomId"
            "huya" -> "https://m.huya.com/$roomId"
            "cc" -> "https://cc.163.com/$roomId"
            "egame" -> "https://egame.qq.com/$roomId"
            else -> "https://github.com/guyijie1211/JustLive-Android/issues/new"
        }
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addCategory(Intent. CATEGORY_BROWSABLE)
        startActivity(intent)
    }

    override fun onForeground(activity: Activity?) {
        val intent = Intent(this, ForegroundService::class.java)
        stopService(intent)
    }

    override fun onBackground(activity: Activity?) {
        Log.i("test","onBackground")
        val backTiny = sharedPreferences.getBoolean("tiny_when_back", false)
        if (backTiny) {
            startFloatWindow()
            return
        }
        val intent = Intent(this, ForegroundService::class.java)
        intent.putExtra("platform", platform)
        intent.putExtra("roomId", roomId)
        intent.putExtra("roomInfo", "${ownerName_roomInfo.text}:${roomName_roomInfo.text}")
        startService(intent)
    }

    fun pause() {
        videoView?.release()
    }

    fun stopCountdown() {
        ToastUtils.showShort("计时结束")
        this.countDownTimer?.cancel()
    }

    fun startCountdown() {
        val time = sharedPreferences.getInt("closeAppTime", 0)
        if (sharedPreferences.getBoolean("closeAppOn", false) && time > 0) {
                val countDownTimer: CountDownTimer =
                    object : CountDownTimer((time * 60000).toLong(), 10000) {
                        override fun onTick(millisUntilFinished: Long) {
                            // 5分钟倒计时
                            if (millisUntilFinished < 5.5 * 60000 && millisUntilFinished > 4.5 * 60000) {
                                ToastUtils.showLong("5分钟后关闭app")
                            }
                        }

                        override fun onFinish() {
                            AppUtils.exitApp()
                        }
                    }
                setCountDown(countDownTimer)
                ToastUtils.showShort("开始计时," +time.toString() + "分钟后退出应用")
            }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        platform = intent?.getStringExtra("platform")?:""
        roomId = intent?.getStringExtra("roomId")?:""
        var uid = SunnyWeatherApplication.userInfo!!.uid
        viewModel.startDanmu(
            platform,
            roomId,
            SunnyWeatherApplication.userInfo!!.selectedContent,
            SunnyWeatherApplication.userInfo!!.isActived == "1"
        )
        viewModel.getRealUrl(platform, roomId)
        mMyDanmakuView.clearDanmakusOnScreen()

        viewModel.danmuList.clear()
        scopeNetLife { // 创建作用域
            val userInfo = SunnyWeatherApplication.userInfo
            val url = ServiceCreator.getRequestUrl() + "/api/live/getRoomInfo?uid=" + userInfo!!.uid + "&platform=" + platform + "&roomId=" + roomId
            val realUrl = ServiceCreator.getRequestUrl() + "/api/live/getRealUrl?platform=$platform&roomId=$roomId"
            val data = Get<String>(url) // 发起GET请求并返回`String`类型数据
            val realUrlData = Get<String>(realUrl)
            var result: JSONObject = JSONObject.parseObject(data.await()).getJSONObject("data")
            Glide.with(context).load(result.getString("ownerHeadPic")).transition(
                DrawableTransitionOptions.withCrossFade()
            ).into(ownerPic_roomInfo)
            ownerName_roomInfo.text =
                SunnyWeatherApplication.platformName(result.getString("platForm"))
            roomName_roomInfo.text = result.getString("ownerName")
            liveRoom_bar_txt.text = result.getString("roomName")
            isFollowed = (result.getInteger("isFollowed") == 1)
            if (isFollowed) follow_roomInfo.text = "已关注"

            var realUrlResult: JSONObject =
                JSONObject.parseObject(realUrlData.await()).getJSONObject("data")
            val urls: TreeMap<String, ArrayList<JSONObject>> = getRealUrls(realUrlResult)
            handelRatesAndWifi(urls)
        }
    }

    fun refreshUrl() {
        scopeNetLife { // 创建作用域
            val realUrl =
                ServiceCreator.getRequestUrl() + "/api/live/getRealUrl?platform=$platform&roomId=$roomId"
            val realUrlData = Get<String>(realUrl)
            var realUrlResult: JSONObject =
                JSONObject.parseObject(realUrlData.await()).getJSONObject("data")
            val urls: TreeMap<String, ArrayList<JSONObject>> = getRealUrls(realUrlResult)
            handelRatesAndWifi(urls)
        }
    }

    fun getRealUrls(jsonObject: JSONObject): TreeMap<String, ArrayList<JSONObject>> {
        return JSONObject.parseObject(
            jsonObject.toJSONString(),
            object : TypeReference<TreeMap<String, ArrayList<JSONObject>>>() {})
    }

    private fun handelRatesAndWifi(urls: TreeMap<String, ArrayList<JSONObject>>?) {
        if (urls.isNullOrEmpty()) {
            Toast.makeText(context, "获取直播源为空", Toast.LENGTH_SHORT).show()
            return
        }
        val defaultDefinition: String = if (NetworkUtils.isMobileData()) {
            Toast.makeText(context, "正在使用流量", Toast.LENGTH_SHORT).show()
            sharedPreferences.getString("default_definition_4G", "最高")!!
        } else {
            sharedPreferences.getString("default_definition_wifi", "最高")!!
        }

        val urlList = urls.firstEntry()!!.value
        val urlFinalObj: JSONObject = when (defaultDefinition) {
            "最低" -> urlList.last()
            "中等" -> urlList[urlList.size / 2]
            else -> urlList[0]
        }
        mDefinitionControlView?.updateRateSelection(urls, urlFinalObj.getString("qualityName"))
        onRateChange(urlFinalObj.getString("playUrl"), true)
    }
}