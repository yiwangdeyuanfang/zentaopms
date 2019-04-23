package com.buglife.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author zhangyueli
 * @date 2019/4/18
 * Bug类型
 */
public class ZentaoConfigData implements Parcelable {

    private String version;
    private String requestType;
    private String requestFix;
    private String moduleVar;
    private String methodVar;
    private String viewVar;
    private String sessionVar;
    private String sessionName;
    private String sessionID;
    private String random;
    private String expiredTime;
    private String serverTime;
    private String rand;

    protected ZentaoConfigData(Parcel in) {
        version = in.readString();
        requestType = in.readString();
        requestFix = in.readString();
        moduleVar = in.readString();
        methodVar = in.readString();
        viewVar = in.readString();
        sessionVar = in.readString();
        sessionName = in.readString();
        sessionID = in.readString();
        random = in.readString();
        expiredTime = in.readString();
        serverTime = in.readString();
        rand = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(version);
        dest.writeString(requestType);
        dest.writeString(requestFix);
        dest.writeString(moduleVar);
        dest.writeString(methodVar);
        dest.writeString(viewVar);
        dest.writeString(sessionVar);
        dest.writeString(sessionName);
        dest.writeString(sessionID);
        dest.writeString(random);
        dest.writeString(expiredTime);
        dest.writeString(serverTime);
        dest.writeString(rand);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ZentaoConfigData> CREATOR = new Creator<ZentaoConfigData>() {
        @Override
        public ZentaoConfigData createFromParcel(Parcel in) {
            return new ZentaoConfigData(in);
        }

        @Override
        public ZentaoConfigData[] newArray(int size) {
            return new ZentaoConfigData[size];
        }
    };

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestFix() {
        return requestFix;
    }

    public void setRequestFix(String requestFix) {
        this.requestFix = requestFix;
    }

    public String getModuleVar() {
        return moduleVar;
    }

    public void setModuleVar(String moduleVar) {
        this.moduleVar = moduleVar;
    }

    public String getMethodVar() {
        return methodVar;
    }

    public void setMethodVar(String methodVar) {
        this.methodVar = methodVar;
    }

    public String getViewVar() {
        return viewVar;
    }

    public void setViewVar(String viewVar) {
        this.viewVar = viewVar;
    }

    public String getSessionVar() {
        return sessionVar;
    }

    public void setSessionVar(String sessionVar) {
        this.sessionVar = sessionVar;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public String getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(String expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getRand() {
        return rand;
    }

    public void setRand(String rand) {
        this.rand = rand;
    }
}
