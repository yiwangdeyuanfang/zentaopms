package com.buglife.sdk.model;

/**
 * @author zhangyueli
 * @date 2019/4/18
 * projects信息
 */
public class ProjectsData {

    public String  projectsId; //projects ID
    private String projectsDes; // 汉字描述

    public String getProjectsId() {
        return projectsId;
    }

    public void setProjectsId(String projectsId) {
        this.projectsId = projectsId;
    }

    public String getProjectsDes() {
        return projectsDes;
    }

    public void setProjectsDes(String projectsDes) {
        this.projectsDes = projectsDes;
    }
}
