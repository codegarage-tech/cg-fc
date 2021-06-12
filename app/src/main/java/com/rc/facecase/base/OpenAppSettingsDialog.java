package com.rc.facecase.base;

import android.app.Activity;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class OpenAppSettingsDialog extends BaseAlertDialog {

    private OnClickListener mOnClickListener;
    private String mMessage = "";

    public OpenAppSettingsDialog(Activity activity, String message, OnClickListener onClickListener) {
        super(activity);
        mOnClickListener = onClickListener;
        mMessage = message;
    }

    @Override
    public Builder initView() {
        Builder builder = prepareView("", mMessage, "CANCEL", "I AGREE", "", mOnClickListener);

        return builder;
    }
}