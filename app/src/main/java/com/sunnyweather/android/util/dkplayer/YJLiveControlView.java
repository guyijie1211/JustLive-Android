package com.sunnyweather.android.util.dkplayer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.gson.internal.LinkedTreeMap;
import com.sunnyweather.android.R;
import com.sunnyweather.android.ui.liveRoom.LiveRoomActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.L;
import xyz.doikki.videoplayer.util.PlayerUtils;

public class YJLiveControlView extends FrameLayout implements IControlComponent, View.OnClickListener{
    private OnRateSwitchListener onRateSwitchListener;
    protected ControlWrapper mControlWrapper;
    private Boolean showDanmu = true;
    private TextView mDefinition;
    private LiveRoomActivity liveRoomActivity;

    private PopupWindow mPopupWindow;
    private List<String> mRateStr;
    private LinearLayout mPopLayout;

    private int mCurIndex;

    private LinkedTreeMap<String, String> mMultiRateData;

    private YJLiveControlView.OnRateSwitchListener mOnRateSwitchListener;

    private ImageView mFullScreen;
    private FrameLayout mBottomContainer;
    private ProgressBar mBottomProgress;
    private ImageView mPlayButton;
    private ImageView danmu_show;

    private boolean mIsDragging;

    private boolean mIsShowBottomProgress = true;

    public YJLiveControlView(@NonNull Context context) {
        super(context);
    }

    public YJLiveControlView(@NonNull Context context, LiveRoomActivity liveRoomActivity) {
        super(context);
        this.liveRoomActivity = liveRoomActivity;
    }

    public YJLiveControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public YJLiveControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    {
        onRateSwitchListener = (OnRateSwitchListener) getContext();
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        mFullScreen = findViewById(xyz.doikki.videocontroller.R.id.fullscreen);
        mFullScreen.setOnClickListener(this);
        mBottomContainer = findViewById(R.id.controllerContainer);
        mPlayButton = findViewById(xyz.doikki.videocontroller.R.id.iv_play);
        mPlayButton.setOnClickListener(this);
        mBottomProgress = findViewById(xyz.doikki.videocontroller.R.id.bottom_progress);
        danmu_show = findViewById(R.id.danmu_show);
        danmu_show.setOnClickListener(this);
        ImageView refresh = findViewById(R.id.iv_refresh);
        refresh.setOnClickListener(this);

        //增加清晰度切换
        mPopupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_rate_pop, this, false);
        mPopupWindow.setContentView(mPopLayout);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setClippingEnabled(false);
        mDefinition = findViewById(R.id.tv_definition);
        mDefinition.setOnClickListener(v -> showRateMenu());
    }
    private void showRateMenu() {
        mPopLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mPopupWindow.showAsDropDown(mDefinition, -((mPopLayout.getMeasuredWidth() - mDefinition.getMeasuredWidth()) / 2),
                -(mPopLayout.getMeasuredHeight() + mDefinition.getMeasuredHeight() + PlayerUtils.dp2px(getContext(), 10)));
    }
    protected int getLayoutId() {
        return R.layout.layout_definition_control_view;
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
            liveRoomActivity.changeRoomInfoVisible(true);
            mBottomContainer.setVisibility(VISIBLE);
            if (anim != null) {
                mBottomContainer.startAnimation(anim);
            }
            if (mIsShowBottomProgress) {
                mBottomProgress.setVisibility(GONE);
            }
        } else {
            liveRoomActivity.changeRoomInfoVisible(false);
            mPopupWindow.dismiss();
            mBottomContainer.setVisibility(GONE);
            if (anim != null) {
                mBottomContainer.startAnimation(anim);
            }
            if (mIsShowBottomProgress) {
                mBottomProgress.setVisibility(VISIBLE);
                AlphaAnimation animation = new AlphaAnimation(0f, 1f);
                animation.setDuration(300);
                mBottomProgress.startAnimation(animation);
            }
        }
    }
    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_IDLE:
            case VideoView.STATE_PLAYBACK_COMPLETED:
                setVisibility(GONE);
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                break;
            case VideoView.STATE_START_ABORT:
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
                setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYING:
                mPlayButton.setSelected(true);
                if (mIsShowBottomProgress) {
                    if (mControlWrapper.isShowing()) {
                        mBottomProgress.setVisibility(GONE);
                        mBottomContainer.setVisibility(VISIBLE);
                    } else {
                        mBottomContainer.setVisibility(GONE);
                        mBottomProgress.setVisibility(VISIBLE);
                    }
                } else {
                    mBottomContainer.setVisibility(GONE);
                }
                setVisibility(VISIBLE);
                //开始刷新进度
                mControlWrapper.startProgress();
                break;
            case VideoView.STATE_PAUSED:
                mPlayButton.setSelected(false);
                break;
            case VideoView.STATE_BUFFERING:
            case VideoView.STATE_BUFFERED:
                mPlayButton.setSelected(mControlWrapper.isPlaying());
                break;
        }
    }
    @Override
    public void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                mFullScreen.setSelected(false);
                mPopupWindow.dismiss();
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                mFullScreen.setSelected(true);
                break;
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mBottomContainer.setPadding(0, 0, 0, 0);
                mBottomProgress.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mBottomContainer.setPadding(cutoutHeight, 0, 0, 0);
                mBottomProgress.setPadding(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mBottomContainer.setPadding(0, 0, cutoutHeight, 0);
                mBottomProgress.setPadding(0, 0, cutoutHeight, 0);
            }
        }
    }
    @Override
    public void setProgress(int duration, int position) {
        if (mIsDragging) {
            return;
        }
    }
    @Override
    public void onLockStateChanged(boolean isLocked) {
        onVisibilityChanged(!isLocked, null);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == xyz.doikki.videocontroller.R.id.fullscreen) {
            toggleFullScreen();
        } else if (id == xyz.doikki.videocontroller.R.id.iv_play) {
            mControlWrapper.togglePlay();
        } else if (id == R.id.iv_refresh) {
            mControlWrapper.replay(true);
        } else if (id == R.id.danmu_show) {
            danmu_show.setSelected(showDanmu);
            showDanmu = !showDanmu;
            onRateSwitchListener.onDanmuShowChange();
        }
    }

    /**
     * 横竖屏切换
     */
    private void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        mControlWrapper.toggleFullScreen(activity);
        // 下面方法会根据适配宽高决定是否旋转屏幕
//        mControlWrapper.toggleFullScreenByVideoSize(activity);
    }

    public void setData(LinkedTreeMap<String, String> multiRateData) {
        mMultiRateData = multiRateData;
        if (mDefinition != null && TextUtils.isEmpty(mDefinition.getText())) {
            L.d("multiRate");
            if (multiRateData == null) return;
            mRateStr = new ArrayList<>();
            int index = 0;
            ListIterator<Map.Entry<String, String>> iterator = new ArrayList<>(multiRateData.entrySet()).listIterator(multiRateData.size());
            while (iterator.hasPrevious()) {//反向遍历
                Map.Entry<String, String> entry = iterator.previous();
                mRateStr.add(entry.getKey());
                TextView rateItem = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_rate_item, null);
                rateItem.setText(entry.getKey());
                rateItem.setTag(index);
                rateItem.setOnClickListener(rateOnClickListener);
                mPopLayout.addView(rateItem);
                index++;
            }
            ((TextView) mPopLayout.getChildAt(index - 1)).setTextColor(ContextCompat.getColor(getContext(), R.color.purple_500));
            mDefinition.setText(mRateStr.get(index - 1));
            mCurIndex = index - 1;
        }
    }

    private OnClickListener rateOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            if (mCurIndex == index) return;
            ((TextView) mPopLayout.getChildAt(mCurIndex)).setTextColor(Color.BLACK);
            ((TextView) mPopLayout.getChildAt(index)).setTextColor(ContextCompat.getColor(getContext(), R.color.purple_500));
            mDefinition.setText(mRateStr.get(index));
            switchDefinition(mRateStr.get(index));
            mPopupWindow.dismiss();
            mCurIndex = index;
        }
    };

    private void switchDefinition(String s) {
        mControlWrapper.hide();
        mControlWrapper.stopProgress();
        String url = mMultiRateData.get(s);
        if (mOnRateSwitchListener != null)
            mOnRateSwitchListener.onRateChange(url);
    }

    public interface OnRateSwitchListener {
        void onRateChange(String url);
        void onDanmuShowChange();
    }

    public void setOnRateSwitchListener(YJLiveControlView.OnRateSwitchListener onRateSwitchListener) {
        mOnRateSwitchListener = onRateSwitchListener;
    }

}
