package com.sunnyweather.android.ui.liveRoom

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.internal.LinkedTreeMap
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.RoomInfo
import com.sunnyweather.android.util.dkplayer.YJLiveControlView
import kotlinx.android.synthetic.main.activity_liveroom.*
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videocontroller.component.*
import com.sunnyweather.android.util.dkplayer.PIPManager
import com.sunnyweather.android.util.dkplayer.YJstandardController
import com.yanzhenjie.permission.AndPermission
import xyz.doikki.videoplayer.exo.ExoMediaPlayer
import xyz.doikki.videoplayer.player.VideoView
import xyz.doikki.videoplayer.player.VideoViewManager

class LiveRoomActivity : AppCompatActivity(), YJLiveControlView.OnRateSwitchListener {
    private val viewModel by lazy { ViewModelProvider(this).get(LiveRoomViewModel::class.java) }
    private var mDefinitionControlView: YJLiveControlView? = null
    private lateinit var mPIPManager: PIPManager
    private var controller: YJstandardController? = null
    private var videoView: VideoView<ExoMediaPlayer>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("test","roomOnCreate")
        setContentView(R.layout.activity_liveroom)
        //设置16:9的高宽
        val lp = player_container.layoutParams
        val point = Point()
        this.windowManager.defaultDisplay.getRealSize(point)
        lp.height = point.x * 9 / 16
        Log.i("test", lp.height.toString())
        player_container.layoutParams = lp

        mPIPManager = PIPManager.getInstance()
        controller = YJstandardController(this)
        addControlComponents(controller!!)
        controller!!.setDoubleTapTogglePlayEnabled(false)
        controller!!.setEnableInNormal(true)

        Log.i("test", "正常变窗口")
        mPIPManager.actClass = LiveRoomActivity::class.java
        val platform = intent.getStringExtra("platform")?:""
        val roomId = intent.getStringExtra("roomId")?:""
        viewModel.getRealUrl(platform, roomId)
        viewModel.getRoomInfo("0eb26a33e68d4582858a74abf5a645d5", platform, roomId)


        videoView = VideoViewManager.instance().get("pip") as VideoView<ExoMediaPlayer>?
        player_container.addView(videoView)
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
            Log.i("test", "窗口变正常")
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
        mDefinitionControlView = YJLiveControlView(this)
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