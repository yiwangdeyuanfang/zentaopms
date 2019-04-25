package com.buglife.sdk.floatintinterface;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;

import static android.content.Context.MEDIA_PROJECTION_SERVICE;
/**
 * @author zhangyueli
 * @date  2019/4/23 下午3:29
 *
 * 悬浮按钮
*/
@TargetApi(Build.VERSION_CODES.M)
public final class FloatWindowPermissionHelper extends Fragment {

    public static final String TAG = "com.langlib.FloatWindowPermissionHelper";
    private static final int SCREEN_OVERLAY_REQUEST_CODE = 1234;
    private static final int SCREEN_RECORD_REQUEST_CODE = 12345;

    private PermissionCallback mPermissionCallback;

    public static FloatWindowPermissionHelper newInstance() {
        return new FloatWindowPermissionHelper();
    }

    public FloatWindowPermissionHelper() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure the fragment isn't destroyed & recreated on orientation changes
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        checkOverlayPermissions();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setPermissionCallback(PermissionCallback permissionCallback) {
        mPermissionCallback = permissionCallback;
    }

    private void checkOverlayPermissions() {
        // First make sure we can draw an overlay
//        if (!Settings.canDrawOverlays(getContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
            startActivityForResult(intent, SCREEN_OVERLAY_REQUEST_CODE);
//        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCREEN_OVERLAY_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
                mPermissionCallback.onPermissionGranted(resultCode, data);
//            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public enum PermissionType {
        OVERLAY
    }

    public interface PermissionCallback {
        void onPermissionGranted(int resultCode, Intent data);
        void onPermissionDenied(PermissionType permissionType);
    }
}
