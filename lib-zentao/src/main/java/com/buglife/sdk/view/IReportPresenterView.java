package com.buglife.sdk.view;

import com.buglife.sdk.model.AccountInfo;
import com.buglife.sdk.model.AllUserData;
import com.buglife.sdk.model.ZentaoConfigData;

/**
 * @author zhangyueli
 * @date 2019/4/23
 */
public interface IReportPresenterView {

    void setZentaoConfig(ZentaoConfigData zentaoConfig);

    void loginFail();

    void setBugAllInfo(AccountInfo accountInfo);

    void setAllUserData(AllUserData allUserData);
}
