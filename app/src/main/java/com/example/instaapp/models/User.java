package com.example.instaapp.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    String Name=new String();
    String Id=new String();
    String Email=new String();
    String Password=new String();
    String gender=new String();
    String Image=new String();
    String About=new String();
    List<String> Followers=new ArrayList<>();
    List<String> Following=new ArrayList<>();
    List<String> Posts=new ArrayList<>();

    public User() {
    }
    public User(User user) {
        Name=new String(user.getName());
        Id=new String(user.getId());
        Email=new String(user.getEmail());
        Password=new String(user.getPassword());
        gender=new String(user.getGender());
        Image=new String(user.getImage());
        About=new String(user.getAbout());
        Followers=new ArrayList<>(user.Followers);
        Following=new ArrayList<>(user.Following);
        Posts=new ArrayList<>(user.Posts);
    }
    public User(String name, String id, String email, String password, String gender, String image, String about, List<String> followers, List<String> following, List<String> posts) {
        Name = name;
        Id = id;
        Email = email;
        Password = password;
        this.gender = gender;
        Image = image;
        About = about;
        Followers = followers;
        Following = following;
        Posts = posts;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getAbout() {
        return About;
    }

    public void setAbout(String about) {
        About = about;
    }

    public List<String> getFollowers() {
        return Followers;
    }

    public void setFollowers(List<String> followers) {
        Followers = followers;
    }

    public List<String> getFollowing() {
        return Following;
    }

    public void setFollowing(List<String> following) {
        Following = following;
    }

    public List<String> getPosts() {
        return Posts;
    }

    public void setPosts(List<String> posts) {
        Posts = posts;
    }



}
