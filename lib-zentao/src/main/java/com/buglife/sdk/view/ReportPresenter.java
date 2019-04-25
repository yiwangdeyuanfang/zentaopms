package com.buglife.sdk.view;/**
 * @author zhangyueli
 * @date 2019/4/23
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.buglife.sdk.Log;
import com.buglife.sdk.ZentaoConstant;
import com.buglife.sdk.model.*;
import com.google.gson.Gson;
import com.langlib.net.HttpCallback;
import com.langlib.net.HttpTaskUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;

/**
 * @author zhangyueli
 * @date 2019/4/23
 *
 */
public class ReportPresenter {

    private Context mContext;

    private IReportPresenterView mView;

    private String mSessionId;

    private String mProductID;

    public ReportPresenter(Context context, IReportPresenterView view, String productID){
        mContext = context;
        mView = view;
        mSessionId = "";
        mProductID = productID;
    }

    public void getZentaoConfig(final Context context){

        SharedPreferences sp = context.getSharedPreferences(ZentaoConstant.APP_NAME, Context.MODE_PRIVATE);
        String sessionId = sp.getString(ZentaoConstant.SESSION_ID,"");

        if(!TextUtils.isEmpty(sessionId)){ //sessionId不为空时，说明登录过，不用再次登录
            mSessionId = sessionId;

            SharedPreferences loginSp = context.getSharedPreferences(ZentaoConstant.APP_NAME, Context.MODE_PRIVATE);
            boolean login = loginSp.getBoolean(ZentaoConstant.LOGIN,false);
            if(login) {
                getBugAllInfo(sessionId);
            }else {
                mView.loginFail(mSessionId);
            }
        }else {

            HttpTaskUtil.getTask().reqHttpPost(ZentaoConstant.ZENTAO_CONFIG_URL, "", new HttpCallback<String>() {

                @Override
                public void onSuccess(String configStr) {

                    Gson gson = new Gson();

                    ZentaoConfigData zentaoConfigData = gson.fromJson(configStr, ZentaoConfigData.class);
                    mView.setZentaoConfig(zentaoConfigData);

                    mSessionId = zentaoConfigData.getSessionID();
                    SharedPreferences sp = context.getSharedPreferences(ZentaoConstant.APP_NAME, Context.MODE_PRIVATE);
                    sp.edit().putString(ZentaoConstant.SESSION_ID,zentaoConfigData.getSessionID()).commit();

                }

                @Override
                public void onError(String errorMsg) {
                    Log.i("uploadData() onError() errorMsg = " + errorMsg);
                }
            }, String.class);
        }
    }

    public void loginZentao(String account, String password, final String sessionId){

        String url = String.format(ZentaoConstant.LOGIN_URL, sessionId, account, password, true);

        LoginData loginData = new LoginData();
        loginData.setAccount(account);
        loginData.setPassword(password);
        loginData.setZentaosid(sessionId);
        loginData.setKeepLogin(true);

        Log.i("loginZentao() url = " + url);
        HttpTaskUtil.getTask().reqHttpPost(url, "", new HttpCallback<String>() {

            @Override
            public void onSuccess(String loginStr) {
                Log.i("loginZentao() loginStr = " + loginStr);

                if(loginStr.contains("登录失败") || loginStr.contains("failed") || !loginStr.contains("user")){
                    mView.loginFail(sessionId);
                    SharedPreferences sp = mContext.getSharedPreferences(ZentaoConstant.APP_NAME, Context.MODE_PRIVATE);
                    sp.edit().putBoolean(ZentaoConstant.LOGIN,false).commit();
                }else { //登录成功后，获取bug信息

                    getBugAllInfo(sessionId);

                    SharedPreferences sp = mContext.getSharedPreferences(ZentaoConstant.APP_NAME, Context.MODE_PRIVATE);
                    sp.edit().putBoolean(ZentaoConstant.LOGIN,true).commit();
                }

            }

            @Override
            public void onError(String errorMsg) {
                Log.i("uploadData() onError() errorMsg = " + errorMsg);
            }
        }, String.class);
    }

    public void logoutZentao(final Context context){

        HttpTaskUtil.getTask().reqHttpPost(ZentaoConstant.LOGOUT_URL,"", new HttpCallback<String>() {

            @Override
            public void onSuccess(String logoutStr) {

                Log.i("logoutZentao() logoutStr = " + logoutStr);
                SharedPreferences sp = context.getSharedPreferences(ZentaoConstant.APP_NAME, Context.MODE_PRIVATE);
                sp.edit().putString(ZentaoConstant.SESSION_ID,"").commit();
            }

            @Override
            public void onError(String errorMsg) {
                Log.i("uploadData() onError() errorMsg = " + errorMsg);
            }
        }, String.class);
    }

    //获取bug的所有信息
    public void getBugAllInfo(String sessionId) {
        String url = String.format(ZentaoConstant.ZENTAO_REPORT_URL, mProductID, sessionId);

        HttpTaskUtil.getTask().reqHttpPost(url, "", new HttpCallback<BaseBody>() {

            @Override
            public void onSuccess(BaseBody body) {
                BaseBody baseBody = body;

                Gson gson = new Gson();
                BugInfo accountInfo = gson.fromJson(body.getData(), BugInfo.class);

                mView.setBugAllInfo(accountInfo);
            }

            @Override
            public void onError(String errorMsg) {
                super.onError(errorMsg);
            }
        }, BaseBody.class);

    }

    //全部产品列表
    public void getProductsList() {

        HttpTaskUtil.getTask().reqHttpPost(ZentaoConstant.PRODUCTS_LIST, "", new HttpCallback<BaseBody>() {

            @Override
            public void onSuccess(BaseBody baseBody) {
                Gson gson = new Gson();
                ProductData productData = gson.fromJson(baseBody.getData(), ProductData.class);
                Map<String, String> account = productData.getProducts();
                for (String key : account.keySet()) {


                }
            }

            @Override
            public void onError(String errorMsg) {
                Log.i("uploadData() onError() errorMsg = " + errorMsg);
            }
        }, BaseBody.class);
    }


    //获取所有用户
    public void getAllUser() {

        String url = String.format(ZentaoConstant.LOAD_ALL_USERS, mSessionId);

        HttpTaskUtil.getTask().reqHttpPost(url, "", new HttpCallback<String>() {

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

        String url = String.format(ZentaoConstant.SEVERITY_LIST, mSessionId);


        HttpTaskUtil.getTask().reqHttpPost(url, "", new HttpCallback<Severity>() {

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
