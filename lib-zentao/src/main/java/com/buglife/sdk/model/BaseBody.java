package com.buglife.sdk.model;/**
 * @author zhangyueli
 * @date 2019/4/18
 */

/**
 * @author zhangyueli
 * @date 2019/4/18
 *
 */
public class BaseBody {

    private String status;
    private String data;
    private String md5;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
