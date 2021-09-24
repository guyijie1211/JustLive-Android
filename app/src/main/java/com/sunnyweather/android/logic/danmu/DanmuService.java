package com.sunnyweather.android.logic.danmu;

import androidx.lifecycle.MutableLiveData;

import com.sunnyweather.android.ui.liveRoom.LiveRoomViewModel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class DanmuService {
    String platform;
    String roomId;
    WebSocket webSocket;
    OkHttpClient mClient = new OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间]
            .build();
    Request request;
    Timer myTimer = new Timer();

    //构造函数
    public DanmuService(String platform, String roomId) {
        this.platform = platform;
        this.roomId = roomId;
    }

    //连接弹幕服务器
    public void connect(ArrayList<LiveRoomViewModel.DanmuInfo> resultList, MutableLiveData<Integer> danmuNum) {
        if (platform.equals("bilibili")) return;
        //开始连接
        request = DanmuUtils.getRequest(platform, roomId);
        webSocket = mClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                DanmuUtils.sendOpenMsg(webSocket, platform, roomId, myTimer);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                DanmuUtils.onMessage(platform, bytes, resultList, danmuNum);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                DanmuUtils.onMessageString(platform, text, resultList, danmuNum);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                //连接关闭...
                System.out.println("onClosed");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
                super.onFailure(webSocket, throwable, response);
                //连接失败...
                System.out.println("onFailure");
            }
        });
    }

    //停止接受消息
    public void stop() {
        if (!platform.equals("bilibili")) {
            webSocket.cancel();
        }
        myTimer.cancel();
    }
}
