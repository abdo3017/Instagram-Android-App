package com.example.instaapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class likes implements Parcelable, Serializable {
    int counter;
    List<String>likers=new ArrayList<>();

    public likes() {
    }
    public likes(likes likes) {
        counter=likes.getCounter();
        likers=new ArrayList<>(likes.likers);
    }
    protected likes(Parcel in) {
        counter = in.readInt();
        likers = in.createStringArrayList();
    }

    public static final Creator<likes> CREATOR = new Creator<likes>() {
        @Override
        public likes createFromParcel(Parcel in) {
            return new likes(in);
        }

        @Override
        public likes[] newArray(int size) {
            return new likes[size];
        }
    };

    public int getCounter() { return likers.size(); }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public List<String> getLikers() {
        return likers;
    }

    public void setLikers(List<String> likers) {
        this.likers = likers;
    }
    public void setLike(String likers) {
        this.likers.add( likers);
    }
    public void deleteLike(String likers) {
        this.likers.remove(likers);
    }
    public likes(int counter, List<String> likers) {
        this.counter = counter;
        this.likers = likers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(counter);
        dest.writeStringList(likers);
    }
}
