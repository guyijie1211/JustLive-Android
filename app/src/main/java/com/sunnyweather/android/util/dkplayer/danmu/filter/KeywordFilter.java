package com.sunnyweather.android.util.dkplayer.danmu.filter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import master.flame.danmaku.controller.DanmakuFilters;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;

/**
 * Created by xyoye on 2021/2/25.
 */

public class KeywordFilter extends DanmakuFilters.BaseDanmakuFilter<List<String>> {
    private static final int FILTER_TYPE_KEYWORD = 1024;
    private final List<String> mKeyWordList = new ArrayList<>();

    @Override
    public boolean filter(BaseDanmaku danmaku, int index, int totalsizeInScreen, DanmakuTimer timer, boolean fromCachingTask, DanmakuContext config) {
        boolean filtered = false;
        for (int i = 0; i < mKeyWordList.size(); i++) {
            String keyword = mKeyWordList.get(i);
            String danmakuText = danmaku.text.toString();
            if (danmakuText.contains(keyword)) {
                Log.e("KeywordFilter", danmakuText);
                filtered = true;
                danmaku.mFilterParam |= FILTER_TYPE_KEYWORD;
                break;
            }
        }
        return filtered;
    }

    @Override
    public void setData(List<String> data) {
        reset();
        if (data != null) {
            for (String i : data) {
                addKeyword(i);
            }
        }
    }

    @Override
    public void reset() {
        mKeyWordList.clear();
    }

    public void addKeyword(String keyword) {
        if (!mKeyWordList.contains(keyword)) {
            mKeyWordList.add(keyword);
        }
    }
    public void removeKeyword(String keyword) {
        mKeyWordList.remove(keyword);
    }
}
