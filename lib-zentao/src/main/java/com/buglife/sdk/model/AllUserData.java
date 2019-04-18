package com.buglife.sdk.model;/**
 * @author zhangyueli
 * @date 2019/4/18
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyueli
 * @date 2019/4/18
 *
 */

public class AllUserData {

    private List<AllUserItemData> userItemData;

   public AllUserData(){
       userItemData = new ArrayList<>();
   }

    public List<AllUserItemData> getUserItemData() {
        return userItemData;
    }

    public void setUserItemData(List<AllUserItemData> userItemData) {
        this.userItemData = userItemData;
    }

    public void add(AllUserItemData itemData){
        userItemData.add(itemData);
    }
}
