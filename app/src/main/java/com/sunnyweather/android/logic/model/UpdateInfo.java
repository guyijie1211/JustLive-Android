package com.sunnyweather.android.logic.model;


import java.util.ArrayList;

public class UpdateInfo{
    private int versionNum;//版本号
    private String latestVersion;//最新版本号
    private String updateUrl;//apk下载地址
    private String apkSize;//apk大小
    private String apkMD5;//更新包md5
    private int importance;//更新重要性 0:小修复,1:新功能,2:大版本
    private ArrayList<String> description;//更新描述

    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public String getApkSize() {
        return apkSize;
    }

    public void setApkSize(String apkSize) {
        this.apkSize = apkSize;
    }

    public String getApkMD5() {
        return apkMD5;
    }

    public void setApkMD5(String apkMD5) {
        this.apkMD5 = apkMD5;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }
}
