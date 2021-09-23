
package com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.android;

import com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.android.DrawingCache;
import com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.objectpool.PoolableManager;

public class DrawingCachePoolManager implements PoolableManager<com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.android.DrawingCache> {

    @Override
    public com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.android.DrawingCache newInstance() {
        return null;
    }

    @Override
    public void onAcquired(com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.android.DrawingCache element) {

    }

    @Override
    public void onReleased(DrawingCache element) {

    }

}
