package com.langlib.zentaopms;

import android.app.Application;
import com.buglife.sdk.Buglife;
import com.buglife.sdk.InvocationMethod;
import com.buglife.sdk.TextInputField;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Buglife.initWithEmail(this, "zhangyueli@langlib.com");
        Buglife.setInvocationMethod(InvocationMethod.SCREENSHOT);
        Buglife.setInvocationMethod(InvocationMethod.SHAKE);
//        Buglife.setInvocationMethod(InvocationMethod.valueOf(3));
//        Buglife.setInvocationMethod(InvocationMethod.valueOf(7));
    }
}
