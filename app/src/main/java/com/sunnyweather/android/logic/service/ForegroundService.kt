package com.sunnyweather.android.logic.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sunnyweather.android.R
import com.sunnyweather.android.ui.liveRoom.LiveRoomActivity

class ForegroundService : Service() {
    private var platform = ""
    private var roomId = ""
    private var roomInfo = ""
    private lateinit var mNotificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("foreground", "后台播放通知", NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager.createNotificationChannel(channel)
        }
        mNotificationManager.cancelAll()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        platform = intent?.getStringExtra("platform")?:""
        roomId = intent?.getStringExtra("roomId")?:""
        roomInfo = intent?.getStringExtra("roomInfo")?:""
        val intent = Intent(this, LiveRoomActivity::class.java)
        intent.putExtra("platform", platform)
        intent.putExtra("roomId", roomId)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        val notification = NotificationCompat.Builder(this, "foreground")
            .setContentTitle("正在后台播放")
            .setContentText(roomInfo)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()
        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mNotificationManager.cancel(1)
        stopForeground(true)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}