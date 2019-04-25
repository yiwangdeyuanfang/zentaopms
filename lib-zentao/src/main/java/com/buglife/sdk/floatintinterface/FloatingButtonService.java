package com.buglife.sdk.floatintinterface;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import com.buglife.sdk.Buglife;
import com.buglife.sdk.FileAttachment;
import com.buglife.sdk.R;
import com.buglife.sdk.screenrecorder.ScreenRecordButton;

import static android.graphics.PixelFormat.TRANSLUCENT;

/**
 * @author zhangyueli
 * @date  2019/4/23 下午1:54
 * 悬浮窗
*/

public class FloatingButtonService {
    public static boolean isStarted = false;

    private final @NonNull Context mContext;

    private @Nullable FloatingButton mFloatingButton;
    private final @NonNull WindowManager mWindowManager;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;


    public FloatingButtonService(Context context){
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }



    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }


    public void start() {
        showOverlay();
    }

    private void showOverlay() {
        mFloatingButton = new FloatingButton(mContext);
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
//                stopRecording();
            }
        });

        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                TRANSLUCENT);

        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;

        mWindowManager.addView(mFloatingButton, layoutParams);
    }
}
