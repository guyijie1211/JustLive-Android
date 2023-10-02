package com.sunnyweather.android.util.dkplayer;

import static xyz.doikki.videoplayer.util.PlayerUtils.dp2px;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.alibaba.fastjson.JSONObject;
import com.sunnyweather.android.R;
import com.sunnyweather.android.ui.liveRoom.LiveRoomActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
    private String currentRateSource;
    private String currentRate;

    private TextView currentSourceTextView;
    private TextView currentRateTextView;

    private TreeMap<String, ArrayList<JSONObject>> mMultiRateData;
    private HashMap<String, String> rateMap;

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
    private ScrollView multiRateContainer;
    private LinearLayout multiRateSourceContainer;
    private LinearLayout multiRateNameContainer;

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
        mDefinition = findViewById(R.id.tv_definition);
        mDefinition.setOnClickListener(this);
        multiRateContainer = findViewById(R.id.multi_rate_layout);
        multiRateSourceContainer = findViewById(R.id.rate_source);
        multiRateNameContainer = findViewById(R.id.rate_name);
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
            liveRoomActivity.changeRoomInfoVisible(false);
            hideSetting(multiRateContainer, 200f);
            hideSetting(danmu_setting_container, 340f);
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
        } else if (id == R.id.tv_definition) {
            if (multiRateContainer.getVisibility() == INVISIBLE) {
                onRateSwitchListener.onMultiRateShowChanged();
                showSetting(multiRateContainer);
                hideBottom();
            } else {
                hideSetting(multiRateContainer, 200f);
            }
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

    public void updateRateSelection(TreeMap<String, ArrayList<JSONObject>> multiRateData, String selectRateName) {
        rateMap = new HashMap<>();
        boolean isFirstSource = true;
        mDefinition.setText(selectRateName);
        multiRateSourceContainer.removeAllViews();
        multiRateNameContainer.removeAllViews();
        for (Map.Entry<String, ArrayList<JSONObject>> entry : multiRateData.entrySet()) {
            String sourceName = entry.getKey();
            ArrayList<JSONObject> rateObjList = entry.getValue();
            // 处理线路
            TextView sourceTextView = getNewRateTextView(sourceName);
            sourceTextView.setOnClickListener(textView -> {
                // 如果当前线路已经选中了, 就不操作
                if (currentSourceTextView == textView) return;
                // 切换选中效果
                currentSourceTextView.setTextColor(Color.WHITE);
                sourceTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.teal_200));
                // 设置当前选中线路
                currentSourceTextView = sourceTextView;
                currentRateSource = sourceName;
                // 切换url
                switchDefinition(sourceName, currentRate);
            });
            // 当前选中线路
            if (isFirstSource) {
                currentSourceTextView = sourceTextView;
                currentRateSource = sourceName;
                sourceTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.teal_200));
                isFirstSource = false;
            }
            multiRateSourceContainer.addView(sourceTextView);

            // 处理清晰度
            for (JSONObject rateObj : rateObjList) {
                String qualityName = rateObj.getString("qualityName");
                String playUrl = rateObj.getString("playUrl");
                String rateSource = rateObj.getString("sourceName");

                rateMap.put(rateSource + qualityName, playUrl);
                // 只渲染当前线路的 清晰度view
                if (currentRateSource.equalsIgnoreCase(rateSource)) {
                    TextView rateTextView = getNewRateTextView(qualityName);
                    rateTextView.setOnClickListener(textView -> {
                        if (currentRateTextView == rateTextView) return;
                        // 切换选中效果
                        currentRateTextView.setTextColor(Color.WHITE);
                        rateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.teal_200));
                        // 设置当前选中信息
                        currentRateTextView = rateTextView;
                        currentRate = qualityName;
                        // 切换url
                        switchDefinition(currentRateSource, qualityName);
                    });
                    // 当前选中的清晰度
                    if (selectRateName.equalsIgnoreCase(qualityName)) {
                        currentRateTextView = rateTextView;
                        currentRate = qualityName;
                        rateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.teal_200));
                    }
                    multiRateNameContainer.addView(rateTextView);
                }
            }
        }
    }

    private TextView getNewRateTextView(String text) {
        TextView rateTextView = new TextView(getContext());
        rateTextView.setText(text);
        rateTextView.setTextColor(Color.WHITE);
        rateTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        // margin
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(35, 30, 0, 0);//4个参数按顺序分别是左上右下
        rateTextView.setLayoutParams(layoutParams);

        return rateTextView;
    }

    private void switchDefinition(String sourceName, String rateName) {
        mControlWrapper.hide();
        mControlWrapper.stopProgress();
        mDefinition.setText(rateName);
        String playUrl = rateMap.get(sourceName + rateName);
        mOnRateSwitchListener.onRateChange(playUrl, false);
        hideSetting(multiRateContainer, 200f);
    }

    public interface OnRateSwitchListener {
        void onRateChange(String url, Boolean isInit);

        void onDanmuShowChange();
        void onDanmuSettingShowChanged();

        void startFloat();

        void onMultiRateShowChanged();
    }

    public void setOnRateSwitchListener(YJLiveControlView.OnRateSwitchListener onRateSwitchListener) {
        mOnRateSwitchListener = onRateSwitchListener;
    }

    //处理设置页面的显示
    private void handleSetting() {
        if (danmu_setting_container.getVisibility() == INVISIBLE) {
            onRateSwitchListener.onDanmuSettingShowChanged();
            showSetting(danmu_setting_container);
        } else {
            hideSetting(danmu_setting_container, 340f);
        }
    }

    //显示设置页面
    private void showSetting(View view) {
        view.animate()
                .translationX(0f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(VISIBLE);
                    }
                }).start();
        hideBottom();
    }

    //隐藏设置
    private void hideSetting(View view, float dpValue) {
        view.animate()
                .translationX(dp2px(getContext(), dpValue))
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(INVISIBLE);
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
