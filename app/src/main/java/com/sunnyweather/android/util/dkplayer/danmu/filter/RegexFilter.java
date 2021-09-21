package com.sunnyweather.android.util.dkplayer.danmu.filter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import master.flame.danmaku.controller.DanmakuFilters;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;

/**
 * Created by xyoye on 2021/2/25.
 */

public class RegexFilter extends DanmakuFilters.BaseDanmakuFilter<List<String>> {
    private static final int FILTER_TYPE_REGEX = 2048;
    public final List<String> mRegexList = new ArrayList<>();

    @Override
    public boolean filter(BaseDanmaku danmaku, int index, int totalsizeInScreen, DanmakuTimer timer, boolean fromCachingTask, DanmakuContext config) {
        boolean filtered = false;
        for (int i = 0; i < mRegexList.size(); i++) {
            String regex = mRegexList.get(i);

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(danmaku.text);
            if (matcher.find()) {
                Log.d("RegexFilter", danmaku.text.toString());
                filtered = true;
                break;
            }
        }
        if (filtered) {
            danmaku.mFilterParam |= FILTER_TYPE_REGEX;
        }
        return filtered;
    }

    @Override
    public void setData(List<String> data) {
        reset();
        if (data != null) {
            for (String i : data) {
                addRegex(i);
            }
        }
    }

    @Override
    public void reset() {
        mRegexList.clear();
    }

    public void addRegex(String regex) {
        if (!mRegexList.contains(regex)) {
            mRegexList.add(regex);
        }
    }

    public void removeRegex(String regex) {
        mRegexList.remove(regex);
    }
}
