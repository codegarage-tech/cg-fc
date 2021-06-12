package com.rc.facecase.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public abstract class BaseAlertDialog extends AlertDialog {

    public static String TAG = BaseAlertDialog.class.getSimpleName();

    //Abstract declaration
    public abstract Builder initView();

    private Activity mActivity;

    protected BaseAlertDialog(Activity activity) {
        super(activity);
        mActivity = activity;
        TAG = getClass().getSimpleName();
//        mBuilder = prepareView(-1, -1, -1, -1, -1, null);
    }

//    public BaseAlertDialog(Activity activity, int title, int message, int negativeButton, int positiveButton, int neutralButton, final OnClickListener onClickListener) {
//        super(activity);
//        mActivity = mActivity;
//        mBuilder = prepareView(title, message, negativeButton, positiveButton, neutralButton, onClickListener);
//    }

    public Activity getActivity() {
        return mActivity;
    }

    public Builder prepareView(String title, String message, int negativeButton, int positiveButton, int neutralButton, final OnClickListener listener) {
        Builder builder = new Builder(this.getContext());
        builder.create();
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }

        if (negativeButton > -1) {
            builder.setNegativeButton(negativeButton, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onClick(dialog, which);
                    }
                    dialog.dismiss();
                }
            });
        }

        if (positiveButton > 0) {
            builder.setPositiveButton(positiveButton, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onClick(dialog, which);
                    }
                    dialog.dismiss();
                }
            });
        }

        if (neutralButton > -1) {
            builder.setNeutralButton(neutralButton, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onClick(dialog, which);
                    }
                    dialog.dismiss();
                }
            });
        }

        return builder;
    }

    public Builder prepareView(String title, String message, String negativeButton, String positiveButton, String neutralButton, final OnClickListener listener) {
        Builder builder = new Builder(this.getContext());
        builder.create();
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }

        if (!TextUtils.isEmpty(negativeButton)) {
            builder.setNegativeButton(negativeButton, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onClick(dialog, which);
                    }
                    dialog.dismiss();
                }
            });
        }

        if (!TextUtils.isEmpty(positiveButton)) {
            builder.setPositiveButton(positiveButton, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onClick(dialog, which);
                    }
                    dialog.dismiss();
                }
            });
        }

        if (!TextUtils.isEmpty(neutralButton)) {
            builder.setNeutralButton(neutralButton, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onClick(dialog, which);
                    }
                    dialog.dismiss();
                }
            });
        }

        return builder;
    }

    public Builder prepareView(int title, int message, int negativeButton, int positiveButton, int neutralButton, final OnClickListener listener) {
        Builder builder = new Builder(this.getContext());
        builder.create();
        if (title > 0) {
            builder.setTitle(title);
        }
        if (message > 0) {
            builder.setMessage(message);
        }

        if (negativeButton > -1) {
            builder.setNegativeButton(negativeButton, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onClick(dialog, which);
                    }
                    dialog.dismiss();
                }
            });
        }

        if (positiveButton > 0) {
            builder.setPositiveButton(positiveButton, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onClick(dialog, which);
                    }
                    dialog.dismiss();
                }
            });
        }

        if (neutralButton > -1) {
            builder.setNeutralButton(neutralButton, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onClick(dialog, which);
                    }
                    dialog.dismiss();
                }
            });
        }

        return builder;
    }
}