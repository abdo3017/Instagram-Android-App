package com.example.instaapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post implements Comparable<Post> , Parcelable ,Serializable{
    List<Comment> comments=new ArrayList<>();
    String userId=new String();
    String postId=new String();
    String Image=new String();
    String disc=new String();
    likes likes=new likes();
    private Date dateTime=new Date();
    public Post(Post post){
        comments=new ArrayList<>(post.comments);
        userId=new String(post.getUserId());
        postId=new String(post.getPostId());
        Image=new String(post.getImage());
        disc=new String(post.getDisc());
        likes=new likes(post.getLikes());
        dateTime=new Date(post.getDateTime().getTime());

    }
    protected Post(Parcel in) {
        userId = in.readString();
        postId = in.readString();
        Image = in.readString();
        disc = in.readString();
    }


    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date datetime) {
        this.dateTime = datetime;
    }



    public Post() {
    }

    public String getDisc() {
        return disc;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public com.example.instaapp.models.likes getLikes() {
        return likes;
    }

    public void setLikes(com.example.instaapp.models.likes likes) {
        this.likes = likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Post(String userId, String postId, String image,String disc,Date date) {
        this.userId = userId;
        this.postId = postId;
        dateTime=date;
        Image = image;
        this.disc=disc;
        this.likes = new likes(0,new ArrayList<String>());
        this.comments = new ArrayList<>();
    }

    @Override
    public int compareTo(Post o) {
        return getDateTime().compareTo(o.getDateTime());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(comments);
        dest.writeString(userId);
        dest.writeString(postId);
        dest.writeString(Image);
        dest.writeString(disc);
        dest.writeParcelable(likes, flags);
    }
}
