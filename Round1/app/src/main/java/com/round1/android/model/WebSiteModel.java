package com.round1.android.model;

import android.os.Parcel;
import android.os.Parcelable;

public class WebSiteModel  implements Parcelable {

    public String name;
    public String url;
    public boolean cached;

    public WebSiteModel() {
        //Empty public constructor
    }

    protected WebSiteModel(Parcel in) {
        name = in.readString();
        url = in.readString();
        cached = in.readByte() != 0;
    }

    public static final Creator<WebSiteModel> CREATOR = new Creator<WebSiteModel>() {
        @Override
        public WebSiteModel createFromParcel(Parcel in) {
            return new WebSiteModel(in);
        }

        @Override
        public WebSiteModel[] newArray(int size) {
            return new WebSiteModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(url);
        parcel.writeByte((byte) (cached ? 1 : 0));
    }
}
