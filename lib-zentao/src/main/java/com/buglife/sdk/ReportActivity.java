/*
 * Copyright (C) 2017 Buglife, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.buglife.sdk;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.buglife.sdk.model.*;
import com.buglife.sdk.reporting.ReportSubmissionCallback;
import com.buglife.sdk.view.IReportPresenterView;
import com.buglife.sdk.view.ReportPresenter;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;
import static com.buglife.sdk.ActivityUtils.INTENT_KEY_ATTACHMENT;
import static com.buglife.sdk.ActivityUtils.INTENT_KEY_BUG_CONTEXT;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener, IReportPresenterView {

    private static final int SEND_MENU_ITEM = 1;
    private static final int REQUEST_IMAGE = 2;

    private BugContext mBugContext;
    private AttachmentAdapter mAttachmentAdapter;
    private RecyclerView mAttachmentListView;
    private ImageView mAddIv; //添加图片按钮
    private @NonNull
    List<InputField> mInputFields;
    private @Nullable
    ProgressDialog mProgressDialog;
    private @NonNull
    ColorPalette mColorPalette;


    private ZentaoConfigData mZentaoConfigData;
    //所有用户
    private AllUserData mAllUserData;

    private List<ProjectsData> mProjectsData;

    private PickerInputField mAssignedToField; //可分配的人

    private PickerInputField mBugTypeField;//Bug类型

    private PickerInputField mSeverityField;//bug等级

    private PickerInputField mPriField;//优先级


    private ReportPresenter mReportPresenter;

    public static Intent newStartIntent(Context context, BugContext bugContext) {
        Intent intent = new Intent(context, ReportActivity.class);
        intent.setFlags(intent.getFlags() | FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(INTENT_KEY_BUG_CONTEXT, bugContext);
        return intent;
    }

    public ReportActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initModelData();

        mAttachmentListView = findViewById(R.id.attachment_list_view);
        mAddIv =  findViewById(R.id.add_iv);
        mAddIv.setOnClickListener(this);

        Intent intent = getIntent();
        intent.setExtrasClassLoader(BugContext.class.getClassLoader());
        mBugContext = intent.getParcelableExtra(INTENT_KEY_BUG_CONTEXT);

        final List<FileAttachment> mediaAttachments = mBugContext.getMediaAttachments();

        mAttachmentAdapter = new AttachmentAdapter(this,mediaAttachments);
        mAttachmentAdapter.setItemListener(new AttachmentAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pistion,FileAttachment attachment) {
                showActivityForAttachment(attachment);
            }
        });
        mAttachmentListView.setLayoutManager(new LinearLayoutManager(this));
        mAttachmentListView.setAdapter(mAttachmentAdapter);


        mColorPalette = new ColorPalette.Builder(this).build();
        int colorPrimary = mColorPalette.getColorPrimary();
        int titleTextColor = mColorPalette.getTextColorPrimary();
        String titleTextColorHex = ColorPalette.getHexColor(titleTextColor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor(titleTextColorHex));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            Drawable drawable = ActivityUtils.getTintedDrawable(this, android.R.drawable.ic_menu_close_clear_cancel,
                    mColorPalette.getTextColorPrimary());

            actionBar.setHomeAsUpIndicator(drawable);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(colorPrimary));
            actionBar.setTitle(getString(R.string.report_a_bug));
        }

        ActivityUtils.setStatusBarColor(this, mColorPalette.getColorPrimaryDark());


        mReportPresenter = new ReportPresenter(this,this,Buglife.getProductId());

        mReportPresenter.getZentaoConfig(this);
//        mReportPresenter.logoutZentao(this);
    }

    public void initModelData() {
        mAllUserData = new AllUserData();
        mProjectsData = new ArrayList<>();
    }

    public void initContentItem() {

        //问题描述
        TextInputField titleTextIput = new TextInputField(ZentaoConstant.BUG_TITLE, false);
        titleTextIput.setTitle(getString(R.string.summary_field_title));
        titleTextIput.setMultiline(true);

        //重现步骤
        TextInputField stepTextInput = new TextInputField(ZentaoConstant.BUG_STEPS, false);
        stepTextInput.setTitle(getString(R.string.setp_field_title));
        stepTextInput.setMultiline(true);

        //可分配的人
        mAssignedToField = new PickerInputField(ZentaoConstant.BUG_ASSIGNEDTO);
        mAssignedToField.setTitle("指派给");
        for (AllUserItemData itemData : mAllUserData.getUserItemData()) {
            if (!TextUtils.isEmpty(itemData.getValue())) {
                mAssignedToField.addOption(itemData.getName(), itemData.getValue());
            }
        }

        //Bug类型
        mBugTypeField = new PickerInputField(ZentaoConstant.BUG_TYPE);
        mBugTypeField.setTitle("Bug类型");
        for (BugTypeData bugTypeData : ZentaoConstant.bugTypeList()) {
            mBugTypeField.addOption(bugTypeData.getDes(), bugTypeData.getBugCode());
        }

        //bug等级
        mSeverityField = new PickerInputField(ZentaoConstant.BUG_SEVERITY);
        mSeverityField.setTitle("严重程度");
        for (String severity : ZentaoConstant.SEVERITY) {
            mSeverityField.addOption(severity);
        }

        //优先级
        mPriField = new PickerInputField(ZentaoConstant.BUG_PRI);
        mPriField.setTitle("优先级");
        for (String severity : ZentaoConstant.PRI_TYPE) {
            mPriField.addOption(severity);
        }

        //版本
        PickerInputField projectsField = new PickerInputField(ZentaoConstant.BUG_PROJECTS);
        projectsField.setTitle("版本号");
        for (ProjectsData data : mProjectsData) {
            if (!TextUtils.isEmpty(data.getProjectsId())) {
                projectsField.addOption(data.getProjectsDes(), data.getProjectsId());
            }
        }

        Buglife.setInputFields(titleTextIput, stepTextInput, mAssignedToField, mSeverityField, mBugTypeField, mPriField,
                projectsField);
    }

    public void initContentItemRes() {
        mInputFields = Buglife.getInputFields();
        ArrayList<InputFieldView> inputFieldViews = new ArrayList<>();

        LinearLayout inputFieldLayout = (LinearLayout) findViewById(R.id.input_field_layout);

        for (final InputField inputField : mInputFields) {
            final InputFieldView inputFieldView = InputFieldView.newInstance(this, inputField);
            final String currentValue = getValueForInputField(inputField);

            inputFieldView.configureWithInputField(inputField, new InputFieldView.ValueCoordinator() {
                @Override
                public void onValueChanged(@NonNull InputField inputField, @Nullable String newValue) {
                    setValueForInputField(inputField, newValue);
                }
            });

            inputFieldLayout.addView(inputFieldView);
            inputFieldViews.add(inputFieldView);
            inputFieldView.setValue(currentValue);
        }
    }

    private @Nullable
    String getValueForInputField(@NonNull InputField inputField) {
        String attributeName = inputField.getAttributeName();
        Attribute attribute = mBugContext.getAttribute(attributeName);
        return attribute == null ? null : attribute.getValue();
    }

    void setValueForInputField(@NonNull InputField inputField, @Nullable String value) {
        String attributeName = inputField.getAttributeName();
        mBugContext.putAttribute(attributeName, value);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem sendItem = menu.add(0, SEND_MENU_ITEM, Menu.NONE, R.string.send);
        sendItem.setShowAsAction(SHOW_AS_ACTION_ALWAYS);
        Drawable drawable = ActivityUtils
                .getTintedDrawable(this, android.R.drawable.ic_menu_send, mColorPalette.getTextColorPrimary());
        sendItem.setIcon(drawable);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case SEND_MENU_ITEM:
                submitReport();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showActivityForAttachment(FileAttachment attachment) {
        if (attachment.isImage()) {
            showScreenshotAnnotatorActivity(attachment);
        } else if (attachment.isVideo()) {
            showVideoActivity(attachment);
        }
    }

    private void showScreenshotAnnotatorActivity(FileAttachment attachment) {
        Intent intent = ScreenshotAnnotatorActivity.newStartIntent(this, attachment);
        startActivityForResult(intent, ScreenshotAnnotatorActivity.REQUEST_CODE);
    }

    private void showVideoActivity(FileAttachment attachment) {
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra(INTENT_KEY_ATTACHMENT, attachment);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ScreenshotAnnotatorActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mAttachmentAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                for (String filePath : path) {
                    File file = new File(filePath);
                    mBugContext.addAttachment(new FileAttachment(file, MimeTypes.PNG));
                }
                final List<FileAttachment> mediaAttachments = mBugContext.getMediaAttachments();
                mAttachmentAdapter.setAttachments(mediaAttachments);
            }
        }
    }

    public void sendButtonPressed(MenuItem item) {
        submitReport();
    }

    private void dismiss() {
        onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        Buglife.onFinishReportFlow();
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private void submitReport() {
        Report report = new Report(mBugContext);

        if (Buglife.getRetryPolicy() == RetryPolicy.MANUAL) {
            showProgressDialog();
        }

        Buglife.submitReport(report, new ReportSubmissionCallback() {
            @Override
            public void onSuccess() {
                dismissProgressDialog();
                Toast.makeText(ReportActivity.this, R.string.thanks_for_filing_a_bug, Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onFailure(Error error, Throwable throwable) {
                dismissProgressDialog();
                throwable.printStackTrace();

                switch (error) {
                    case NETWORK:
                        showErrorDialog(getString(R.string.error_dialog_message));
                        break;
                    case SERIALIZATION:
                        showErrorDialog(getString(R.string.error_dialog_message_check_logs));
                        break;
                }
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, getString(R.string.sending_toast), "");
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void showErrorDialog(String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.buglife_alert_dialog).create();
        alertDialog.setTitle(R.string.error_dialog_title);
        alertDialog.setMessage(getString(R.string.error_dialog_message));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog
                .setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

        alertDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch (requestCode) {
            //就像onActivityResult一样这个地方就是判断你是从哪来的。
            case REQUEST_IMAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openMultiImageSelector();
                } else {
                    Toast.makeText(ReportActivity.this, "很遗憾你没有权限。", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View view) {

//        int i = view.getId();
//        if (i == R.id.add_iv) {
//            if (Build.VERSION.SDK_INT >= 23) {
//                setPermission();
//            }else {
                openMultiImageSelector();
//            }

//        }
    }

    public void showLoginDialog(final String sessionId){
        final LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.showDialog();
        loginDialog.setLoginClickListener(new LoginDialog.OnLoginClickListener() {
            @Override
            public void onLoginClick(String userName, String password) {
                mReportPresenter.loginZentao(userName,password,sessionId);
            }
        });
    }

    public void openMultiImageSelector() {
        ArrayList<String> defaultDataArray = new ArrayList<>();
        Intent intent = new Intent(this, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, defaultDataArray);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    public void setPermission() {
        String[] mPermissionList = new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_APN_SETTINGS };
        ActivityCompat.requestPermissions(this, mPermissionList, REQUEST_IMAGE);

    }

    @Override
    public void setZentaoConfig(final ZentaoConfigData zentaoConfig) {

        showLoginDialog(zentaoConfig.getSessionID());

    }

    @Override
    public void loginFail(String sessionId) {

        showLoginDialog(sessionId);
    }

    @Override
    public void setBugAllInfo(BugInfo accountInfo) {
        Map<String, String> products = accountInfo.getProjects(); //对动态的key，来创建map，间接从中取出实体类futrue。
        for (String key : products.keySet()) {                        //遍历取出key，再遍历map取出value。

            ProjectsData projectsData = new ProjectsData();
            projectsData.setProjectsId(key);
            projectsData.setProjectsDes(products.get(key));
            mProjectsData.add(projectsData);
        }

        Map<String, String> account = accountInfo.getUsers(); //对动态的key，来创建map，间接从中取出实体类futrue。
        for (String key : account.keySet()) {                        //遍历取出key，再遍历map取出value。

            AllUserItemData userItemData = new AllUserItemData();
            userItemData.setValue(key);
            userItemData.setName(account.get(key));
            mAllUserData.add(userItemData);
        }

        //初始化界面
        initContentItem();

        initContentItemRes();
    }

    @Override
    public void setAllUserData(AllUserData allUserData) {
        mAllUserData =  allUserData;

//        initContentItem();
//
//        initContentItemRes();;
    }
}

