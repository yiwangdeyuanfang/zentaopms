package com.buglife.sdk.model;

import java.util.List;

/**
 * @author zhangyueli
 * @date 2019/4/18
 * Bug类型
 */
public class BugTypeData {

    public String bugCode; //英文代码
    private String des; // 汉字描述

    public BugTypeData(String bugCode, String des){
        this.bugCode = bugCode;
        this.des = des;
    }

    public String getBugCode() {
        return bugCode;
    }

    public void setBugCode(String bugCode) {
        this.bugCode = bugCode;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
