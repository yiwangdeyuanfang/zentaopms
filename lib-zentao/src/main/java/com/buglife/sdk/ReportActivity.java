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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.buglife.sdk.model.*;
import com.buglife.sdk.reporting.CreateBugData;
import com.buglife.sdk.reporting.ReportSubmissionCallback;
import com.google.gson.Gson;
import com.langlib.net.HttpCallback;
import com.langlib.net.HttpTaskUtil;
import com.langlib.utils.LogUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;
import static com.buglife.sdk.ActivityUtils.INTENT_KEY_ATTACHMENT;
import static com.buglife.sdk.ActivityUtils.INTENT_KEY_BUG_CONTEXT;

public class ReportActivity extends AppCompatActivity {

    private static final int SEND_MENU_ITEM = 1;

    private BugContext mBugContext;
    private AttachmentAdapter mAttachmentAdapter;
    private ListView mAttachmentListView;
    private @NonNull List<InputField> mInputFields;
    private @Nullable ProgressDialog mProgressDialog;
    private @NonNull ColorPalette mColorPalette;


    //所有用户
    private AllUserData mAllUserData;

    private AccountInfo mAccountInfo;
    private List<ProjectsData> mProjectsData;

    private PickerInputField mAssignedToField; //可分配的人

    private PickerInputField mBugTypeField;//Bug类型

    private PickerInputField mSeverityField ;//bug等级

    private PickerInputField mPriField;//优先级

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

        mAttachmentListView = (ListView) findViewById(R.id.attachment_list_view);

        Intent intent = getIntent();
        intent.setExtrasClassLoader(BugContext.class.getClassLoader());
        mBugContext = intent.getParcelableExtra(INTENT_KEY_BUG_CONTEXT);

        final List<FileAttachment> mediaAttachments = mBugContext.getMediaAttachments();

        mAttachmentAdapter = new AttachmentAdapter(mediaAttachments);
        mAttachmentListView.setAdapter(mAttachmentAdapter);
        mAttachmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileAttachment attachment = mAttachmentAdapter.getItem(position);
                showActivityForAttachment(attachment);
            }
        });

        mColorPalette = new ColorPalette.Builder(this).build();
        int colorPrimary = mColorPalette.getColorPrimary();
        int titleTextColor = mColorPalette.getTextColorPrimary();
        String titleTextColorHex = ColorPalette.getHexColor(titleTextColor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor(titleTextColorHex));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            Drawable drawable = ActivityUtils.getTintedDrawable(this, android.R.drawable.ic_menu_close_clear_cancel, mColorPalette.getTextColorPrimary());

            actionBar.setHomeAsUpIndicator(drawable);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(colorPrimary));
            actionBar.setTitle(getString(R.string.report_a_bug));
        }

        ActivityUtils.setStatusBarColor(this, mColorPalette.getColorPrimaryDark());

        getAccountInfo();
    }

    public void initModelData(){
        mAllUserData = new AllUserData();
        mProjectsData = new ArrayList<>();
    }

    public void initContentItem(){

        //问题描述
        TextInputField titleTextIput = new TextInputField(ZentaoConstant.BUG_TITLE,false);
        titleTextIput.setTitle(getString(R.string.summary_field_title));
        titleTextIput.setMultiline(true);

        //重现步骤
        TextInputField stepTextInput = new TextInputField(ZentaoConstant.BUG_STEPS,false);
        stepTextInput.setTitle(getString(R.string.setp_field_title));
        stepTextInput.setMultiline(true);

        //可分配的人
        mAssignedToField = new PickerInputField(ZentaoConstant.BUG_ASSIGNEDTO);
        mAssignedToField.setTitle("指派给");
        for(AllUserItemData itemData : mAllUserData.getUserItemData()) {
            if(!TextUtils.isEmpty(itemData.getValue())) {
                mAssignedToField.addOption(itemData.getName(), itemData.getValue());
            }
        }


        //Bug类型
        mBugTypeField = new PickerInputField(ZentaoConstant.BUG_TYPE);
        mBugTypeField.setTitle("Bug类型");
        for(BugTypeData bugTypeData : ZentaoConstant.bugTypeList()) {
            mBugTypeField.addOption(bugTypeData.getDes(),bugTypeData.getBugCode());
        }

        //bug等级
        mSeverityField = new PickerInputField(ZentaoConstant.BUG_SEVERITY);
        mSeverityField.setTitle("严重程度");
        for(String severity : ZentaoConstant.SEVERITY) {
            mSeverityField.addOption(severity);
        }

        //优先级
        mPriField = new PickerInputField(ZentaoConstant.BUG_PRI);
        mPriField.setTitle("优先级");
        for(String severity : ZentaoConstant.PRI_TYPE) {
            mPriField.addOption(severity);
        }

        //版本
        PickerInputField projectsField = new PickerInputField(ZentaoConstant.BUG_PROJECTS);
        projectsField.setTitle("版本号");
        for(ProjectsData data : mProjectsData) {
            if(!TextUtils.isEmpty(data.getProjectsId())) {
                projectsField.addOption(data.getProjectsDes(), data.getProjectsId());
            }
        }


        Buglife.setInputFields(titleTextIput,stepTextInput,mAssignedToField,mSeverityField,mBugTypeField,mPriField,projectsField);
    }

    public void initContentItemRes(){
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

    private @Nullable String getValueForInputField(@NonNull InputField inputField) {
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
        Drawable drawable = ActivityUtils.getTintedDrawable(this, android.R.drawable.ic_menu_send, mColorPalette.getTextColorPrimary());
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
//                getAllUser();
//                getAccountInfo();
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

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    //获取bug的所有信息
    private void getAccountInfo() {

        HttpTaskUtil.getTask().reqHttpPost(ZentaoConstant.ZENTAO_REPORT_URL, "" ,new HttpCallback<BaseBody>(){

            @Override
            public void onSuccess(BaseBody body) {
                BaseBody baseBody  = body;

                Gson gson = new Gson();
                mAccountInfo = gson.fromJson(body.getData(),AccountInfo.class);

                Map<String, String> products = mAccountInfo.getProjects(); //对动态的key，来创建map，间接从中取出实体类futrue。
                for (String key:products.keySet()){                        //遍历取出key，再遍历map取出value。

                    ProjectsData projectsData = new ProjectsData();
                    projectsData.setProjectsId(key);
                    projectsData.setProjectsDes(products.get(key));
                    mProjectsData.add(projectsData);
                }

                Map<String, String> account = mAccountInfo.getUsers(); //对动态的key，来创建map，间接从中取出实体类futrue。
                for (String key:account.keySet()){                        //遍历取出key，再遍历map取出value。

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
            public void onError(String errorMsg) {
                super.onError(errorMsg);
            }
        },BaseBody.class);

    }

    //获取所有用户
    public void getAllUser(){

        mAllUserData.getUserItemData().clear();

        HttpTaskUtil.getTask().reqHttpPost(ZentaoConstant.LOAD_ALL_USERS, "",new HttpCallback<String>(){

            @Override
            public void onSuccess(String htmlString) {
                Log.i("uploadData() getAllUser() s = " + htmlString);

                Document document = Jsoup.parse(htmlString);
                Elements elements = document.getElementsByTag("option");


                //遍历Elements,解析其标签
                for (Element title : elements) {
                    Log.d("title = " + title.text() + " " + title.val());

                    if(!TextUtils.isEmpty(title.val())) {
                        AllUserItemData itemData = new AllUserItemData();
                        itemData.setValue(title.val());
                        itemData.setName(title.text());
                        mAllUserData.add(itemData);
                    }
                }

                initContentItem();

                initContentItemRes();

            }
            @Override
            public void onError(String errorMsg) {
                Log.i("uploadData() onError() errorMsg = " + errorMsg);
            }
        },String.class);
    }
    //获取Bug严重程度列表
    public void getSeverityList(){


        HttpTaskUtil.getTask().reqHttpPost(ZentaoConstant.SEVERITY_LIST, "",new HttpCallback<Severity>(){

            @Override
            public void onSuccess(Severity severity) {
            }
            @Override
            public void onError(String errorMsg) {
                Log.i("uploadData() onError() errorMsg = " + errorMsg);
            }
        },Severity.class);
    }
}

