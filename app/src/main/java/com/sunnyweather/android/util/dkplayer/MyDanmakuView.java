package com.sunnyweather.android.util.dkplayer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.sunnyweather.android.logic.model.DanmuSetting;

import java.util.HashMap;
import java.util.Map;

import com.sunnyweather.android.flame.master.flame.danmaku.controller.DrawHandler;
import com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.BaseDanmaku;
import com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.DanmakuTimer;
import com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.IDanmakus;
import com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.IDisplayer;
import com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.android.DanmakuContext;
import com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.android.Danmakus;
import com.sunnyweather.android.flame.master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import com.sunnyweather.android.flame.master.flame.danmaku.ui.widget.DanmakuView;
import xyz.doikki.videoplayer.BuildConfig;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.player.VideoView;

public class MyDanmakuView extends DanmakuView implements IControlComponent {

    private DanmakuContext mContext;
    private BaseDanmakuParser mParser;
    private DanmuSetting setting;
    private float fps;

    public MyDanmakuView(@NonNull Context context, @NonNull DanmuSetting set, @Nullable float refreshRate) {
        super(context);
        // 设置是否禁止重叠
        fps = refreshRate;
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        setting = set;
        mContext = DanmakuContext.create();
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, setting.getBorder())
                .setDuplicateMergingEnabled(setting.getMerge())
                .setScrollSpeedFactor(getSpeedValue(setting.getSpeed()))
                .setDanmakuTransparency(setting.getAlpha())
                .setScaleTextSize(setting.getSize())
                .setMaximumLines(getLineNum(setting.getShowArea()))
                .setDanmakuBold(setting.getBold())
                .preventOverlapping(overlappingEnablePair)
                .setDanmakuMargin(10);
        Log.i("test", "屏幕刷新率" + refreshRate);
        mContext.setFrameUpateRate(getFps(setting.getFps()));
    }

    public MyDanmakuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDanmakuView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        mParser = new BaseDanmakuParser() {
            @Override
            protected IDanmakus parse() {
                return new Danmakus();
            }
        };
        setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                start();
            }
            @Override
            public void updateTimer(DanmakuTimer timer) { }
            @Override
            public void danmakuShown(BaseDanmaku danmaku) { }
            @Override
            public void drawingFinished() { }
        });
        showFPS(BuildConfig.DEBUG);
        enableDanmakuDrawingCache(true);
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
    }
    @Override
    public View getView() {
        return this;
    }
    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }
    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_IDLE:
                release();
                break;
            case VideoView.STATE_PREPARING:
                if (isPrepared()) {
                    restart();
                }
                prepare(mParser, mContext);
                break;
            case VideoView.STATE_PLAYING:
                if (isPrepared() && isPaused()) {
                    resume();
                }
                break;
            case VideoView.STATE_PAUSED:
                if (isPrepared()) {
                    pause();
                }
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                clear();
                clearDanmakusOnScreen();
                break;
        }
    }
    @Override
    public void onPlayerStateChanged(int playerState) {

    }
    @Override
    public void setProgress(int duration, int position) {

    }
    @Override
    public void onLockStateChanged(boolean isLocked) {

    }

    /**
     * 发送文字弹幕
     * @param text   弹幕文字
     */
    public void addDanmaku(String text) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null) {
            return;
        }
        danmaku.text = text;
//        danmaku.padding = 5;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = false;
        danmaku.setTime(getCurrentTime());
        danmaku.textSize = 14f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.WHITE;
        danmaku.textShadowColor = Color.BLACK;
        addDanmaku(danmaku);
    }
    //设置弹幕属性
    public void setContext(DanmuSetting setting, String updateItem) {
        switch (updateItem) {
            case "speed":
                mContext.setScrollSpeedFactor(getSpeedValue(setting.getSpeed()));
                break;
            case "alpha":
                mContext.setDanmakuTransparency(setting.getAlpha());
                break;
            case "size":
                mContext.setScaleTextSize(setting.getSize());
                break;
            case "showArea":
                mContext.setMaximumLines(getLineNum(setting.getShowArea()));
                break;
            case "border":
                mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, setting.getBorder());
                break;
            case "merge":
                mContext.setDuplicateMergingEnabled(setting.getMerge());
                break;
            case "bold":
                mContext.setDanmakuBold(setting.getBold());
                break;
            case "fps":
                mContext.setFrameUpateRate(getFps(setting.getFps()));
                break;
        }
    }

    //转换fps
    private int getFps(Boolean value) {
        if (value) {
            Log.i("test", "60");
            return (int) (1000 / fps * 2);
        } else {
            Log.i("test", "120");
            return (int) (1000 / fps);
        }
    }

    //转换最大行数
    private Map<Integer, Integer> getLineNum(float value) {
        Map<Integer, Integer> result = new HashMap<>();
        if (value == 20f) {
            return null;
        } else {
            result.put(BaseDanmaku.TYPE_SCROLL_RL, (int) value);
        }
        return result;
    }

    //转换speed
    private float getSpeedValue(float value) {
        return 4f - value;
    }
}
