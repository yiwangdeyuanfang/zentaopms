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

import android.location.Location;

import android.text.TextUtils;
import com.buglife.sdk.reporting.DeviceSnapshot;
import com.buglife.sdk.reporting.EnvironmentSnapshot;
import com.buglife.sdk.reporting.SessionSnapshot;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Attr;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a bug report draft.
 */
public final class Report {
    private final BugContext mBugContext;

    Report(BugContext bugContext) {
        mBugContext = bugContext;
    }

    public JSONObject toJSON() throws JSONException, IOException {
        JSONObject params = new JSONObject();

        Attribute summaryAttribute = mBugContext.getAttribute(ZentaoConstant.BUG_TITLE);

        if(summaryAttribute != null && !TextUtils.isEmpty(summaryAttribute.getValue())) {
            params.put("title", summaryAttribute.getValue());
        }else {
            params.put("title","问题描述");
        }

        Attribute assignedToAttribute = mBugContext.getAttribute(ZentaoConstant.BUG_ASSIGNEDTO);
        if(assignedToAttribute != null){
            params.put("assignedTo", assignedToAttribute.getValue());
        }

        Attribute openedBuildAttribute = mBugContext.getAttribute(ZentaoConstant.BUG_ASSIGNEDTO);
        if(openedBuildAttribute != null){
            params.put("openedBuild", "trunk");
        }else {
            params.put("openedBuild", "trunk");
        }

        params.put("product", "1");
        params.put("module", "1");
        params.put("os", "Android");


        Attribute typeAttribute = mBugContext.getAttribute(ZentaoConstant.BUG_TYPE);
        if(typeAttribute != null){
            params.put("type", typeAttribute.getValue());
        }else {
            params.put("type", "codeerror");
        }

        Attribute severityAttribute = mBugContext.getAttribute(ZentaoConstant.BUG_SEVERITY);
        if(severityAttribute != null){
            params.put("severity", severityAttribute.getValue());
        }else {
            params.put("severity", "4");
        }

        Attribute stepsAttribute = mBugContext.getAttribute(ZentaoConstant.BUG_STEPS);
        if(stepsAttribute != null){
            params.put("steps", stepsAttribute.getValue());
        }else {
            params.put("steps", "重现步骤");
        }


        // Attachments 截图
        JSONArray attachmentsParams = new JSONArray();

        for (FileAttachment attachment : mBugContext.getAttachments()) {
            attachmentsParams.put(attachment.getFile());
//            attachment.getFile().delete();
        }

        if (attachmentsParams.length() > 0) {
            params.put("files", attachmentsParams);
        }

        return params;
    }
}
