package com.rc.facecase.model;

public class ParamsAppUser {
    private String token = "";

    public ParamsAppUser() {
    }

    public ParamsAppUser(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "ParamsAppUser{" +
                "token='" + token + '\'' +
                '}';
    }
}
