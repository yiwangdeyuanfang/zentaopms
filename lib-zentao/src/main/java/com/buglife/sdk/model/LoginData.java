package com.buglife.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author zhangyueli
 * @date 2019/4/18
 * Bug类型
 */
public class LoginData {

    private String account;
    private String password;
    private String zentaosid;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getZentaosid() {
        return zentaosid;
    }

    public void setZentaosid(String zentaosid) {
        this.zentaosid = zentaosid;
    }
}
