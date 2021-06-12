package com.rc.facecase.model;

public class AppUser {

    private String id = "";
    private String token = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
