package com.sunnyweather.android.util.dkplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.doikki.videocontroller.StandardVideoController;

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
