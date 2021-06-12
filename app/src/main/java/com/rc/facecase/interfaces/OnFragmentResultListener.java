package com.rc.facecase.interfaces;

import android.content.Intent;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public interface OnFragmentResultListener {
    public void onFragmentResult(int requestCode, int resultCode, Intent data);
}