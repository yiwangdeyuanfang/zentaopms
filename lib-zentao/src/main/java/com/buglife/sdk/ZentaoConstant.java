package com.buglife.sdk;

import com.buglife.sdk.model.BugTypeData;
import com.buglife.sdk.model.Severity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyueli
 * @date  2019/4/18 上午11:32
 *
*/
public class ZentaoConstant {

    public static final String ZENTAO_URL = "http://192.168.80.30/zentaopms/www";

    public static final String ZENTAO_CONFIG_URL = ZENTAO_URL + "/index.php?mode=getconfig";
    public static final String LOGIN_URL = ZENTAO_URL + "/index.php?m=user&f=login&t=json&zentaosid=%s";
    public static final String ZENTAO_REPORT_URL = ZENTAO_URL + "/index.php?m=bug&f=create&t=json&productID=1&zentaosid=5nk9viec8fk6nithsv3trq3690";
    public static final String LOAD_ALL_USERS = ZENTAO_URL + "/index.php?m=bug&f=ajaxLoadAllUsers&t=json&zentaosid=5nk9viec8fk6nithsv3trq3690";
    public static final String SEVERITY_LIST = ZENTAO_URL + "/index.php?m=custom&f=set&module=bug&t=json&field=severityList&zentaosid=5nk9viec8fk6nithsv3trq3690";
    public static final String PROJECTS_LIST = ZENTAO_URL + "/index.php?m=project&f=team&t=json&projectID=1&zentaosid=5nk9viec8fk6nithsv3trq3690";

    //严重等级
    public static String[] SEVERITY = {"1","2","3","4"};

    //优先级
    public static String[] PRI_TYPE = {"1","2","3","4"};

    private static List<BugTypeData> bugTypeDataList;

    //BUG类型
    public static List<BugTypeData> bugTypeList(){
        if(bugTypeDataList == null) {
            bugTypeDataList = new ArrayList<>();
            bugTypeDataList.add(new BugTypeData("codeerror", "代码错误"));
            bugTypeDataList.add(new BugTypeData("interface", "界面优化"));
            bugTypeDataList.add(new BugTypeData("config", "配置相关"));
            bugTypeDataList.add(new BugTypeData("install", "安装部署"));
            bugTypeDataList.add(new BugTypeData("security", "安全相关"));
            bugTypeDataList.add(new BugTypeData("performance", "性能问题"));
            bugTypeDataList.add(new BugTypeData("standard", "标准规范"));
            bugTypeDataList.add(new BugTypeData("automation", "测试脚本"));
            bugTypeDataList.add(new BugTypeData("others", "其他"));
            bugTypeDataList.add(new BugTypeData("designchange", "设计变更"));
            bugTypeDataList.add(new BugTypeData("newfeature", "新特性"));
            bugTypeDataList.add(new BugTypeData("designdefect", "设计缺陷"));
            bugTypeDataList.add(new BugTypeData("trackthings", "追踪"));
        }
        return bugTypeDataList;
    }

    //创建bug接口所需的参数
    public static String BUG_TITLE = "title";
    public static String BUG_ASSIGNEDTO = "assignedTo";
    public static String BUG_OPENEDBUILD = "openedBuild";
    public static String BUG_FILES = "files";
    public static String BUG_PRODUCT = "product";
    public static String BUG_PROJECTS = "projects";
    public static String BUG_MODULE = "module";
    public static String BUG_TYPE = "type";                //bug类型
    public static String BUG_SEVERITY = "severity";        //严重程度
    public static String BUG_STEPS = "steps";
    public static String BUG_OS = "os";                    //操作系统
    public static String BUG_PRI = "pri";                  //优先级
}
