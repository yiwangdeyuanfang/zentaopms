package com.buglife.sdk;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.langlib.utils.LogUtil;
import com.langlib.utils.ScreenUtils;

/**
 * @author zhangyueli
 * @date 2019/4/25 下午4:54
 */

public class LoginDialog extends Dialog {

    private Context mContext;
    private EditText mUserName;
    private EditText mUserPw;
    private TextView mCancelBtn;
    private TextView mLoginBtn;

    private OnLoginClickListener mLoginListener;

    public LoginDialog(Context context) {
        super(context, R.style.CustomDialog);
        mContext = context;
    }

    public LoginDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager wManager = getWindow().getWindowManager();
        int width = (int) (wManager.getDefaultDisplay().getWidth() * 0.85);//
        getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_login);

        mUserName = findViewById(R.id.user_name);
        mUserPw = findViewById(R.id.user_password);
        mCancelBtn = findViewById(R.id.dialog_cancel_button);
        mLoginBtn = findViewById(R.id.dialog_login_button);

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoginListener != null)
                    if (TextUtils.isEmpty(mUserName.getText().toString()) || TextUtils
                            .isEmpty(mUserPw.getText().toString())) {
                        Toast.makeText(mContext,"用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        mLoginListener.onLoginClick(mUserName.getText().toString(), mUserPw.getText().toString());
                        dismiss();
                    }
            }
        });
    }

    public void showDialog() {
        // this.setCanceledOnTouchOutside(true);
        this.show();
        WindowManager windowManager = getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (int) (ScreenUtils.getScreenWidthPx(mContext) * 0.85); // 设置宽度
        // lp.height = (int) (470 * 1.04);
        this.getWindow().setAttributes(lp);
    }

    public void setLoginClickListener(OnLoginClickListener listener) {
        mLoginListener = listener;
    }

    public interface OnLoginClickListener {
        void onLoginClick(String userName, String password);
    }
}
