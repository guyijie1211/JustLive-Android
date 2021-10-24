package com.sunnyweather.android.util.dkplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ScreenUtils;

import xyz.doikki.videocontroller.component.CompleteView;
import xyz.doikki.videocontroller.component.ErrorView;
import xyz.doikki.videoplayer.controller.GestureVideoController;
import xyz.doikki.videoplayer.util.PlayerUtils;

/**
 * 悬浮播放控制器
 * Created by Doikki on 2017/6/1.
 */
public class FloatController extends GestureVideoController {
    private int sizeType;
    private View floatView;
    private String platform;
    private String roomId;
    private WindowManager.LayoutParams params;
    private WindowManager mWindowManager;

    public FloatController(@NonNull Context context, @NonNull View view, @NonNull WindowManager.LayoutParams params, @NonNull String platform, @NonNull String roomId) {
        super(context);
        this.platform = platform;
        this.roomId = roomId;
        addControlComponent(new CompleteView(getContext()));
        addControlComponent(new ErrorView(getContext()));
        addControlComponent(new PipControlView(getContext(), platform, roomId));
        sizeType = 2;
        mWindowManager = PlayerUtils.getWindowManager(getContext().getApplicationContext());
        this.floatView = view;
        this.params = params;
    }

    public FloatController(@NonNull Context context) {
        super(context);
    }

    public FloatController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (PIPManager.getInstance(platform, roomId).getActClass() != null) {
            Intent intent = new Intent(getContext(), PIPManager.getInstance(platform, roomId).getActClass());
            intent.putExtra("platform", platform);
            intent.putExtra("roomId", roomId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (sizeType == 1) { //中等
            int width;
            if (ScreenUtils.isLandscape()) {
                width = getWindowWidth() / 7 * 3;
            } else {
                width = getWindowWidth() / 7 * 5 ;
            }
            params.width = width;
            params.height = width * 9 / 16;
            mWindowManager.updateViewLayout(floatView, params);
            sizeType = 2;
            return true;
        }
        if (sizeType == 2) { //最大
            int width;
            if (ScreenUtils.isLandscape()) {
                width = getWindowWidth() / 3 * 2;
            } else {
                width = getWindowWidth() ;
            }
            params.width = width;
            params.height = width * 9 / 16;
            mWindowManager.updateViewLayout(floatView, params);
            sizeType = 3;
            return true;
        }
        if (sizeType == 3) { //最小
            int width;
            if (ScreenUtils.isLandscape()) {
                width = getWindowWidth() / 4;
            }  else {
                width = getWindowWidth() / 2;
            }
            params.width = width;
            params.height = width * 9 / 16;
            mWindowManager.updateViewLayout(floatView, params);
            sizeType = 1;
            return true;
        }
        return false;
    }

    private int getWindowWidth() {
        Point point = new Point();
        mWindowManager.getDefaultDisplay().getRealSize(point);
        return point.x;
    }
}
