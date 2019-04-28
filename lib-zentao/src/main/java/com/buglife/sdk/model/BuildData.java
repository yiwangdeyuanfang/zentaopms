package com.buglife.sdk.model;

/**
 * @author zhangyueli
 * @date 2019/4/18
 * 版本号信息
 */
public class BuildData {

    public String  buildId; //projects ID
    private String buildDes; // 汉字描述

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getBuildDes() {
        return buildDes;
    }

    public void setBuildDes(String buildDes) {
        this.buildDes = buildDes;
    }
}
