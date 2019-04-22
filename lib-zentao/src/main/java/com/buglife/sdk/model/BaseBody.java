package com.buglife.sdk.model;/**
 * @author zhangyueli
 * @date 2019/4/18
 */

/**
 * @author zhangyueli
 * @date 2019/4/18
 *
 */
public class BaseBody<T> {

    private String status;
    private T data;
    private String md5;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
