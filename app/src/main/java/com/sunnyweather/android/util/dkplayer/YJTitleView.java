package com.sunnyweather.android.util.dkplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.sunnyweather.android.R;
import com.sunnyweather.android.ui.customerUIs.bottomPop.TVBottomPop;
import com.sunnyweather.android.ui.customerUIs.bottomPop.TimeBottomPop;
import com.sunnyweather.android.ui.liveRoom.LiveRoomActivity;

import xyz.doikki.videocontroller.component.TitleView;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.PlayerUtils;

public class YJTitleView extends FrameLayout implements IControlComponent, View.OnClickListener {
    private YJLiveControlView.OnRateSwitchListener onRateSwitchListener;

    private ControlWrapper mControlWrapper;
    Boolean isDarkTheme;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private TextView mSysTime;//系统当前时间
    private ImageView more;
    private ImageView back;
    private ImageView tv;

    private BatteryReceiver mBatteryReceiver;
    private boolean mIsRegister;//是否注册BatteryReceiver

    public YJTitleView(@NonNull Context context) {
        super(context);
    }

    public YJTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public YJTitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int theme = sharedPreferences.getInt("theme", R.style.SunnyWeather);
        isDarkTheme = (theme == R.style.nightTheme);
        setVisibility(GONE);
        onRateSwitchListener = (YJLiveControlView.OnRateSwitchListener) getContext();
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_title_view, this, true);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        mTitleContainer = findViewById(R.id.title_container);
        mTitle = findViewById(R.id.title);
        mSysTime = findViewById(R.id.sys_time);
        more = findViewById(R.id.player_more);
        more.setOnClickListener(this);
        tv = findViewById(R.id.player_to_tv);
        tv.setOnClickListener(this);
        //电量
        ImageView batteryLevel = findViewById(R.id.iv_battery);
        mBatteryReceiver = new BatteryReceiver(batteryLevel);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.player_more) {
            onRateSwitchListener.onDanmuSettingShowChanged();
            new XPopup.Builder(getContext())
                    .hasShadowBg(false)
                    .isClickThrough(true)
                    .isDarkTheme(isDarkTheme)
                    .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                    .asAttachList(new String[]{"外置播放器", "定时关闭"},
                            null,
                            (position, text) -> {
                                switch (position) {
                                    case 0:
                                        playWithSystemPlayers();
                                        break;
                                    case 1:
                                        closeByTime();
                                        break;
                                }
                            }, 0, 0/*, Gravity.LEFT*/)
                    .show();
        } else if (id == R.id.back) {
            Activity activity = PlayerUtils.scanForActivity(getContext());
            if (activity != null && mControlWrapper.isFullScreen()) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mControlWrapper.stopFullScreen();
            } else if (activity != null){
                activity.onBackPressed();
            }
        } else if (id == R.id.player_to_tv) {
            new XPopup.Builder(getContext())
                    .popupHeight((int) (ScreenUtils.getScreenHeight()*.5f))
                    .isDestroyOnDismiss(true)
                    .asCustom(new TVBottomPop(getContext()))
                    .show();
        }
    }

    private void playWithSystemPlayers() {
        Activity activity = (LiveRoomActivity)getContext();
        String url = ((LiveRoomActivity)activity).getUrl();
        Uri uri = Uri.parse(url);
        // 调用系统自带的播放器来播放流媒体视频
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "video/mp4");
        getContext().startActivity(intent);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mIsRegister) {
            getContext().unregisterReceiver(mBatteryReceiver);
            mIsRegister = false;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mIsRegister) {
            getContext().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            mIsRegister = true;
        }
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        if (isVisible) {
            if (getVisibility() == GONE) {
                mSysTime.setText(PlayerUtils.getCurrentSystemTime());
                setVisibility(VISIBLE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        } else {
            if (getVisibility() == VISIBLE) {
                setVisibility(GONE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_IDLE:
            case VideoView.STATE_START_ABORT:
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
            case VideoView.STATE_PLAYBACK_COMPLETED:
                setVisibility(GONE);
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        if (playerState == VideoView.PLAYER_FULL_SCREEN) {
            if (mControlWrapper.isShowing() && !mControlWrapper.isLocked()) {
                setVisibility(VISIBLE);
                mSysTime.setText(PlayerUtils.getCurrentSystemTime());
            }
            mTitle.setSelected(true);
        } else {
            setVisibility(GONE);
            mTitle.setSelected(false);
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mTitleContainer.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mTitleContainer.setPadding(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mTitleContainer.setPadding(0, 0, cutoutHeight, 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        if (isLocked) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            mSysTime.setText(PlayerUtils.getCurrentSystemTime());
        }
    }

    private static class BatteryReceiver extends BroadcastReceiver {
        private ImageView pow;

        public BatteryReceiver(ImageView pow) {
            this.pow = pow;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras == null) return;
            int current = extras.getInt("level");// 获得当前电量
            int total = extras.getInt("scale");// 获得总电量
            int percent = current * 100 / total;
            pow.getDrawable().setLevel(percent);
        }
    }

    private void closeByTime() {
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true)
                .asCustom(new TimeBottomPop(getContext()))
                .show();
    }
}
