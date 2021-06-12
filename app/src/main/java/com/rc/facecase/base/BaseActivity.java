package com.rc.facecase.base;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.rc.facecase.R;
import com.rc.facecase.util.AppUtil;
import com.rc.facecase.util.Logger;
import com.rc.facecase.util.RuntimePermissionManager;

import java.util.ArrayList;
import java.util.List;

import static com.rc.facecase.util.RuntimePermissionManager.REQUEST_CODE_APP_INFO;
import static com.rc.facecase.util.RuntimePermissionManager.REQUEST_CODE_PERMISSION;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public abstract class BaseActivity extends AppCompatActivity {

    private BaseActivity mActivity;
    public Bundle mSavedInstanceState;
    public String TAG;
    public ProgressDialog mProgressDialog;
    private PERMISSION_TYPE mPermissionType;
    public List<String> mDefaultPermissions = new ArrayList<>();
    private List<String> mPermissions = new ArrayList<>();
    private BaseUpdateListener mRuntimePermissionUpdateListener;

    public enum PERMISSION_TYPE {ACTIVITY, TASK}

    //Abstract declaration
    public abstract String[] initActivityPermissions();

    public abstract int initActivityLayout();

    public abstract void initStatusBarView();

    public abstract void initNavigationBarView();

    public abstract void initIntentData(Bundle savedInstanceState, Intent intent);

    public abstract void initActivityViews();

    public abstract void initActivityViewsData(Bundle savedInstanceState);

    public abstract void initActivityActions(Bundle savedInstanceState);

    public abstract void initActivityOnResult(int requestCode, int resultCode, Intent data);

    public abstract void initActivityBackPress();

    public abstract void initActivityDestroyTasks();

    public abstract void initActivityPermissionResult(int requestCode, String permissions[], int[] grantResults);

    public void setRuntimePermissionUpdateListener(BaseUpdateListener runtimePermissionUpdateListener) {
        mRuntimePermissionUpdateListener = runtimePermissionUpdateListener;
    }

    public void initRuntimePermissions(PERMISSION_TYPE permissionType, String[] runtimePermissions) {
        //Check runtime permissions
        if (processRuntimePermissions(permissionType, runtimePermissions)) {
            switch (permissionType) {
                case ACTIVITY:
                    break;
                case TASK:
                    break;
            }

            //Update about runtime permission update if there any listener is assigned.
            if (mRuntimePermissionUpdateListener != null) {
                mRuntimePermissionUpdateListener.onUpdate(true);
            }
        }
    }

    public BaseActivity() {
        this.mActivity = this;
        this.TAG = getClass().getSimpleName();
        this.mDefaultPermissions = new ArrayList<String>() {{
            //For internet
            add(Manifest.permission.INTERNET);
            add(Manifest.permission.ACCESS_NETWORK_STATE);
            //For image caching
            add(Manifest.permission.READ_EXTERNAL_STORAGE);
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            //For phone state
//            add(Manifest.permission.READ_PHONE_STATE);
        }};
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSavedInstanceState = savedInstanceState;
        initIntentData(mSavedInstanceState, getIntent());
        setContentView(initActivityLayout());
        initActivityViews();
        initStatusBarView();
        initNavigationBarView();
        initActivityViewsData(mSavedInstanceState);
        initActivityActions(mSavedInstanceState);
        initRuntimePermissions(PERMISSION_TYPE.ACTIVITY, initActivityPermissions());
    }

    public Activity getActivity() {
        return mActivity;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);

            mSavedInstanceState = savedInstanceState;
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.d(TAG, "onRestoreInstanceState: " + ex.getMessage());
//            Intent intent = new Intent(this, BaseLocationActivity.class);
//            startActivity(intent);
        }
    }

    /********************
     * Fragment methods *
     ********************/
    protected void initFragment(int containerViewId, BaseFragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, tag).commitAllowingStateLoss();
    }

    protected void addFragment(int containerViewId, BaseFragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment).commitAllowingStateLoss();
    }

    protected <F extends BaseFragment> void addFragment(int containerViewId, Class<F> fragmentClazz) {
        F frg = createFragment(fragmentClazz, getIntent().getExtras());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, frg).commitAllowingStateLoss();
    }

    public static <T extends BaseFragment> T createFragment(Class<T> fragmentClazz, Bundle args) {
        T fragment = null;
        try {
            fragment = fragmentClazz.newInstance();
            fragment.setArguments(args);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fragment;
    }

    /**********************
     * Permission methods *
     **********************/
    public List<String> getPermissions() {
        return mPermissions;
    }

    private boolean processRuntimePermissions(PERMISSION_TYPE permissionType, String[] runtimePermissions) {
        //Set permission type for all type of version
        mPermissionType = permissionType;

        //Check permission for only OS above Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Logger.d(TAG, "processRuntimePermissions: build version is above lollipop");
            //Process permissions with default permissions
            mPermissions.clear();
            mPermissions.addAll(mDefaultPermissions);
            if (runtimePermissions != null && runtimePermissions.length > 0) {
                for (String permission : runtimePermissions) {
                    if (!isPermissionExist(permission)) {
                        mPermissions.add(permission);
                    }
                }
            }

            //Check runtime permissions
            List<String> ungrantedPermissions = RuntimePermissionManager.getInstance().getAllUngrantedPermissions(getActivity(), mPermissions);
            if (ungrantedPermissions.size() > 0) {
                Logger.d(TAG, "processRuntimePermissions: Found ungranted permission: " + ungrantedPermissions.size());

                //Check if runtime request is shown before
                if (!RuntimePermissionManager.getInstance().isPermissionRequested(ungrantedPermissions)) {
                    //First time user is calling runtime request
                    Logger.d(TAG, "processRuntimePermissions: Permission request is not shown before");
                    RuntimePermissionManager.getInstance().requestRuntimePermission(getActivity(), ungrantedPermissions);
                    return false;
                } else {
                    //User already requested runtime permission before
                    Logger.d(TAG, "processRuntimePermissions: Permission request is already shown before");

                    //Check all denied permissions
                    List<String> deniedPermissions = RuntimePermissionManager.getInstance().getAllDeniedPermissions(getActivity(), ungrantedPermissions);
                    if (deniedPermissions.size() > 0) {
                        //Request runtime permissions
                        Logger.d(TAG, "processRuntimePermissions: Found denied permissions " + deniedPermissions.size());
                        RuntimePermissionManager.getInstance().requestRuntimePermission(getActivity(), deniedPermissions);
                        return false;
                    } else {
                        //There is no denied permission
                        Logger.d(TAG, "processRuntimePermissions: There are no denied permissions");

                        //Check if there any never asked permissions
                        List<String> neverAskedPermissions = RuntimePermissionManager.getInstance().getAllNeverAskedPermissions(getActivity(), ungrantedPermissions);
                        if (neverAskedPermissions.size() > 0) {
                            Logger.d(TAG, "processRuntimePermissions: There are \"never asked\" permissions " + neverAskedPermissions.size());

                            //Filter permission group for showing dialog message
                            List<String> filteredPermission = new ArrayList<>();
                            for (String permission : neverAskedPermissions) {
                                Logger.d(TAG, "processRuntimePermissions: needed - " + permission);

                                //Get permission name from that permission which has underscore
                                String[] underScoredPermission = permission.split("_");
                                String permissionName = "";
                                if (underScoredPermission.length == 1) {
                                    //Get permission name from that permission which has dot
                                    String[] dotPermission = permission.split("\\.");
                                    if (dotPermission.length > 0) {
                                        permissionName = dotPermission[dotPermission.length - 1];
                                    }
                                } else if (underScoredPermission.length > 1) {
                                    permissionName = underScoredPermission[underScoredPermission.length - 1];
                                }

                                //Store permission name
                                if (!TextUtils.isEmpty(permissionName)) {
                                    Logger.d(TAG, "processRuntimePermissions: filtered - " + permissionName);
                                    if (!filteredPermission.contains(permissionName)) {
                                        filteredPermission.add(permissionName);
                                        Logger.d(TAG, "processRuntimePermissions: added- " + permissionName);
                                    } else {
                                        Logger.d(TAG, "processRuntimePermissions: already exist - " + permissionName);
                                    }
                                }
                            }
                            Logger.d(TAG, "processRuntimePermissions: total filtered permission - " + filteredPermission.size());

                            //Build never asked permissions message
                            String message = "You have checked \"Don't ask again\" for below permissions-\n";
                            for (int i = 0; i < filteredPermission.size(); i++) {
                                message = message + "\n" + (i + 1) + ") " + AppUtil.convertToCamelCase(filteredPermission.get(i));
                            }
                            message = message + "\n\nTo make the app working properly you need to grant above permissions from \"App info\".";

                            //Show open app settings dialog
                            OpenAppSettingsDialog openAppSettingsDialog = new OpenAppSettingsDialog(getActivity(), message, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            RuntimePermissionManager.getInstance().openAppInfo(getActivity(), REQUEST_CODE_APP_INFO);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                        case DialogInterface.BUTTON_NEUTRAL:
                                            break;
                                    }
                                }
                            });
                            openAppSettingsDialog.initView().show();
                            return false;
                        } else {
                            Logger.d(TAG, "processRuntimePermissions: There are no \"never asked\" permissions");
                        }
                    }
                }
            } else {
                Logger.d(TAG, "processRuntimePermissions: All permissiona are granted");
                //If all permissions are granted
                return true;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        initActivityPermissionResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                //Sync runtime permission with application settings
                RuntimePermissionManager.getInstance().syncPermissionsWithAppInfo(getActivity(), AppUtil.convertToArrayList(permissions));

                //Check further task for permission result
                if (RuntimePermissionManager.getInstance().isAllPermissionsGranted(getActivity(), permissions)) {
                    switch (mPermissionType) {
                        case ACTIVITY:
                            break;
                        case TASK:
                            break;
                    }

                    //Update about runtime permission update if there any listener assigned.
                    if (mRuntimePermissionUpdateListener != null) {
                        mRuntimePermissionUpdateListener.onUpdate(true);
                    }
                }
                break;
        }
    }

    /******************************************
     * Click listener for checking permission *
     ******************************************/
    public abstract class OnBaseClickListener implements View.OnClickListener {

        //Abstract declaration
        public abstract void OnPermissionValidation(View view);

        private static final String TAG = "OnBaseClickListener";
        private static final long MIN_DELAY_MS = 1500L;
        private long mLastClickTime = 0;

        public OnBaseClickListener() {
        }

        @Override
        public final void onClick(View view) {
            long lastClickTime = mLastClickTime;
            long now = System.currentTimeMillis();
            this.mLastClickTime = now;
            if (now - lastClickTime < MIN_DELAY_MS) {
                Toast.makeText(getActivity(), getString(R.string.toast_ignored_fast_click), Toast.LENGTH_SHORT).show();
            } else {
                //Check runtime permissions
                if (processRuntimePermissions(PERMISSION_TYPE.TASK, initActivityPermissions())) {
                    switch (mPermissionType) {
                        case TASK:
//                            if (getActivity() instanceof BaseLocationActivity && !isGpsEnabled(getActivity())) {
//                                Logger.d(TAG, "Permissions: All permissions are granted but location is disabled");
//                                //Request for enabling gps from base location activity
//                                if (mRuntimePermissionUpdateListener != null) {
//                                    mRuntimePermissionUpdateListener.onUpdate(true);
//                                }
//                            } else {
                                //Now it's the time to disco
                                Logger.d(TAG, "Permissions: All permissions are granted, now it's the time to disco!");
                                OnPermissionValidation(view);
//                            }
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initActivityOnResult(requestCode, resultCode, data);

        //Process runtime permissions
        switch (requestCode) {
            case REQUEST_CODE_APP_INFO:
                //Sync runtime permission with application settings
                RuntimePermissionManager.getInstance().syncPermissionsWithAppInfo(getActivity(), mPermissions);
                break;
        }

        //send on activity result event to the fragment
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).onFragmentResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        initActivityBackPress();

        //send back press event to the fragment
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).onFragmentBackPressed();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        initActivityDestroyTasks();
    }

    public String[] getPermissions(List<String> permissions) {
        String permissionsList[] = new String[]{};
        if (permissions != null && permissions.size() > 0) {
            for (int i = 0; i < permissions.size(); i++) {
                permissionsList[i] = permissions.get(i);
            }
        }
        return permissionsList;
    }

    public boolean isPermissionExist(String permission) {
        for (String mPermission : mDefaultPermissions) {
            if (permission.equalsIgnoreCase(mPermission)) {
                return true;
            }
        }

        return false;
    }

    public boolean isGpsEnabled(Context context) {
        boolean isGpsEnabled = false;
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException ex) {
                ex.printStackTrace();
            }
            isGpsEnabled = locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            isGpsEnabled = !TextUtils.isEmpty(locationProviders);
        }
        Logger.d(TAG, "SelfHealingLocationManager: gps enable " + isGpsEnabled);
        return isGpsEnabled;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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