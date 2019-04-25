package com.buglife.sdk.model;

/**
 * @author zhangyueli
 * @date 2019/4/18
 * Bug类型
 */
public class AccountData {

    private String account;
    private String password;
    private boolean keepLogin;

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

    public boolean getKeepLogin() {
        return keepLogin;
    }

    public void setKeepLogin(boolean keepLogin) {
        this.keepLogin = keepLogin;
    }
}
