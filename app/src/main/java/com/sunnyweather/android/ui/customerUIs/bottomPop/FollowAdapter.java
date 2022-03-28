package com.sunnyweather.android.ui.customerUIs.bottomPop;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.sunnyweather.android.R;
import com.sunnyweather.android.SunnyWeatherApplication;
import com.sunnyweather.android.logic.model.RoomInfo;
import com.sunnyweather.android.ui.liveRoom.LiveRoomActivity;

import org.jetbrains.annotations.NotNull;

public class FollowAdapter extends BaseQuickAdapter<RoomInfo, BaseViewHolder> {
    public FollowAdapter() {
        super(R.layout.owner_item);
    }

    /**
     * 在此方法中设置item数据
     */
    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull RoomInfo item) {
        Glide.with(getContext()).load(item.getOwnerHeadPic()).transition(DrawableTransitionOptions.withCrossFade()).into((ImageView) helper.getView(R.id.profileImageIv));
        helper.setText(R.id.platform_search, SunnyWeatherApplication.Companion.platformName(item.getPlatForm()) + "·");
        helper.setText(R.id.usernameTv, item.getOwnerName());
        helper.setText(R.id.fullNameTv, item.getRoomName());
        helper.getView(R.id.contentContainerRl).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LiveRoomActivity.class);
            intent.putExtra("platform", item.getPlatForm());
            intent.putExtra("roomId", item.getRoomId());
            ((Activity)getContext()).startActivity(intent);
        });
    }
}
