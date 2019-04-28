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

package com.buglife.sdk.reporting;

import com.buglife.sdk.Buglife;
import com.buglife.sdk.Log;
import com.buglife.sdk.NetworkManager;

import com.buglife.sdk.ZentaoConstant;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

public final class SubmitReportTask {
    private final NetworkManager mNetworkManager;

    public SubmitReportTask() {
        mNetworkManager = NetworkManager.getInstance();
    }

    /**
     * Synchronously executes a POST request
     *
     * @param report a JSON payload with the report to submit
     * @return The result of the network request
     */
    public Result execute(JSONObject report) {

        Log.d("Report submitted successfully!" + report);
        final Request request = newRequest(report);

        try {
            final Response response = mNetworkManager.executeRequest(request);
            if (response.body() == null) {
                return new Result(new IllegalStateException("Response body was null!"));
            }

            String str = response.body().string();
            Log.d("Report submitted successfully!" + str);
            final JSONObject responseJSONObject = new JSONObject(str);
            Log.d("Report submitted successfully!");
            return new Result(responseJSONObject);
        } catch (Exception error) {
            Log.d("Error submitting report", error);
            return new Result(error);
        }
    }



    /**
     * Asynchronously executes a POST request
     *
     * @param report   a JSON payload with the report to submit
     * @param callback Calls back with the result of the request; this is called on the main thread
     */
    public void execute(JSONObject report, final ReportSubmissionCallback callback) {
        mNetworkManager.executeRequestAsync(newRequest(report), new Callback() {
            @Override
            public void onFailure(final Call call, final IOException error) {
                Log.d("Error submitting report", error);
                callback.onFailure(ReportSubmissionCallback.Error.NETWORK, error);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                Log.d("Report submitted successfully!");
                callback.onSuccess();
            }
        });
        Log.d("JSON object request for report added to request queue...");
    }


    private Request newRequest(JSONObject report) {

        FormBody.Builder formBuild = new FormBody.Builder();

        MultipartBody.Builder multipartBuilder =  new MultipartBody.Builder();
        multipartBuilder.setType(MultipartBody.FORM);

        Iterator<String> it = report.keys();
        while(it.hasNext()) {
            // 获得key
            String key = it.next();
            if(key.equals("files")){ //图片字段
                try {
                    JSONArray jsonArray = report.getJSONArray("files");
                    for(int i = 0; i < jsonArray.length() ; i ++) {
                        multipartBuilder.addFormDataPart("files"+"["+i+"]",jsonArray.getString(i),  RequestBody.create(MediaType.parse("multipart/form-data"), new File(jsonArray.getString(i))));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else { //非图片字段
                String value = null;
                try {
                    value = report.getString(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                multipartBuilder.addFormDataPart(key,value);
            }

        }

        RequestBody multipartBody = multipartBuilder
                .setType(MultipartBody.FORM)
                .build();

        String url = String.format(ZentaoConstant.ZENTAO_REPORT_URL, Buglife.getProductId(),Buglife.getSessionId());

        return new Request.Builder()
                .header("Authorization", "Client-ID " + UUID.randomUUID())
                .url(url)
                .post(multipartBody)
                .build();

    }

    public class Result {
        private final JSONObject mResponse;
        private final Exception mError;

        Result(JSONObject response) {
            mResponse = response;
            mError = null;
        }

        Result(Exception error) {
            mResponse = null;
            mError = error;
        }

        JSONObject getResponse() {
            return mResponse;
        }

        public Exception getError() {
            return mError;
        }
    }
}
