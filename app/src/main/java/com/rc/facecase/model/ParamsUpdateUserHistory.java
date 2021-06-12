package com.rc.facecase.model;

public class ParamsUpdateUserHistory {
    private String user_id = "";
    private String item_id = "";

    public ParamsUpdateUserHistory(String user_id, String item_id) {
        this.user_id = user_id;
        this.item_id = item_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "ParamsUpdateUserHistory{" +
                "user_id='" + user_id + '\'' +
                ", item_id='" + item_id + '\'' +
                '}';
    }
}
