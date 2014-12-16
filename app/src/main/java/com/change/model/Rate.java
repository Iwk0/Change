package com.change.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Rate implements Parcelable {

    public static final String UP = "up";
    public static final String DOWN = "down";
    public static final String EQUAL = "equal";

    public int id;
    public String name;
    public String code;
    public String movement = EQUAL;
    public boolean fixed = false;
    public int ratio;
    public double rate;
    public double reverseRate;

    public Rate() {}

    public Rate(Parcel in) {
        this.name = in.readString();
        this.code = in.readString();
        this.movement = in.readString();
        this.fixed = in.readByte() != 0;
        this.ratio = in.readInt();
        this.rate = in.readDouble();
        this.reverseRate = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(code);
        parcel.writeString(movement);
        parcel.writeByte((byte) (fixed ? 1 : 0));
        parcel.writeInt(ratio);
        parcel.writeDouble(rate);
        parcel.writeDouble(reverseRate);
    }

    public static final Creator CREATOR = new Creator() {

        public Rate createFromParcel(Parcel in) {
            return new Rate(in);
        }

        public Rate[] newArray(int size) {
            return new Rate[size];
        }
    };

    @Override
    public String toString() {
        return code;
    }
}