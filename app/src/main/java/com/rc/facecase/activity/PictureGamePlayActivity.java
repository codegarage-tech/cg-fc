package com.rc.facecase.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.rc.facecase.R;
import com.rc.facecase.base.BaseActivity;
import com.rc.facecase.enumeration.CategoryType;
import com.rc.facecase.enumeration.MODE;
import com.rc.facecase.model.AppUser;
import com.rc.facecase.model.Items;
import com.rc.facecase.model.ParamsUpdateUserHistory;
import com.rc.facecase.retrofit.APIClient;
import com.rc.facecase.retrofit.APIInterface;
import com.rc.facecase.retrofit.APIResponse;
import com.rc.facecase.service.MediaPlayingService;
import com.rc.facecase.util.AllConstants;
import com.rc.facecase.util.AppUtil;
import com.rc.facecase.util.Logger;
import com.rc.facecase.util.RandomFlashManager;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;

import org.parceler.Parcels;

import retrofit2.Call;
import retrofit2.Response;

import static com.rc.facecase.enumeration.CategoryType.PICTURE_TO_CATEGORY;
import static com.rc.facecase.util.AllConstants.INTENT_KEY_CATEGORY_TYPE;
import static com.rc.facecase.util.AllConstants.SUB_CATEGORY_NAME;
import static com.rc.facecase.util.AppUtil.isServiceRunning;

public class PictureGamePlayActivity extends BaseActivity {

    private final long firstPlayTime = 8 * 1000, secondPlayTime = 12 * 1000;
    private final long interval = 1000;
    private TextView tvCount, tvTitle, tvAnswer;
    private ImageView ivBack, ivHome, ivLoading, ivAnswer, ivAnswer11sec, ivPlay11Sec, ivAnswer7sec, ivFlashImage;
    private RelativeLayout rlGameMode, rlPlayAgainMode, rlAskAnswerMode, rlAnswerMode;
    private LinearLayout linTakeAbout30Second;
    //    private ShapeRipple shapeRipple;
    RandomFlashManager randomFlashManager;
    private String subCategoryName = "";
    private AppUser mAppUser;
    private Items items;
    private Bitmap bitmapOriginal, bitmapScaled;
    private CategoryType mCategoryType;

    //Background task
    private APIInterface mApiInterface;
    private UpdateUserHistoryTask updateUserHistoryTask;
    private MediaPlayer mp;

    @Override
    public String[] initActivityPermissions() {
        return new String[]{};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_game_play_screen;

    }

    @Override
    public void initStatusBarView() {
    }

    @Override
    public void initNavigationBarView() {

    }

    @Override
    public void initIntentData(Bundle savedInstanceState, Intent intent) {
        if (intent != null) {
            subCategoryName = getIntent().getExtras().getString(SUB_CATEGORY_NAME);
            String mParcelableCategoryType = intent.getStringExtra(INTENT_KEY_CATEGORY_TYPE);
            if (!AllSettingsManager.isNullOrEmpty(mParcelableCategoryType)) {
                mCategoryType = CategoryType.valueOf(mParcelableCategoryType);
                Logger.d(TAG, TAG + " >>> " + "mCategoryType: " + mParcelableCategoryType);
            }
            Parcelable mParcelableItem = intent.getParcelableExtra(AllConstants.INTENT_KEY_ITEM);
            if (mParcelableItem != null) {
                items = Parcels.unwrap(mParcelableItem);

                Logger.d(TAG, TAG + " >>> " + "mParcelableItem: " + mParcelableItem.toString());
            }
        }
    }

    @Override
    public void initActivityViews() {
//        shapeRipple = (ShapeRipple) findViewById(R.id.shape_ripple);
//        shapeRipple.setVisibility(View.GONE);
        ivFlashImage = (ImageView) findViewById(R.id.iv_flash_image);
        ivFlashImage.setVisibility(View.GONE);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivHome = (ImageView) findViewById(R.id.iv_home);
        ivLoading = (ImageView) findViewById(R.id.iv_loading);
        ivAnswer11sec = (ImageView) findViewById(R.id.iv_answer_11sec);
        ivPlay11Sec = (ImageView) findViewById(R.id.iv_play_11sec);
        ivAnswer7sec = (ImageView) findViewById(R.id.iv_answer_7sec);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvAnswer = (TextView) findViewById(R.id.tv_answer);
        ivAnswer = (ImageView) findViewById(R.id.iv_answer);
        tvCount = (TextView) findViewById(R.id.tv_count);
        rlGameMode = (RelativeLayout) findViewById(R.id.rl_game_mode);
        rlPlayAgainMode = (RelativeLayout) findViewById(R.id.rl_play_again_mode);
        rlAskAnswerMode = (RelativeLayout) findViewById(R.id.rl_ask_answer_mode);
        rlAnswerMode = (RelativeLayout) findViewById(R.id.rl_answer_mode);
        linTakeAbout30Second = (LinearLayout) findViewById(R.id.lin_take_about_30second);

        initModeView(MODE.GAME);
        initCheckCategoryView();
    }

    private void initCheckCategoryView() {
        if (mCategoryType != null) {
            switch (mCategoryType) {
                case PICTURE_TO_CATEGORY:
                    ivPlay11Sec.setBackgroundResource(R.drawable.ic_play11sec1);
                    linTakeAbout30Second.setVisibility(View.VISIBLE);
                    break;
                case ADDITIONAL_TO_CATEGORY:
                    ivPlay11Sec.setBackgroundResource(R.drawable.ic_buy_this_category);
                    linTakeAbout30Second.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void initModeView(MODE mode) {
        switch (mode) {
            case GAME:
                rlGameMode.setVisibility(View.VISIBLE);
                rlPlayAgainMode.setVisibility(View.GONE);
                rlAskAnswerMode.setVisibility(View.GONE);
                rlAnswerMode.setVisibility(View.GONE);
                ivLoading.setVisibility(View.VISIBLE);
                ivFlashImage.setVisibility(View.VISIBLE);
                break;
            case PLAY_AGAIN:
                rlGameMode.setVisibility(View.GONE);
                rlPlayAgainMode.setVisibility(View.VISIBLE);
                rlAskAnswerMode.setVisibility(View.GONE);
                rlAnswerMode.setVisibility(View.GONE);
                ivLoading.setVisibility(View.GONE);
                ivFlashImage.setVisibility(View.GONE);
                break;
            case ASK_ANSWER:
                rlGameMode.setVisibility(View.GONE);
                rlPlayAgainMode.setVisibility(View.GONE);
                rlAskAnswerMode.setVisibility(View.VISIBLE);
                rlAnswerMode.setVisibility(View.GONE);
                ivLoading.setVisibility(View.GONE);
                ivFlashImage.setVisibility(View.GONE);
                break;
            case ANSWER:
                rlGameMode.setVisibility(View.GONE);
                rlPlayAgainMode.setVisibility(View.GONE);
                rlAskAnswerMode.setVisibility(View.GONE);
                rlAnswerMode.setVisibility(View.VISIBLE);
                ivLoading.setVisibility(View.GONE);
                ivFlashImage.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);
        tvTitle.setText(subCategoryName);
        //  playingMusic();
        if (items != null) {
            tvAnswer.setText(items.getTitle());
        }
        String appUserID = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_USER);
        if (!AllSettingsManager.isNullOrEmpty(appUserID)) {
            mAppUser = APIResponse.getResponseObject(appUserID, AppUser.class);
            Logger.d(TAG, TAG + " >>> " + "mAppUser: " + mAppUser.toString());
        }
        try {
            // Load
            Glide.with(PictureGamePlayActivity.this)
                    .asGif()
                    .load(R.drawable.gif_loading)
                    .into(ivLoading);

//            AppUtil.loadImage(getApplicationContext(), ivAnswer, items.getSource(), false, false, false);

            Glide
                    .with(PictureGamePlayActivity.this)
                    .asBitmap()
                    .load(items.getSource())
//                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/9/92/Neymar_PSG.jpg/220px-Neymar_PSG.jpg")
//                    .load("https://www.thesun.co.uk/wp-content/uploads/2019/07/NINTCHDBPICT000434076580.jpg")
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(final Bitmap bitmap, Transition<? super Bitmap> transition) {
                            if (bitmap != null) {
                                bitmapOriginal = bitmap;
                                Logger.d(TAG, "bitmap>>original>> height: " + bitmapOriginal.getHeight() + " width: " + bitmapOriginal.getWidth());

                                // Find optimal size of image
                                int optimizedWidth = 0, optimizedHeight = 0;
                                if (bitmapOriginal.getWidth() > bitmapOriginal.getHeight()) {
                                    Logger.d(TAG, "bitmap>> It's landscape image");

                                    // Fix for different devices
                                    int tabletSize = AppUtil.getTabletSize(getActivity());
                                    Logger.d(TAG, "bitmap>> Tablet size: " + tabletSize);
                                    if (tabletSize == 10) {
                                        optimizedWidth = (int) AppUtil.dpToPixel(getActivity(), 330);
                                        optimizedHeight = (int) AppUtil.dpToPixel(getActivity(), 280);
//                                        bitmapScaled = Bitmap.createScaledBitmap(bitmapOriginal, (int) AppUtil.dpToPixel(getActivity(), 330), (int) AppUtil.dpToPixel(getActivity(), 280), true);
                                    } else if (tabletSize == 7) {
                                        optimizedWidth = (int) AppUtil.dpToPixel(getActivity(), 230);
                                        optimizedHeight = (int) AppUtil.dpToPixel(getActivity(), 200);
//                                        bitmapScaled = Bitmap.createScaledBitmap(bitmapOriginal, (int) AppUtil.dpToPixel(getActivity(), 230), (int) AppUtil.dpToPixel(getActivity(), 200), true);
                                    } else {
                                        optimizedWidth = (int) AppUtil.dpToPixel(getActivity(), 180);
                                        optimizedHeight = (int) AppUtil.dpToPixel(getActivity(), 130);
//                                        bitmapScaled = Bitmap.createScaledBitmap(bitmapOriginal, (int) AppUtil.dpToPixel(getActivity(), 180), (int) AppUtil.dpToPixel(getActivity(), 130), true);
                                    }
                                } else {
                                    Logger.d(TAG, "bitmap>> It's portrait image");

                                    // Fix for different devices
                                    int tabletSize = AppUtil.getTabletSize(getActivity());
                                    Logger.d(TAG, "bitmap>> Tablet size: " + tabletSize);
                                    if (tabletSize == 10) {
                                        optimizedWidth = (int) AppUtil.dpToPixel(getActivity(), 280);
                                        optimizedHeight = (int) AppUtil.dpToPixel(getActivity(), 330);
//                                        bitmapScaled = Bitmap.createScaledBitmap(bitmapOriginal, (int) AppUtil.dpToPixel(getActivity(), 280), (int) AppUtil.dpToPixel(getActivity(), 330), true);
                                    } else if (tabletSize == 7) {
                                        optimizedWidth = (int) AppUtil.dpToPixel(getActivity(), 200);
                                        optimizedHeight = (int) AppUtil.dpToPixel(getActivity(), 230);
//                                        bitmapScaled = Bitmap.createScaledBitmap(bitmapOriginal, (int) AppUtil.dpToPixel(getActivity(), 200), (int) AppUtil.dpToPixel(getActivity(), 230), true);
                                    } else {
                                        optimizedWidth = (int) AppUtil.dpToPixel(getActivity(), 130);
                                        optimizedHeight = (int) AppUtil.dpToPixel(getActivity(), 180);
//                                        bitmapScaled = Bitmap.createScaledBitmap(bitmapOriginal, (int) AppUtil.dpToPixel(getActivity(), 130), (int) AppUtil.dpToPixel(getActivity(), 180), true);
                                    }
                                }

                                // Use optimal size to load scaled bitmap
                                Glide
                                        .with(PictureGamePlayActivity.this)
                                        .asBitmap()
                                        .load(items.getSource())
                                        .override(optimizedWidth, optimizedHeight)
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                if (resource != null) {
                                                    bitmapScaled = resource;
                                                    Logger.d(TAG, "bitmap>>scaled>> height: " + bitmapScaled.getHeight() + " width: " + bitmapScaled.getWidth());

                                                    //you can use loaded bitmap here
                                                    ivFlashImage.setImageBitmap(bitmapScaled);
                                                    ivFlashImage.setVisibility(View.VISIBLE);
                                                    ivLoading.setVisibility(View.GONE);

                                                    Intent intentMediaService = new Intent(PictureGamePlayActivity.this, MediaPlayingService.class);
                                                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_START);
//                                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC, Parcels.wrap(items));
                                                    intentMediaService.putExtra(AllConstants.KEY_INTENT_BACKGROUND_MUSIC_SET, AllConstants.BACKGROUND_MUSIC_TIMER_SET);
                                                    startService(intentMediaService);

                                                    randomFlashManager = new RandomFlashManager(getActivity(), ivFlashImage, firstPlayTime, interval, new RandomFlashManager.RandomFlashListener() {
                                                        @Override
                                                        public void onTick(long millisUntilFinished) {
                                                            Logger.d(TAG, TAG + " >>> " + "leftSeconds>>>: " + millisUntilFinished / interval);
                                                            tvCount.setText("" + millisUntilFinished / interval);
                                                        }

                                                        @Override
                                                        public void onFinish() {
                                                            if (isServiceRunning(PictureGamePlayActivity.this, MediaPlayingService.class)) {
                                                                Intent intentMediaService = new Intent(getActivity(), MediaPlayingService.class);
                                                                intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                                                                stopService(intentMediaService);
                                                            }
//                                    shapeRipple.stopRipple();
                                                            randomFlashManager.destroyFlashing();
                                                            initModeView(MODE.PLAY_AGAIN);
                                                        }
                                                    });
                                                    randomFlashManager.startFlashing();

                                                    // Set answer image
                                                    ivAnswer.setImageBitmap(bitmapScaled);
                                                }
                                            }
                                        });
                            }
                        }
                    });
        } catch (Exception ex) {
            Logger.d(TAG, "Exception: " + ex.getMessage());
        }

        //Check internet connection
        if (!NetworkManager.isConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
            //  stopPlaying();

        } else {
            if (items != null) {
                //Update User History
                ParamsUpdateUserHistory paramUpdateUserHistory = new ParamsUpdateUserHistory(mAppUser.getId(), items.getId());
                updateUserHistoryTask = new UpdateUserHistoryTask(getActivity(), paramUpdateUserHistory);
                updateUserHistoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    @Override
    public void initActivityActions(Bundle savedInstanceState) {

        ivHome.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (isServiceRunning(PictureGamePlayActivity.this, MediaPlayingService.class)) {
                    Intent intentMediaService = new Intent(getActivity(), MediaPlayingService.class);
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                    stopService(intentMediaService);
                }
                //   stopPlaying();
                Intent iFacePlay = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(iFacePlay);
                initActivityBackPress();
            }
        });

        ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (isServiceRunning(PictureGamePlayActivity.this, MediaPlayingService.class)) {
                    Intent intentMediaService = new Intent(getActivity(), MediaPlayingService.class);
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                    stopService(intentMediaService);
                }
                // stopPlaying();
                initActivityBackPress();
            }
        });

        ivAnswer7sec.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intentMediaService = new Intent(PictureGamePlayActivity.this, MediaPlayingService.class);
                intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_START);
                //   intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC, Parcels.wrap(items));
                intentMediaService.putExtra(AllConstants.KEY_INTENT_BACKGROUND_MUSIC_SET, AllConstants.BACKGROUND_MUSIC_TADA_SET);
                startService(intentMediaService);

                initModeView(MODE.ANSWER);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        if (isServiceRunning(PictureGamePlayActivity.this, MediaPlayingService.class)) {
                            Intent intentMediaServiceStop = new Intent(PictureGamePlayActivity.this, MediaPlayingService.class);
                            intentMediaServiceStop.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                            stopService(intentMediaServiceStop);
                        }
                    }
                }, 1500);


            }
        });

        ivAnswer11sec.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (isServiceRunning(PictureGamePlayActivity.this, MediaPlayingService.class)) {
                    Intent intentMediaServiceStop = new Intent(getActivity(), MediaPlayingService.class);
                    intentMediaServiceStop.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                    stopService(intentMediaServiceStop);
                }

                Intent intentMediaService = new Intent(PictureGamePlayActivity.this, MediaPlayingService.class);
                intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_START);
                //   intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC, Parcels.wrap(items));
                intentMediaService.putExtra(AllConstants.KEY_INTENT_BACKGROUND_MUSIC_SET, AllConstants.BACKGROUND_MUSIC_TADA_SET);
                startService(intentMediaService);

                initModeView(MODE.ANSWER);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        if (isServiceRunning(PictureGamePlayActivity.this, MediaPlayingService.class)) {
                            Intent intentMediaServiceStop = new Intent(PictureGamePlayActivity.this, MediaPlayingService.class);
                            intentMediaServiceStop.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                            stopService(intentMediaServiceStop);
                        }
                    }
                }, 1500);


            }
        });

        ivPlay11Sec.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (isServiceRunning(PictureGamePlayActivity.this, MediaPlayingService.class)) {
                    Intent intentMediaServiceStop = new Intent(PictureGamePlayActivity.this, MediaPlayingService.class);
                    intentMediaServiceStop.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                    stopService(intentMediaServiceStop);
                }

                if (mCategoryType == PICTURE_TO_CATEGORY) {
                    Intent intentMediaService = new Intent(PictureGamePlayActivity.this, MediaPlayingService.class);
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_START);
                    //   intentMediaService.putExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC, Parcels.wrap(items));
                    intentMediaService.putExtra(AllConstants.KEY_INTENT_BACKGROUND_MUSIC_SET, AllConstants.BACKGROUND_MUSIC_TIMER_SET);
                    startService(intentMediaService);

//                shapeRipple.startRipple();
                    initModeView(MODE.GAME);
                    ivLoading.setVisibility(View.GONE);

                    randomFlashManager = new RandomFlashManager(getActivity(), ivFlashImage, secondPlayTime, interval, new RandomFlashManager.RandomFlashListener() {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            Logger.d(TAG, TAG + " >>> " + "leftSeconds>>>: " + millisUntilFinished / interval);
                            tvCount.setText("" + millisUntilFinished / interval);
                        }

                        @Override
                        public void onFinish() {
                            if (isServiceRunning(PictureGamePlayActivity.this, MediaPlayingService.class)) {
                                Intent intentMediaServiceStop = new Intent(getActivity(), MediaPlayingService.class);
                                intentMediaServiceStop.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                                stopService(intentMediaServiceStop);
                            }

//                        shapeRipple.stopRipple();
                            randomFlashManager.destroyFlashing();
                            initModeView(MODE.ASK_ANSWER);
                        }
                    });
                    randomFlashManager.startFlashing();
                } else {
                    Toast.makeText(getActivity(), "Buy this category", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void initActivityOnResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void initActivityBackPress() {
        if (isServiceRunning(PictureGamePlayActivity.this, MediaPlayingService.class)) {
            Intent intentMediaServiceStop = new Intent(getActivity(), MediaPlayingService.class);
            intentMediaServiceStop.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
            stopService(intentMediaServiceStop);
        }
        finish();

    }

    @Override
    public void initActivityDestroyTasks() {
        try {
            dismissProgressDialog();

            if (isServiceRunning(PictureGamePlayActivity.this, MediaPlayingService.class)) {
                Intent intentMediaServiceStop = new Intent(getActivity(), MediaPlayingService.class);
                intentMediaServiceStop.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                stopService(intentMediaServiceStop);
            }

//            if (bitmapOriginal != null && !bitmapOriginal.isRecycled()) {
//                bitmapOriginal.recycle();
//                bitmapOriginal = null;
//            }
//
//            if (bitmapScaled != null && !bitmapScaled.isRecycled()) {
//                bitmapScaled.recycle();
//                bitmapScaled = null;
//            }

            randomFlashManager.destroyFlashing();
            ivFlashImage.setImageBitmap(null);

            if (updateUserHistoryTask != null && updateUserHistoryTask.getStatus() == AsyncTask.Status.RUNNING) {
                updateUserHistoryTask.cancel(true);
            }

        } catch (Exception ex) {
            Logger.d(TAG, "exception: " + ex.getMessage());
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG, TAG + " onResume>>> " + "onResume>>>: ");
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        if (isServiceRunning(PictureGamePlayActivity.this, MediaPlayingService.class)) {
//            Intent intentMediaServiceStop = new Intent(getActivity(), MediaPlayingService.class);
//            intentMediaServiceStop.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
//            stopService(intentMediaServiceStop);
//        }
//        Logger.d(TAG, TAG + " onRestart>>> " + "onRestart>>>: ");
//
//    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (isServiceRunning(PictureGamePlayActivity.this, MediaPlayingService.class)) {
                Intent intentMediaServiceStop = new Intent(getActivity(), MediaPlayingService.class);
                intentMediaServiceStop.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_STOP);
                stopService(intentMediaServiceStop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.d(TAG, TAG + " onPause>>> " + "onPause>>>: ");

        //  Log.e("onPause>>>", "onPause");

    }

    /************************
     * Server communication *
     ************************/
    private class UpdateUserHistoryTask extends AsyncTask<String, Integer, Response> {

        Context mContext;
        ParamsUpdateUserHistory mParamUpdateUserHistory;

        private UpdateUserHistoryTask(Context context, ParamsUpdateUserHistory paramUpdateUserHistory) {
            mContext = context;
            mParamUpdateUserHistory = paramUpdateUserHistory;
        }

        @Override
        protected void onPreExecute() {
            //  showProgressDialog();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Logger.d(TAG, TAG + " >>> " + "AppUser(home-param): " + mParamUpdateUserHistory.toString());
                Call<APIResponse> call = mApiInterface.apiUpdateUserBrowsingHistory(mParamUpdateUserHistory);

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
                // dismissProgressDialog();

                if (result != null && result.isSuccessful()) {
                    Logger.d(TAG, TAG + " >>> " + "APIResponse(UpdateUserHistory): onResponse-server = " + result.toString());
                    APIResponse data = (APIResponse) result.body();
                    Logger.d(TAG, TAG + " >>> " + "data: " + data.toString() + "");

                    if (data != null && data.getStatus().equalsIgnoreCase("1")) {
                        Logger.d(TAG, TAG + " >>> " + "APIResponse(UpdateUserHistory()): onResponse-object = " + data.toString());


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
