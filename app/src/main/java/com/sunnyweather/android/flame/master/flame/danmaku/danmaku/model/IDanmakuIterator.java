package com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model;

import com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.BaseDanmaku;

public interface IDanmakuIterator {

    public BaseDanmaku next();
    
    public boolean hasNext();
    
    public void reset();

    public void remove();
    
}
