package com.example.kathleenbenavides.movies.DO;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathleenbenavides on 10/26/15.
 */
public class TrailerDetailsDO implements Parcelable {

    private String source;
    private String name;

    public static final Creator<TrailerDetailsDO> CREATOR = new Creator<TrailerDetailsDO>() {
        @Override
        public TrailerDetailsDO createFromParcel(Parcel in) {
            return new TrailerDetailsDO(in);
        }

        @Override
        public TrailerDetailsDO[] newArray(int size) {
            return new TrailerDetailsDO[size];
        }
    };

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(source);
        dest.writeString(name);
    }

    public TrailerDetailsDO() {

    }

    protected TrailerDetailsDO(Parcel in) {

        source = in.readString();
        name = in.readString();
    }
}
