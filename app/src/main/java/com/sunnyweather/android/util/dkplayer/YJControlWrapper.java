package com.sunnyweather.android.util.dkplayer;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ScreenUtils;

import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IVideoController;
import xyz.doikki.videoplayer.controller.MediaPlayerControl;

/**
 * 此类的目的是为了在ControlComponent中既能调用VideoView的api又能调用BaseVideoController的api，
 * 并对部分api做了封装，方便使用
 */
public class YJControlWrapper extends ControlWrapper {
    private Boolean isLandscape = ScreenUtils.isLandscape();

    public YJControlWrapper(@NonNull MediaPlayerControl playerControl, @NonNull IVideoController controller) {
        super(playerControl, controller);
    }

    @Override
    public void toggleFullScreen(Activity activity) {
        if (activity == null || activity.isFinishing())
            return;
        if (isFullScreen()) {
            if (!isLandscape) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            stopFullScreen();
        } else {
            isLandscape = ScreenUtils.isLandscape();
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            startFullScreen();
        }
    }
}
