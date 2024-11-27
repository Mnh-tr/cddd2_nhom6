package com.example.cddd2_nhom6.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ApiModel implements Parcelable {
    private String id;
    private String name;
    private String url;
    private boolean isChecked; // Thêm thuộc tính isChecked

    public ApiModel() {
        // Constructor mặc định
    }

    public ApiModel(String name, String url) {
        this.name = name;
        this.url = url;
    }
    public ApiModel(String id, String name, String url, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.isChecked = isChecked;
    }

    public ApiModel(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    protected ApiModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        url = in.readString();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<ApiModel> CREATOR = new Creator<ApiModel>() {
        @Override
        public ApiModel createFromParcel(Parcel in) {
            return new ApiModel(in);
        }

        @Override
        public ApiModel[] newArray(int size) {
            return new ApiModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(url);
        parcel.writeByte((byte) (isChecked ? 1 : 0));
    }
}



