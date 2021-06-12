package com.rc.facecase.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.rc.facecase.R;
import com.rc.facecase.adapter.AdditionalSubCategoryListAdapter;
import com.rc.facecase.base.BaseActivity;
import com.rc.facecase.base.BaseUpdateListener;
import com.rc.facecase.decoration.ItemOffsetDecoration;
import com.rc.facecase.model.AppUser;
import com.rc.facecase.model.Category;
import com.rc.facecase.model.ParamsAppUser;
import com.rc.facecase.model.SubCategory;
import com.rc.facecase.retrofit.APIClient;
import com.rc.facecase.retrofit.APIInterface;
import com.rc.facecase.retrofit.APIResponse;
import com.rc.facecase.util.AllConstants;
import com.rc.facecase.util.AppUtil;
import com.rc.facecase.util.Logger;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class AdditionalCategoryActivity extends BaseActivity {

    private ImageView ivHome, ivBack;
    private RecyclerView rvCategory;
    private AdditionalSubCategoryListAdapter additionalSubCategoryListAdapter;

    private AppUser mAppUser;
    //Background task
    private APIInterface mApiInterface;
    private RegisterAppUserTask registerAppUserTask;
    private GetCategoryListTask getCategoryListTask;
    private String deviceUniqueIdentity = "";

    @Override
    public String[] initActivityPermissions() {
        return new String[]{Manifest.permission.READ_PHONE_STATE};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_additonal_category_screen;
    }

    @Override
    public void initStatusBarView() {

    }

    @Override
    public void initNavigationBarView() {

    }

    @Override
    public void initIntentData(Bundle savedInstanceState, Intent intent) {

    }

    @Override
    public void initActivityViews() {
        rvCategory = (RecyclerView) findViewById(R.id.rv_category_list);
        ivHome = (ImageView) findViewById(R.id.iv_home);
        ivBack = (ImageView) findViewById(R.id.iv_back);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);
        additionalSubCategoryListAdapter = new AdditionalSubCategoryListAdapter(getApplicationContext());
        rvCategory.setNestedScrollingEnabled(false);
        rvCategory.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        rvCategory.setHasFixedSize(true);
        // For spacing among items
        // rvSubCategory.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.HORIZONTAL));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.dp_2_5);
        rvCategory.addItemDecoration(itemDecoration);
        rvCategory.scrollToPosition(0);

        setRuntimePermissionUpdateListener(new BaseUpdateListener() {
            @Override
            public void onUpdate(Object... update) {
                if ((Boolean) update[0]) {
                    if (loadFaceCaseData()) {
                        getCategoryListTask = new GetCategoryListTask(getActivity());
                        getCategoryListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            }
        });

//        String appUserID = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_USER);
//        if (!AllSettingsManager.isNullOrEmpty(appUserID)) {
//            mAppUser = APIResponse.getResponseObject(appUserID, AppUser.class);
//            Logger.d(TAG, TAG + " >>> " + "mAppUser: " + mAppUser.toString());
//        }
//
//        if (mAppUser != null) {
//            //Check internet connection
//            if (!NetworkManager.isConnected(getActivity())) {
//                Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            getCategoryListTask = new GetCategoryListTask(getActivity());
//            getCategoryListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }
    }

    @Override
    public void initActivityActions(Bundle savedInstanceState) {
        ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                initActivityBackPress();
            }
        });
        ivHome.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                initActivityBackPress();
            }
        });
    }

    @Override
    public void initActivityOnResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void initActivityBackPress() {
        finish();
    }

    @Override
    public void initActivityDestroyTasks() {
        dismissProgressDialog();
        if (registerAppUserTask != null && registerAppUserTask.getStatus() == AsyncTask.Status.RUNNING) {
            registerAppUserTask.cancel(true);
        }
        if (getCategoryListTask != null && getCategoryListTask.getStatus() == AsyncTask.Status.RUNNING) {
            getCategoryListTask.cancel(true);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    private void initCategoryData(List<SubCategory> subCategoryList) {
        if (additionalSubCategoryListAdapter != null) {
            additionalSubCategoryListAdapter.addAll(subCategoryList);
            rvCategory.setAdapter(additionalSubCategoryListAdapter);
            additionalSubCategoryListAdapter.notifyDataSetChanged();
        }
    }

    private boolean loadFaceCaseData() {
        deviceUniqueIdentity = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_DEVICE_IDENTIFIER);
        if (AllSettingsManager.isNullOrEmpty(deviceUniqueIdentity)) {
            deviceUniqueIdentity = AppUtil.getAppDeviceUniqueIdentifier(getActivity());

            if (AllSettingsManager.isNullOrEmpty(deviceUniqueIdentity)) {
                Toast.makeText(getActivity(), getString(R.string.toast_could_not_retrieve_device_info), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        Logger.d(TAG, TAG + " >>> " + "AppUser(deviceUniqueIdentity): " + deviceUniqueIdentity);

        String appUserID = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_USER);
        if (!AllSettingsManager.isNullOrEmpty(appUserID)) {
            mAppUser = APIResponse.getResponseObject(appUserID, AppUser.class);
            Logger.d(TAG, TAG + " >>> " + "mAppUser: " + mAppUser.toString());
        }
        if (mAppUser == null) {
            //Check internet connection
            if (!NetworkManager.isConnected(getActivity())) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                return false;
            }

            //Register app user
            ParamsAppUser paramAppUser = new ParamsAppUser(deviceUniqueIdentity);
            registerAppUserTask = new RegisterAppUserTask(getActivity(), paramAppUser);
            registerAppUserTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return false;
        }

        return true;
    }

    /************************
     * Server communication *
     ************************/
    private class RegisterAppUserTask extends AsyncTask<String, Integer, Response> {

        Context mContext;
        ParamsAppUser mParamAppUser;

        private RegisterAppUserTask(Context context, ParamsAppUser paramAppUser) {
            mContext = context;
            mParamAppUser = paramAppUser;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Logger.d(TAG, TAG + " >>> " + "AppUser(home-param): " + mParamAppUser.toString());
                Call<APIResponse<List<AppUser>>> call = mApiInterface.apiAddUser(mParamAppUser);

                Response response = call.execute();
                Logger.d(TAG, TAG + " >>> " + "response: " + response);
                if (response.isSuccessful()) {
                    return response;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Response result) {
            try {
                if (result != null && result.isSuccessful()) {
                    Logger.d(TAG, TAG + " >>> " + "APIResponse(DoCreateUser): onResponse-server = " + result.toString());
                    APIResponse<List<AppUser>> data = (APIResponse<List<AppUser>>) result.body();
                    Logger.d(TAG, TAG + " >>> " + "data: " + data.toString() + "");

                    if (data != null && data.getStatus().equalsIgnoreCase("1")) {
                        Logger.d(TAG, TAG + " >>> " + "APIResponse(DoCreateUser()): onResponse-object = " + data.toString());

                        //Save data into session
                        String jsonAppUser = APIResponse.getResponseString(data.getData().get(0));
                        Logger.d(TAG, TAG + " >>> " + "APIResponse(DoCreateUser()): app-user = " + jsonAppUser);
                        SessionManager.setStringSetting(getActivity(), AllConstants.SESSION_KEY_USER, jsonAppUser);

                        // Get saved user
                        String appUserID = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_USER);
                        if (!AllSettingsManager.isNullOrEmpty(appUserID)) {
                            mAppUser = APIResponse.getResponseObject(appUserID, AppUser.class);
                            Logger.d(TAG, TAG + " >>> " + "mAppUser: " + mAppUser.toString());
                        }

                        // Request for additional category
                        if (mAppUser != null) {
                            getCategoryListTask = new GetCategoryListTask(getActivity());
                            getCategoryListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception exception) {
                Logger.d(TAG, TAG + " >>> exception: " + exception.getMessage());
            }
        }
    }

    private class GetCategoryListTask extends AsyncTask<String, Integer, Response> {

        Context mContext;

        public GetCategoryListTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Call<APIResponse<List<Category>>> call = mApiInterface.apiGetAdditionalCategoryList(mAppUser.getId());
                Response response = call.execute();
                Logger.d(TAG, TAG + " Category>>> " + "response: " + response);
                if (response.isSuccessful()) {
                    return response;
                }
            } catch (Exception ex) {
                Logger.d(TAG, TAG + " Category>>> exception: " + ex.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Response result) {
            try {
                dismissProgressDialog();

                if (result != null && result.isSuccessful()) {
                    Logger.d(TAG, "APIResponse(GetCategoryListTask): onResponse-server = " + result.toString());
                    APIResponse<List<Category>> data = (APIResponse<List<Category>>) result.body();
                    Logger.e("Category", data.toString() + "");

                    if (data != null && data.getSuccess().equalsIgnoreCase("1")) {
                        Logger.d(TAG, "APIResponse(GetCategoryListTask()): onResponse-object = " + data.toString());

                        if (data.getData().size() > 0) {
                            List<SubCategory> additionalCategory = data.getData().get(0).getSub_categories();
                            if (additionalCategory.size() > 0 && additionalCategory != null) {
                                initCategoryData(additionalCategory);
                            }
                            Logger.d(TAG, "additionalCategory onResponse= " + additionalCategory.toString());
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }


}
