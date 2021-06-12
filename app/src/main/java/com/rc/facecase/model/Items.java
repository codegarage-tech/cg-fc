package com.rc.facecase.model;

import org.parceler.Parcel;

@Parcel
public class Items {

    private String id = "";
    private String sub_category_id = "";
    private String source = "";
    private String title = "";
    private String prio = "";
    private boolean isShown = false;
    private int lastPlayed = -1;
    private int isPlaying = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSub_category_id() {
        return sub_category_id;
    }

    public void setSub_category_id(String sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrio() {
        return prio;
    }

    public void setPrio(String prio) {
        this.prio = prio;
    }

    public boolean isShown() {
        return isShown;
    }

    public void setShown(boolean shown) {
        isShown = shown;
    }

    public int getLastPlayed() {
        return lastPlayed;
    }

    public int getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(int isPlaying) {
        this.isPlaying = isPlaying;
    }

    public void setLastPlayed(int lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    @Override
    public String toString() {
        return "Items{" +
                "id='" + id + '\'' +
                ", sub_category_id='" + sub_category_id + '\'' +
                ", source='" + source + '\'' +
                ", title='" + title + '\'' +
                ", prio='" + prio + '\'' +
                ", isShown=" + isShown +
                ", lastPlayed=" + lastPlayed +
                ", isPlaying=" + isPlaying +
                '}';
    }
}
