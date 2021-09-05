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
    private WindowManager.LayoutParams params;
    private WindowManager mWindowManager;

    public FloatController(@NonNull Context context, @NonNull View view, @NonNull WindowManager.LayoutParams params) {
        super(context);
        sizeType = 1;
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
        addControlComponent(new CompleteView(getContext()));
        addControlComponent(new ErrorView(getContext()));
        addControlComponent(new PipControlView(getContext()));
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.i("test","单击");
        if (PIPManager.getInstance().getActClass() != null) {
            Intent intent = new Intent(getContext(), PIPManager.getInstance().getActClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (sizeType == 1) { //屏幕宽度的三分之二
            int width = getWindowWidth() / 7 * 5;
            params.width = width;
            params.height = width * 9 / 16;
            mWindowManager.updateViewLayout(floatView, params);
            sizeType = 2;
            return true;
        }
        if (sizeType == 2) { //占满手机宽度
            int width = getWindowWidth();
            params.width = width;
            params.height = width * 9 / 16;
            mWindowManager.updateViewLayout(floatView, params);
            sizeType = 3;
            return true;
        }
        if (sizeType == 3) { //占满手机宽度
            int width = PlayerUtils.dp2px(getContext(), 180);
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
