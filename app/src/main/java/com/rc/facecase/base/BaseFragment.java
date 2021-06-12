package com.rc.facecase.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rc.facecase.R;
import com.rc.facecase.interfaces.OnFragmentBackPressedListener;
import com.rc.facecase.interfaces.OnFragmentResultListener;
import com.rc.facecase.interfaces.OnFragmentUpdateListener;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public abstract class BaseFragment extends Fragment implements OnFragmentBackPressedListener, OnFragmentResultListener, OnFragmentUpdateListener {

    //Local variable
    public View containerView;
    public String TAG = BaseFragment.class.getSimpleName();
    public ProgressDialog mProgressDialog;

    //Abstract declaration
    public abstract int initFragmentLayout();

    public abstract void initFragmentBundleData(Bundle bundle);

    public abstract void initFragmentViews(View parentView);

    public abstract void initFragmentViewsData();

    public abstract void initFragmentActions();

    public abstract void initFragmentBackPress();

    public abstract void initFragmentUpdate(Object object);

    public abstract void initFragmentOnResult(int requestCode, int resultCode, Intent data);

    public static <T extends BaseFragment> T newInstance(Class<T> fragmentClazz, Bundle args) {
        T fragment = null;
        try {
            fragment = fragmentClazz.newInstance();
            fragment.setArguments(args);
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            initFragmentBundleData(getArguments());
        }
        this.TAG = getClass().getSimpleName();
        Log.d(TAG, TAG + " >>> " + "BaseFragment: Called onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        container.removeAllViews();
//        if (containerView != null) {
//            ((ViewGroup) containerView).removeAllViews();
//        }
//        containerView = DataBindingUtil.inflate(inflater, initFragmentLayout(), container, false).getRoot();
        containerView = inflater.inflate(initFragmentLayout(), container, false);

        TAG = getClass().getSimpleName();
        initFragmentViews(containerView);
        initFragmentViewsData();
        initFragmentActions();

        Log.d(TAG, TAG + " >>> " + "BaseFragment: Called onCreateView");
        return containerView;
    }

    @Override
    public void onFragmentBackPressed() {
        initFragmentBackPress();
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        initFragmentOnResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentUpdate(Object update) {
        initFragmentUpdate(update);
    }

    /***************************
     * Progress dialog methods *
     ***************************/
    public ProgressDialog showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_loading));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface arg0) {
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                }
            });
        }

        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

        return mProgressDialog;
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
