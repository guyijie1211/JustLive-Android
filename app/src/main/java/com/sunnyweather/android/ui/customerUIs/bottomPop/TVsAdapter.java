package com.sunnyweather.android.ui.customerUIs.bottomPop;

import android.util.Log;

import com.android.cast.dlna.dmc.OnDeviceRegistryListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.sunnyweather.android.R;

import org.fourthline.cling.model.meta.Device;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TVsAdapter extends BaseQuickAdapter<Device<?, ?, ?>, BaseViewHolder> implements OnDeviceRegistryListener {

    /**
     * 构造方法，此示例中，在实例化Adapter时就传入了一个List。
     * 如果后期设置数据，不需要传入初始List，直接调用 super(layoutResId); 即可
     */
    public TVsAdapter() {
        super(R.layout.tv_bottom_item);
    }

    /**
     * 在此方法中设置item数据
     */
    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull Device<?, ?, ?> item) {
        helper.setText(R.id.tv_item_name, item.getDetails().getFriendlyName());
        helper.setText(R.id.tv_item_brand, item.getDetails().getManufacturerDetails().getManufacturer());
        helper.setText(R.id.tv_item_mac, item.getIdentity().getUdn().getIdentifierString());
    }

    @Override
    public void onDeviceAdded(Device<?, ?, ?> device) {
        Log.i("test", "找到1个设备");
        addData(device);
        Log.i("test","数据长度" + getData().size());
    }

    @Override
    public void onDeviceUpdated(Device<?, ?, ?> device) {

    }

    @Override
    public void onDeviceRemoved(Device<?, ?, ?> device) {
        Log.i("test", "删除1个设备");
        remove(device);
    }
}
