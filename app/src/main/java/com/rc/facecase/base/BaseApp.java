package com.rc.facecase.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.rc.facecase.util.AppUtil;
import com.rc.facecase.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class BaseApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mContext == null) {
            mContext = this;
        }

        //Initialize logger
        new Logger.Builder()
                .isLoggable(AppUtil.isDebug(mContext))
                .build();

        //For using vector drawable
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //Multidex initialization
        MultiDex.install(this);
    }

    public static Context getGlobalContext() {
        return mContext;
    }
}