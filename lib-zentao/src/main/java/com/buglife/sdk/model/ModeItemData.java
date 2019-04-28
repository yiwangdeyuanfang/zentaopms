package com.buglife.sdk.model;

/**
 * @author zhangyueli
 * @date 2019/4/18
 * projects信息、builds信息、module信息
 */
public class ModeItemData {

    public String  id; //projects ID
    private String descripte; // 汉字描述

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripte() {
        return descripte;
    }

    public void setDescripte(String descripte) {
        this.descripte = descripte;
    }
}
