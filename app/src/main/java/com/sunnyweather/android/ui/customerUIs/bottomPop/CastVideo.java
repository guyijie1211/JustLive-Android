package com.sunnyweather.android.ui.customerUIs.bottomPop;

import androidx.annotation.NonNull;

import com.android.cast.dlna.core.ICast;

public class CastVideo implements ICast {

    public final String url;

    public final String id;

    public final String name;

    private long duration;

    public CastVideo(String url, String id, String name) {
        this.url = url;
        this.id = id;
        this.name = name;
    }
    @NonNull
    @Override
    public String getId() {
        return id;
    }

    @NonNull
    @Override
    public String getUri() {
        return url;
    }

    @Override
    public String getName() {
        return name;
    }
}
