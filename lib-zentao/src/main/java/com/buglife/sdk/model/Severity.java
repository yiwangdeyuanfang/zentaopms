package com.buglife.sdk.model;

import java.util.List;

/**
 * @author zhangyueli
 * @date 2019/4/18
 * Bug严重程度
 */
public class Severity extends BaseBody<Severity> {
    private String title;
    private List<String> fieldList;

    private List<String> dbFields;

    private String field;
    private String lang2Set;
    private String module;
    private String currentLang;
    private boolean canAdd;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<String> fieldList) {
        this.fieldList = fieldList;
    }

    public List<String> getDbFields() {
        return dbFields;
    }

    public void setDbFields(List<String> dbFields) {
        this.dbFields = dbFields;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getLang2Set() {
        return lang2Set;
    }

    public void setLang2Set(String lang2Set) {
        this.lang2Set = lang2Set;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getCurrentLang() {
        return currentLang;
    }

    public void setCurrentLang(String currentLang) {
        this.currentLang = currentLang;
    }

    public boolean isCanAdd() {
        return canAdd;
    }

    public void setCanAdd(boolean canAdd) {
        this.canAdd = canAdd;
    }
}
