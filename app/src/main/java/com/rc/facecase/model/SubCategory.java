package com.rc.facecase.model;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
@Parcel
public class SubCategory {

    private String id = "";
    private String category_id = "";
    private String sub_category_name = "";
    private String is_picture = "";
    private List<Items> items = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public String getIs_picture() {
        return is_picture;
    }

    public void setIs_picture(String is_picture) {
        this.is_picture = is_picture;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "SubCategory{" +
                "id='" + id + '\'' +
                ", category_id='" + category_id + '\'' +
                ", sub_category_name='" + sub_category_name + '\'' +
                ", is_picture='" + is_picture + '\'' +
                ", items=" + items +
                '}';
    }
}
