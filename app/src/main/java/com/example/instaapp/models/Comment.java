package com.example.instaapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Comment implements Parcelable , Serializable {
    String commenterId=new String();
    String disc=new String();
    String user_name=new String();

    public Comment(String commenterId, String disc, String user_name) {
        this.commenterId = commenterId;
        this.disc = disc;
        this.user_name = user_name;
    }

    public Comment() {
    }

    protected Comment(Parcel in) {
        commenterId = in.readString();
        disc = in.readString();
        user_name = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(String commenterId) {
        this.commenterId = commenterId;
    }

    public String getDisc() {
        return disc;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }

    public Comment(String commenterId, String disc) {
        this.commenterId = commenterId;
        this.disc = disc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(commenterId);
        dest.writeString(disc);
        dest.writeString(user_name);
    }
}
