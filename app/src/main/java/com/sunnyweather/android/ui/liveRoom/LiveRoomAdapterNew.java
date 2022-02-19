package com.sunnyweather.android.ui.liveRoom;

import android.text.Html;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.sunnyweather.android.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LiveRoomAdapterNew extends BaseQuickAdapter<LiveRoomViewModel.DanmuInfo, BaseViewHolder> {

    /**
     * 构造方法，此示例中，在实例化Adapter时就传入了一个List。
     * 如果后期设置数据，不需要传入初始List，直接调用 super(layoutResId); 即可
     */
    public LiveRoomAdapterNew() {
        super(R.layout.danmu_item);
    }

    /**
     * 在此方法中设置item数据
     */
    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull LiveRoomViewModel.DanmuInfo item) {
        try {
            String result = "<b><font>" + item.getUserName() + "：</font></b>" + item.getContent();
            helper.setText(R.id.danMu_name, Html.fromHtml(result));
        } catch (Exception e) {

        }
    }
}
