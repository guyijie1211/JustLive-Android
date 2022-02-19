package com.sunnyweather.android.util.dkplayer;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.doikki.videoplayer.player.AbstractPlayer;
import xyz.doikki.videoplayer.player.VideoView;

/**
 * 播放器
 * Created by Doikki on 2017/4/7.
 */

public class YJVideoView<P extends AbstractPlayer> extends VideoView {

    public YJVideoView(@NonNull Context context) {
        super(context);
    }

    public YJVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public YJVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
