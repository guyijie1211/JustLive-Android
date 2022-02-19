package com.sunnyweather.android.util.dkplayer;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import xyz.doikki.videoplayer.controller.BaseVideoController;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.controller.MediaPlayerControl;

public abstract class YJBaseVideoController extends BaseVideoController{

    public YJBaseVideoController(@NonNull Context context) {
        super(context);
    }

    public YJBaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public YJBaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setMediaPlayer(MediaPlayerControl mediaPlayer) {
        super.setMediaPlayer(mediaPlayer);
        mControlWrapper = new YJControlWrapper(mediaPlayer, this);
        //绑定ControlComponent和Controller
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            component.attach(mControlWrapper);
        }
        //开始监听设备方向
        mOrientationHelper.setOnOrientationChangeListener(this);
    }

}
