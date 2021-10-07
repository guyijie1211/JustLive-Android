package com.sunnyweather.android.util.dkplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sunnyweather.android.ui.liveRoom.LiveRoomActivity;

import java.util.Map;

import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.controller.IControlComponent;

public class YJstandardController extends StandardVideoController {
    private Boolean doubleTap = false;

    public YJstandardController(@NonNull Context context) {
        super(context);
    }

    public YJstandardController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public YJstandardController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLockStateChanged(boolean isLocked) {
        doubleTap = isLocked;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (!doubleTap) {
            toggleFullScreen();
        }
        return true;
    }
}
