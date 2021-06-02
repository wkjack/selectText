package com.wk.selecttextlib;

import android.os.Parcel;
import android.os.Parcelable;

public class SelectionInfo implements Parcelable {

    public int mStart;
    public int mEnd;
    public String mSelectionContent;

    public SelectionInfo() {
    }

    protected SelectionInfo(Parcel in) {
        mStart = in.readInt();
        mEnd = in.readInt();
        mSelectionContent = in.readString();
    }

    public static final Creator<SelectionInfo> CREATOR = new Creator<SelectionInfo>() {
        @Override
        public SelectionInfo createFromParcel(Parcel in) {
            return new SelectionInfo(in);
        }

        @Override
        public SelectionInfo[] newArray(int size) {
            return new SelectionInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mStart);
        dest.writeInt(mEnd);
        dest.writeString(mSelectionContent);
    }
}