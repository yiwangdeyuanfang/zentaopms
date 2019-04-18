package com.buglife.sdk;


/**
 * @author zhangyueli
 * @date  2019/4/18 上午11:32
 *
*/
public class ZentaoConstant {

    public static final String ZENTAO_URL = "http://192.168.80.30/zentaopms/www";

    public static final String ZENTAO_REPORT_URL =ZENTAO_URL + "/index.php?m=bug&f=create&t=json&productID=1&zentaosid=5nk9viec8fk6nithsv3trq3690";
    public static final String LOG_REPORT_URL = ZENTAO_URL + "/index.php?m=user&f=login&t=json&zentaosid=5nk9viec8fk6nithsv3trq3690";
    public static final String LOAD_ALL_USERS = ZENTAO_URL + "/index.php?m=bug&f=ajaxLoadAllUsers&t=json&zentaosid=5nk9viec8fk6nithsv3trq3690";
}
