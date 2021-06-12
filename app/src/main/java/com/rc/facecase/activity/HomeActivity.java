package com.rc.facecase.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.rc.facecase.R;
import com.rc.facecase.base.BaseActivity;
import com.rc.facecase.base.BaseUpdateListener;
import com.rc.facecase.model.AppUser;
import com.rc.facecase.model.ParamsAppUser;
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

import cn.ymex.popup.controller.AlertController;
import cn.ymex.popup.dialog.PopupDialog;
import retrofit2.Call;
import retrofit2.Response;

import static com.rc.facecase.util.AllConstants.KEY_INTENT_EXTRA_HOME;

public class HomeActivity extends BaseActivity {

    private ImageView ivPlay, ivQuit, ivIntroVideo,ivWatchSample;
    //Background task
    private APIInterface mApiInterface;
    private RegisterAppUserTask registerAppUserTask;
    private AppUser mAppUser;
    private String deviceUniqueIdentity = "";

    @Override
    public String[] initActivityPermissions() {
        return new String[]{Manifest.permission.READ_PHONE_STATE};

    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_home;
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
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivQuit = (ImageView) findViewById(R.id.iv_quit);
        ivIntroVideo = (ImageView) findViewById(R.id.iv_intro_video);
        ivWatchSample = (ImageView) findViewById(R.id.iv_watch_sample);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        setRuntimePermissionUpdateListener(new BaseUpdateListener() {
            @Override
            public void onUpdate(Object... update) {
                if ((Boolean) update[0]) {
                    loadFaceCaseData();
                }
            }
        });
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

    @Override
    public void initActivityActions(Bundle savedInstanceState) {
        ivPlay.setOnClickListener(
                new OnBaseClickListener() {
                    @Override
                    public void OnPermissionValidation(View view) {
                        if (loadFaceCaseData()) {
                            Intent iFacePlay = new Intent(getActivity(), CategoryActivity.class);
                            startActivity(iFacePlay);
                        }
                    }
                }
        );
        ivQuit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirm")
                        .setMessage("Exit Facecase?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    finish();
                                }
                            }
                        }).setNegativeButton("Cancel", null).show();

//                showCloseAppDialog("Confirm", "Exit Facecase?");
            }
        });
        ivIntroVideo.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intent = new Intent(getActivity(), IntroductionActivity.class);
                intent.putExtra(KEY_INTENT_EXTRA_HOME, true);
                startActivity(intent);
                finish();
            }
        });

        ivWatchSample.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intent = new Intent(getActivity(), WatchSampleActivity.class);
                intent.putExtra(KEY_INTENT_EXTRA_HOME, true);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showCloseAppDialog(String title, String message) {
        PopupDialog.create(getActivity())
                .outsideTouchHide(false)
                .dismissTime(1000 * 5)
                .controller(AlertController.build()
                        .title(title + "\n")
                        .message(message)
                        .clickDismiss(true)
                        .negativeButton(getString(R.string.dialog_cancel), null)
                        .positiveButton(getString(R.string.dialog_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }))
                .show();
    }

    @Override
    public void initActivityOnResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void initActivityBackPress() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirm")
                .setMessage("Exit Facecase?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            finish();
                        }
                    }
                }).setNegativeButton("Cancel", null).show();
    }

    @Override
    public void initActivityDestroyTasks() {
        dismissProgressDialog();
        if (registerAppUserTask != null && registerAppUserTask.getStatus() == AsyncTask.Status.RUNNING) {
            registerAppUserTask.cancel(true);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

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
                dismissProgressDialog();

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
