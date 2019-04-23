package com.buglife.sdk.view;/**
 * @author zhangyueli
 * @date 2019/4/23
 */

import android.text.TextUtils;
import android.widget.Toast;
import com.buglife.sdk.Log;
import com.buglife.sdk.ReportActivity;
import com.buglife.sdk.ZentaoConstant;
import com.buglife.sdk.model.*;
import com.google.gson.Gson;
import com.langlib.net.HttpCallback;
import com.langlib.net.HttpTaskUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyueli
 * @date 2019/4/23
 *
 */
public class ReportPresenter {

    private IReportPresenterView mView;

    public ReportPresenter(IReportPresenterView view){
        mView = view;
    }

    public void getZentaoConfig(){
        HttpTaskUtil.getTask().reqHttpPost(ZentaoConstant.ZENTAO_CONFIG_URL, "", new HttpCallback<String>() {

            @Override
            public void onSuccess(String configStr) {

                Gson gson = new Gson();

                ZentaoConfigData zentaoConfigData = gson.fromJson(configStr, ZentaoConfigData.class);
                mView.setZentaoConfig(zentaoConfigData);

            }

            @Override
            public void onError(String errorMsg) {
                Log.i("uploadData() onError() errorMsg = " + errorMsg);
            }
        }, String.class);
    }

    public void loginZentao(String account, String password, String sessionId){
        String url = String.format(ZentaoConstant.LOGIN_URL, sessionId);

        LoginData loginData = new LoginData();
        loginData.setAccount(account);
        loginData.setPassword(password);
        loginData.setZentaosid(sessionId);

        HttpTaskUtil.getTask().reqHttpPost(new Gson().toJson(loginData), url, new HttpCallback<String>() {

            @Override
            public void onSuccess(String loginStr) {
                Log.i("loginZentao() loginStr = " + loginStr);

                if(loginStr.contains("登录失败") || loginStr.contains("failed")){
                    mView.loginFail();
                }else { //登录成功后，获取bug信息
                    getBugAllInfo();
                }

            }

            @Override
            public void onError(String errorMsg) {
                Log.i("uploadData() onError() errorMsg = " + errorMsg);
            }
        }, String.class);
    }

    //获取bug的所有信息
    public void getBugAllInfo() {

        HttpTaskUtil.getTask().reqHttpPost(ZentaoConstant.ZENTAO_REPORT_URL, "", new HttpCallback<BaseBody>() {

            @Override
            public void onSuccess(BaseBody body) {
                BaseBody baseBody = body;

                Gson gson = new Gson();
                AccountInfo accountInfo = gson.fromJson(body.getData(), AccountInfo.class);

                mView.setBugAllInfo(accountInfo);
            }

            @Override
            public void onError(String errorMsg) {
                super.onError(errorMsg);
            }
        }, BaseBody.class);

    }


    //获取所有用户
    public void getAllUser() {



        HttpTaskUtil.getTask().reqHttpPost(ZentaoConstant.LOAD_ALL_USERS, "", new HttpCallback<String>() {

            @Override
            public void onSuccess(String htmlString) {
                Log.i("uploadData() getAllUser() s = " + htmlString);

                AllUserData allUserData = new AllUserData();
                Document document = Jsoup.parse(htmlString);
                Elements elements = document.getElementsByTag("option");

                //遍历Elements,解析其标签
                for (Element title : elements) {
                    Log.d("title = " + title.text() + " " + title.val());

                    if (!TextUtils.isEmpty(title.val())) {
                        AllUserItemData itemData = new AllUserItemData();
                        itemData.setValue(title.val());
                        itemData.setName(title.text());
                        allUserData.add(itemData);
                    }
                }

                mView.setAllUserData(allUserData);

            }

            @Override
            public void onError(String errorMsg) {
                Log.i("uploadData() onError() errorMsg = " + errorMsg);
            }
        }, String.class);
    }

    //获取Bug严重程度列表
    public void getSeverityList() {

        HttpTaskUtil.getTask().reqHttpPost(ZentaoConstant.SEVERITY_LIST, "", new HttpCallback<Severity>() {

            @Override
            public void onSuccess(Severity severity) {
            }

            @Override
            public void onError(String errorMsg) {
                Log.i("uploadData() onError() errorMsg = " + errorMsg);
            }
        }, Severity.class);
    }
}
