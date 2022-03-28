package com.sunnyweather.android.logic.model;

import com.drake.brv.annotaion.ItemOrientation;
import com.drake.brv.item.ItemDrag;
import com.drake.brv.item.ItemSwipe;

public class AreaFollow implements ItemDrag, ItemSwipe {
    private String areaName;
    private String areaType;

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    @Override
    public int getItemOrientationDrag() {
        return ItemOrientation.VERTICAL;
    }

    @Override
    public void setItemOrientationDrag(int i) {

    }

    @Override
    public int getItemOrientationSwipe() {
        if (areaName.equals("全部推荐")) {
            return ItemOrientation.NONE;
        } else {
            return ItemOrientation.HORIZONTAL;
        }
    }

    @Override
    public void setItemOrientationSwipe(int i) {

    }
}
