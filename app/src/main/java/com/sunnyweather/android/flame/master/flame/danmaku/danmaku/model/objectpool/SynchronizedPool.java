/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.objectpool;

import com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.objectpool.Pool;
import com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.objectpool.Poolable;

class SynchronizedPool<T extends Poolable<T>> implements com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.objectpool.Pool<T> {
    private final com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.objectpool.Pool<T> mPool;

    private final Object mLock;

    public SynchronizedPool(com.sunnyweather.android.flame.master.flame.danmaku.danmaku.model.objectpool.Pool<T> pool) {
        mPool = pool;
        mLock = this;
    }

    public SynchronizedPool(Pool<T> pool, Object lock) {
        mPool = pool;
        mLock = lock;
    }

    public T acquire() {
        synchronized (mLock) {
            return mPool.acquire();
        }
    }

    public void release(T element) {
        synchronized (mLock) {
            mPool.release(element);
        }
    }
}
