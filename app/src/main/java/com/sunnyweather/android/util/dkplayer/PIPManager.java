package com.sunnyweather.android.util.dkplayer;

import android.view.View;

import com.sunnyweather.android.SunnyWeatherApplication;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.player.VideoViewManager;

/**
 * 悬浮播放
 * Created by Doikki on 2018/3/30.
 */

public class PIPManager {

    private static PIPManager instance;
    private VideoView mVideoView;
    private FloatView mFloatView;
    private FloatController mFloatController;
    private boolean mIsShowing;
    private int mPlayingPosition = -1;
    private Class mActClass;


    private PIPManager() {
        mVideoView = new VideoView(SunnyWeatherApplication.context);
        VideoViewManager.instance().add(mVideoView, "pip");
        mFloatView = new FloatView(SunnyWeatherApplication.context, 0, 0);
        mFloatController = new FloatController(SunnyWeatherApplication.context, mFloatView, mFloatView.getWindowParams());
        mFloatController.setDoubleTapTogglePlayEnabled(false);
    }

    private PIPManager(VideoView videoView) {
        mVideoView = videoView;
        VideoViewManager.instance().add(mVideoView, "pip");
        mFloatView = new FloatView(SunnyWeatherApplication.context, 0, 0);
        mFloatController = new FloatController(SunnyWeatherApplication.context, mFloatView, mFloatView.getWindowParams());
        mFloatController.setDoubleTapTogglePlayEnabled(false);
    }

    public static PIPManager getInstance() {
        if (instance == null) {
            synchronized (PIPManager.class) {
                if (instance == null) {
                    instance = new PIPManager();
                }
            }
        }
        return instance;
    }

    public static PIPManager getInstance(VideoView videoView) {
        if (instance == null) {
            synchronized (PIPManager.class) {
                if (instance == null) {
                    instance = new PIPManager(videoView);
                }
            }
        }
        return instance;
    }

    public void startFloatWindow() {
        if (mIsShowing) return;
        Utils.removeViewFormParent(mVideoView);
        mVideoView.setVideoController(mFloatController);
        mFloatController.setPlayState(mVideoView.getCurrentPlayState());
        mFloatController.setPlayerState(mVideoView.getCurrentPlayerState());
        mFloatView.addView(mVideoView);
        mFloatView.addToWindow();
        mIsShowing = true;
    }

    public void stopFloatWindow() {
        if (!mIsShowing) return;
        mFloatView.removeFromWindow();
        Utils.removeViewFormParent(mVideoView);
        mIsShowing = false;
    }

    public void setPlayingPosition(int position) {
        this.mPlayingPosition = position;
    }

    public int getPlayingPosition() {
        return mPlayingPosition;
    }

    public void pause() {
        if (mIsShowing) return;
        mVideoView.pause();
    }

    public void resume() {
        if (mIsShowing) return;
        mVideoView.resume();
    }

    public void reset() {
        if (mIsShowing) return;
        Utils.removeViewFormParent(mVideoView);
        mVideoView.release();
        mVideoView.setVideoController(null);
        mPlayingPosition = -1;
        mActClass = null;
    }

    public boolean onBackPress() {
        return !mIsShowing && mVideoView.onBackPressed();
    }

    public boolean isStartFloatWindow() {
        return mIsShowing;
    }

    /**
     * 显示悬浮窗
     */
    public void setFloatViewVisible() {
        if (mIsShowing) {
            mVideoView.resume();
            mFloatView.setVisibility(View.VISIBLE);
        }
    }

    public void setActClass(Class cls) {
        this.mActClass = cls;
    }

    public Class getActClass() {
        return mActClass;
    }

}
