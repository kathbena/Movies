package com.example.kathleenbenavides.movies.DO;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathleenbenavides on 10/26/15.
 */
public class ReviewDetailsDO implements Parcelable {

    private String id;
    private String author;
    private String content;
    private String url;

    public static final Creator<ReviewDetailsDO> CREATOR = new Creator<ReviewDetailsDO>() {
        @Override
        public ReviewDetailsDO createFromParcel(Parcel in) {
            return new ReviewDetailsDO(in);
        }

        @Override
        public ReviewDetailsDO[] newArray(int size) {
            return new ReviewDetailsDO[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }

    public ReviewDetailsDO() {

    }

    protected ReviewDetailsDO(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }
}
