package com.buglife.sdk.model;/**
 * @author zhangyueli
 * @date 2019/4/18
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;

/**
 * @author zhangyueli
 * @date 2019/4/18
 *
 */
public class AccountInfo extends BaseBody<AccountInfo> implements Parcelable {

    private String title;
//    private HashMap<String,String> products;
//    private HashMap<String,String> users;
    private int productID;
    private String productName;
    private List<String> moduleOptionMenu;
//    private HashMap<String,String> projects;
//    private HashMap<String,String> builds;
    private int moduleID;
    private int projectID;
    private int taskID;
    private int storyID;

    private int severity;
    private String type;
    private String assignedTo;

    protected AccountInfo(Parcel in) {
        title = in.readString();
        productID = in.readInt();

        productName = in.readString();
        moduleOptionMenu = in.createStringArrayList();
        moduleID = in.readInt();
        projectID = in.readInt();
        taskID = in.readInt();
        storyID = in.readInt();
        severity = in.readInt();
        type = in.readString();
        assignedTo = in.readString();
    }

    public static final Creator<AccountInfo> CREATOR = new Creator<AccountInfo>() {
        @Override
        public AccountInfo createFromParcel(Parcel in) {
            return new AccountInfo(in);
        }

        @Override
        public AccountInfo[] newArray(int size) {
            return new AccountInfo[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public HashMap<String, String> getProducts() {
//        return products;
//    }
//
//    public void setProducts(HashMap<String, String> products) {
//        this.products = products;
//    }
//
//    public HashMap<String, String> getUsers() {
//        return users;
//    }
//
//    public void setUsers(HashMap<String, String> users) {
//        this.users = users;
//    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<String> getModuleOptionMenu() {
        return moduleOptionMenu;
    }

    public void setModuleOptionMenu(List<String> moduleOptionMenu) {
        this.moduleOptionMenu = moduleOptionMenu;
    }

//    public HashMap<String, String> getProjects() {
//        return projects;
//    }
//
//    public void setProjects(HashMap<String, String> projects) {
//        this.projects = projects;
//    }
//
//    public HashMap<String, String> getBuilds() {
//        return builds;
//    }
//
//    public void setBuilds(HashMap<String, String> builds) {
//        this.builds = builds;
//    }

    public int getModuleID() {
        return moduleID;
    }

    public void setModuleID(int moduleID) {
        this.moduleID = moduleID;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getStoryID() {
        return storyID;
    }

    public void setStoryID(int storyID) {
        this.storyID = storyID;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeInt(productID);
        parcel.writeString(productName);
        parcel.writeStringList(moduleOptionMenu);
        parcel.writeInt(moduleID);
        parcel.writeInt(projectID);
        parcel.writeInt(taskID);
        parcel.writeInt(storyID);
        parcel.writeInt(severity);
        parcel.writeString(type);
        parcel.writeString(assignedTo);
    }
}
