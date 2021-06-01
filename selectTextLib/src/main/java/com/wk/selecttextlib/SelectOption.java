package com.wk.selecttextlib;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SelectOption implements Parcelable {

    public static final int TYPE_COPY = 1;
    public static final int TYPE_SELECT_ALL = 2;
    public static final int TYPE_CUSTOM = 3;


    private String name;
    private int type;

    public SelectOption(int type, @NonNull String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    protected SelectOption(Parcel in) {
        name = in.readString();
        type = in.readInt();
    }

    public static final Creator<SelectOption> CREATOR = new Creator<SelectOption>() {
        @Override
        public SelectOption createFromParcel(Parcel in) {
            return new SelectOption(in);
        }

        @Override
        public SelectOption[] newArray(int size) {
            return new SelectOption[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(type);
    }
}