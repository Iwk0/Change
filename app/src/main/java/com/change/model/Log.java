package com.change.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Log implements Parcelable {

    public int id;
    public String currentDay;
    public double currencyFromAmount;
    public String codeFrom;
    public double currencyToAmount;
    public String codeTo;

    public Log() {}

    public Log(Parcel in) {
        this.currentDay = in.readString();
        this.currencyFromAmount = in.readDouble();
        this.codeFrom = in.readString();
        this.currencyToAmount = in.readDouble();
        this.codeTo = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(currentDay);
        parcel.writeDouble(currencyFromAmount);
        parcel.writeString(codeFrom);
        parcel.writeDouble(currencyToAmount);
        parcel.writeString(codeTo);
    }

    public static final Creator CREATOR = new Creator() {

        public Log createFromParcel(Parcel in) {
            return new Log(in);
        }

        public Log[] newArray(int size) {
            return new Log[size];
        }
    };
}