package com.langlib.zentaopms;

import android.app.Application;
import com.buglife.sdk.Buglife;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Buglife.initWithEmail(this, "zhangyueli@langlib.com");
    }
}
