package com.dev.objects;

import java.util.ArrayList;

import java.util.List;

public class UserObject {
    private int id;
    private String username;
    private String password;
    private String token;
    private List<MessageObject> posts;

    //Constructors
    public UserObject(){
        this.posts = new ArrayList<>();
    }

    public UserObject(int id, String username){
        this.username = username;
        this.id = id;
        this.posts = new ArrayList<>();
    }

    public UserObject(int id, String username, List<MessageObject> posts) {
        this.id = id;
        this.username = username;
        this.posts = posts;
    }

    //Getters
    public int getId() {
        return id;
    }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getToken() {
        return token;
    }

    public List<MessageObject> getPosts() { return this.posts;}

    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) { this.password = password;}

    public void setToken(String token) {
        this.token = token;
    }

    public void setPosts(List<MessageObject> posts) { this.posts = posts;}

}
