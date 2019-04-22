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



//        params.put("title", mBugContext.getAttribute());
//        params.put("app", appParams);



        return params;
    }
}
