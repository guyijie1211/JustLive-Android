package com.sunnyweather.android.ui.customerUIs.bottomPop;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.cast.dlna.dmc.DLNACastManager;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.sunnyweather.android.R;
import com.sunnyweather.android.ui.liveRoom.LiveRoomActivity;

import org.fourthline.cling.model.meta.Device;

public class TVBottomPop extends BottomPopupView {
    RecyclerView recyclerView;
    TVsAdapter adapter;

    public TVBottomPop(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.tv_bottom_custom;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        DLNACastManager.getInstance().bindCastService(getContext());
        recyclerView=findViewById(R.id.tv_bottom_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new TVsAdapter();
        // 设置点击事件
        adapter.setOnItemClickListener((adapter, view, position) -> {
            ToastUtils.showShort("开始投屏");
            ((LiveRoomActivity)getContext()).pause();
            DLNACastManager.getInstance().cast((Device<?, ?, ?>) adapter.getData().get(position),
                    new CastVideo(((LiveRoomActivity)getContext()).getUrl(), "101", "live"));
        });
        recyclerView.setAdapter(adapter);
        findViewById(R.id.tv_bottom_search).setOnClickListener(v -> {
            DLNACastManager.getInstance().search(null, 60);
            ToastUtils.showShort("搜索中...");
        });
        ToastUtils.showShort("搜索中...");
        DLNACastManager.getInstance().registerDeviceListener(adapter);
    }

}
