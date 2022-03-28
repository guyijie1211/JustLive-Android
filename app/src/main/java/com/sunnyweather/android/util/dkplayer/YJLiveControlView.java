package com.sunnyweather.android.util.dkplayer;

import static xyz.doikki.videoplayer.util.PlayerUtils.dp2px;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

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
import xyz.doikki.videoplayer.util.PlayerUtils;

public class YJLiveControlView extends FrameLayout implements IControlComponent, View.OnClickListener {
    private OnRateSwitchListener onRateSwitchListener;
    protected ControlWrapper mControlWrapper;
    private Boolean showDanmu = true;
    private TextView mDefinition;
    private LiveRoomActivity liveRoomActivity;
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

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
    private ImageView mStartFloat;
    private ImageView danmu_setting;
    private LinearLayout bottom_container;
    private LinearLayout danmu_setting_container;

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
        LiveRoomActivity context = (LiveRoomActivity) getContext();
        onRateSwitchListener = (OnRateSwitchListener) getContext();
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        mFullScreen = findViewById(xyz.doikki.videocontroller.R.id.fullscreen);
        mFullScreen.setOnClickListener(this);
        mBottomContainer = findViewById(R.id.controllerContainer);
        bottom_container = findViewById(R.id.bottom_container);
        mPlayButton = findViewById(xyz.doikki.videocontroller.R.id.iv_play);
        mPlayButton.setOnClickListener(this);
        mBottomProgress = findViewById(xyz.doikki.videocontroller.R.id.bottom_progress);
        danmu_show = findViewById(R.id.danmu_show);
        danmu_show.setOnClickListener(this);
        danmu_setting = findViewById(R.id.danmu_setting);
        danmu_setting.setOnClickListener(this);
        danmu_setting_container = findViewById(R.id.setting_layout);
        danmu_setting_container.setOnClickListener(this);
        mStartFloat = findViewById(R.id.startFloat);
        mStartFloat.setOnClickListener(this);
        ImageView refresh = findViewById(R.id.iv_refresh);
        refresh.setOnClickListener(this);
        danmu_show.setSelected(!context.getSetting().isShow());

        //增加清晰度切换
        mPopupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_rate_pop, this, false);
        mPopupWindow.setContentView(mPopLayout);
//        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setClippingEnabled(false);
        mDefinition = findViewById(R.id.tv_definition);
        mDefinition.setOnClickListener(v -> showRateMenu());
    }
    private void showRateMenu() {
        onRateSwitchListener.onDanmuSettingShowChanged();
        mPopLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mPopupWindow.showAsDropDown(mDefinition, -((mPopLayout.getMeasuredWidth() - mDefinition.getMeasuredWidth()) / 2),
                -(mPopLayout.getMeasuredHeight() + mDefinition.getMeasuredHeight() + dp2px(getContext(), 10)));
    }
    protected int getLayoutId() {
        return R.layout.layout_definition_control_view;
    }
    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean fullScreenStart = sharedPreferences.getBoolean("full_screen_start", false);
        if (fullScreenStart) {
            toggleFullScreen();
        }
    }
    @Override
    public View getView() {
        return this;
    }
    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        if (isVisible) {
            liveRoomActivity.changeRoomInfoVisible(true);
//            video_drawer.setVisibility(VISIBLE);
            showBottom();
            mBottomContainer.setVisibility(VISIBLE);
            if (anim != null) {
                mBottomContainer.startAnimation(anim);
            }
            if (mIsShowBottomProgress) {
                mBottomProgress.setVisibility(GONE);
            }
        } else {
//            video_drawer.setVisibility(GONE);
            liveRoomActivity.changeRoomInfoVisible(false);
            mPopupWindow.dismiss();
            hideSetting();
            hideBottom();
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
        LiveRoomActivity context = (LiveRoomActivity) getContext();
        context.hideViews();
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                context.stopFullScreen();
                danmu_show.setVisibility(INVISIBLE);
                danmu_setting.setVisibility(INVISIBLE);
                mFullScreen.setSelected(false);
                mPopupWindow.dismiss();
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                context.startFullScreen();
                danmu_show.setVisibility(VISIBLE);
                danmu_setting.setVisibility(VISIBLE);
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
            LiveRoomActivity activity = (LiveRoomActivity)getContext();
            activity.refreshUrl();
        } else if (id == R.id.danmu_show) {
            danmu_show.setSelected(showDanmu);
            showDanmu = !showDanmu;
            onRateSwitchListener.onDanmuShowChange();
        } else if (id == R.id.danmu_setting) {
            handleSetting();
        } else if (id == R.id.startFloat) {
            onRateSwitchListener.startFloat();
        }
    }

    /**
     * 横竖屏切换
     */
    public void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        mControlWrapper.toggleFullScreen(activity);
        // 下面方法会根据适配宽高决定是否旋转屏幕
//        mControlWrapper.toggleFullScreenByVideoSize(activity);
    }

    public void setData(LinkedTreeMap<String, String> multiRateData, String txt) {
        mMultiRateData = multiRateData;
        if (mDefinition != null && TextUtils.isEmpty(mDefinition.getText())) {
            if (multiRateData == null) return;
            mRateStr = new ArrayList<>();
            int index = 0;
            mPopLayout.removeAllViews();
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
            ((TextView) mPopLayout.getChildAt(mRateStr.indexOf(txt))).setTextColor(ContextCompat.getColor(getContext(), R.color.teal_200));
            mDefinition.setText(txt);
            mCurIndex = mRateStr.indexOf(txt);
        }
    }

    private OnClickListener rateOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            if (mCurIndex == index) return;
            ((TextView) mPopLayout.getChildAt(mCurIndex)).setTextColor(Color.WHITE);
            ((TextView) mPopLayout.getChildAt(index)).setTextColor(ContextCompat.getColor(getContext(), R.color.teal_200));
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
        void onDanmuSettingShowChanged();
        void startFloat();
    }

    public void setOnRateSwitchListener(YJLiveControlView.OnRateSwitchListener onRateSwitchListener) {
        mOnRateSwitchListener = onRateSwitchListener;
    }
    //处理设置页面的显示
    private void handleSetting() {
        if (danmu_setting_container.getVisibility() == INVISIBLE) {
            onRateSwitchListener.onDanmuSettingShowChanged();
            showSetting();
        } else {
            hideSetting();
        }
    }
    //显示设置页面
    private void showSetting() {
        danmu_setting_container.animate()
                .translationX(0f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        danmu_setting_container.setVisibility(VISIBLE);
                    }
                }).start();
        hideBottom();
    }
    //隐藏设置
    private void hideSetting() {
        danmu_setting_container.animate()
                .translationX(dp2px(getContext(), 340f))
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        danmu_setting_container.setVisibility(INVISIBLE);
                    }
                }).start();
    }
    //隐藏底部
    private void hideBottom(){
        bottom_container.animate()
                .translationY(dp2px(getContext(), 46f))
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        bottom_container.setVisibility(INVISIBLE);
                    }
                }).start();
    }
    //显示底部
    private void showBottom(){
        bottom_container.animate()
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        bottom_container.setVisibility(VISIBLE);
                    }
                }).start();
    }

}
