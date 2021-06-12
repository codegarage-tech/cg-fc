package com.rc.facecase.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rc.facecase.R;
import com.rc.facecase.adapter.CategoryListAdapter;
import com.rc.facecase.base.BaseActivity;
import com.rc.facecase.enumeration.MODE;
import com.rc.facecase.model.AppUser;
import com.rc.facecase.model.Category;
import com.rc.facecase.retrofit.APIClient;
import com.rc.facecase.retrofit.APIInterface;
import com.rc.facecase.retrofit.APIResponse;
import com.rc.facecase.util.AllConstants;
import com.rc.facecase.util.DataUtils;
import com.rc.facecase.util.Logger;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class CategoryActivity extends BaseActivity {

    private TextView tvPlayingList;
    private ImageView ivHome;
    private RecyclerView rvCategory;
    private CategoryListAdapter categoryListAdapter;

    private AppUser mAppUser;
    Category pictureCategory, musicCategory;
    //Background task
    private APIInterface mApiInterface;
    private GetCategoryListTask getCategoryListTask;

    @Override
    public String[] initActivityPermissions() {
        return new String[]{};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_category_screen;
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
        tvPlayingList = (TextView) findViewById(R.id.tv_playing_list);
//        ivPicturePlay = (ImageView) findViewById(R.id.iv_picture_play);
//        ivMusicPlay = (ImageView) findViewById(R.id.iv_music_play);
        ivHome = (ImageView) findViewById(R.id.iv_home);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);
        categoryListAdapter = new CategoryListAdapter(getApplicationContext());
        rvCategory.setNestedScrollingEnabled(false);
        rvCategory.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvCategory.setHasFixedSize(true);
        String sBeforePlaying= String.valueOf(Html.fromHtml(getResources().getString(R.string.txt_before_playing_list)));
        tvPlayingList.setText(sBeforePlaying);
        String appUserID = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_USER);
        if (!AllSettingsManager.isNullOrEmpty(appUserID)) {
            mAppUser = APIResponse.getResponseObject(appUserID, AppUser.class);
            Logger.d(TAG, TAG + " >>> " + "mAppUser: " + mAppUser.toString());
        }

        if (mAppUser != null) {
            //Check internet connection
            if (!NetworkManager.isConnected(getActivity())) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                return;
            }

            getCategoryListTask = new GetCategoryListTask(getActivity());
            getCategoryListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void initActivityActions(Bundle savedInstanceState) {
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
        if (getCategoryListTask != null && getCategoryListTask.getStatus() == AsyncTask.Status.RUNNING) {
            getCategoryListTask.cancel(true);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    private void initCategoryData(List<Category> categories) {
        if (categoryListAdapter != null) {
            categoryListAdapter.addAll(categories);
            rvCategory.setAdapter(categoryListAdapter);

            categoryListAdapter.notifyDataSetChanged();
        }
    }

    /************************
     * Server communication *
     ************************/
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
                Call<APIResponse<List<Category>>> call = mApiInterface.apiGetCategoryList(mAppUser.getId());
                Response response = call.execute();
                Logger.d(TAG, TAG + " Category>>> " + "response: " + response);
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
                dismissProgressDialog();

                if (result != null && result.isSuccessful()) {
                    Logger.d(TAG, "APIResponse(GetCategoryListTask): onResponse-server = " + result.toString());
                    APIResponse<List<Category>> data = (APIResponse<List<Category>>) result.body();
                    Logger.e("Category", data.toString() + "");

                    if (data != null && data.getSuccess().equalsIgnoreCase("1")) {
                        Logger.d(TAG, "APIResponse(GetCategoryListTask()): onResponse-object = " + data.toString());


                        if (data.getData().size() > 0) {
                            List<Category> categoryList = data.getData();
                            if (categoryList.size()>0 && categoryList !=null) {
                                initCategoryData(categoryList);
                            }

                            Logger.d(TAG, "pictureCategory onResponse= " + pictureCategory.toString());
                            Logger.d(TAG, "musicCategory onResponse= " + musicCategory.toString());
                        }
//
//                        //set count review number
//                        tvReviewCount.setText(getString(R.string.view_review_count, data.getData().size()));
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
