package com.sunnyweather.android.util.dkplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import com.billy.android.swipe.SmartSwipe;
//import com.billy.android.swipe.consumer.DrawerConsumer;
import com.blankj.utilcode.util.ScreenUtils;
import com.sunnyweather.android.R;

import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.util.PlayerUtils;

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

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i("test","upupupupupup");
        return super.onSingleTapUp(e);
    }
}
