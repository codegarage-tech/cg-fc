package com.rc.facecase.model;

import org.parceler.Parcel;


/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class Image  {

    private int resId = -1;

    public Image() {
    }

    public Image(int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
