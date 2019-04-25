package com.buglife.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * @author zhangyueli
 * @date 2019/4/18
 * product（产品）信息
 */
public class ProductData implements Parcelable {

    public String  title;
    public HashMap<String, String> products;

    protected ProductData(Parcel in) {
        title = in.readString();
    }

    public static final Creator<ProductData> CREATOR = new Creator<ProductData>() {
        @Override
        public ProductData createFromParcel(Parcel in) {
            return new ProductData(in);
        }

        @Override
        public ProductData[] newArray(int size) {
            return new ProductData[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap<String, String> getProducts() {
        return products;
    }

    public void setProducts(HashMap<String, String> products) {
        this.products = products;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
    }
}
