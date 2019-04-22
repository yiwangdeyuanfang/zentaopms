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



        params.put("title", mBugContext.getAttribute(ZentaoConstant.BUG_TITLE).getValue());
        params.put("assignedTo", mBugContext.getAttribute(ZentaoConstant.BUG_ASSIGNEDTO).getValue());
        params.put("openedBuild", "trunk");
        params.put("product", "1");
        params.put("module", "1");
        params.put("os", "Android");
        params.put("type", mBugContext.getAttribute(ZentaoConstant.BUG_TYPE).getValue());
        params.put("severity", mBugContext.getAttribute(ZentaoConstant.BUG_SEVERITY).getValue());
        params.put("steps", mBugContext.getAttribute(ZentaoConstant.BUG_STEPS).getValue());


        // Attachments 截图
        JSONArray attachmentsParams = new JSONArray();

        for (FileAttachment attachment : mBugContext.getAttachments()) {
            attachmentsParams.put(attachment.getFile());
            attachment.getFile().delete();
        }

        if (attachmentsParams.length() > 0) {
            params.put("files", attachmentsParams);
        }

        return params;
    }


    public RequestBody getRequestBody() {
        RequestBody requestBody =
                new FormBody.Builder()
                        .add("title", mBugContext.getAttribute(ZentaoConstant.BUG_TITLE).getValue())
                        .add("assignedTo", mBugContext.getAttribute(ZentaoConstant.BUG_ASSIGNEDTO).getValue())
                        .add("openedBuild", "trunk")
                        .add("product", mBugContext.getAttribute(ZentaoConstant.BUG_PRODUCT).getValue())
                        .add("module", mBugContext.getAttribute(ZentaoConstant.BUG_MODULE).getValue())
                        .add("type", mBugContext.getAttribute(ZentaoConstant.BUG_TYPE).getValue())
                        .add("severity", mBugContext.getAttribute(ZentaoConstant.BUG_SEVERITY).getValue())
                        .add("steps", mBugContext.getAttribute(ZentaoConstant.BUG_TITLE).getValue()).build();
        return requestBody;
    }
}
