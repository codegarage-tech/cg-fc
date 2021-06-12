package com.rc.facecase.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.daimajia.slider.library.Animations.NoDescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.rc.facecase.R;
import com.rc.facecase.model.Image;
import com.rc.facecase.util.Logger;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.storage.SessionManager;

import java.util.ArrayList;
import java.util.List;

import static com.rc.facecase.util.AllConstants.KEY_INTENT_EXTRA_HOME;
import static com.rc.facecase.util.AllConstants.SESSION_INTRODUCTION;


/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class IntroductionActivity extends AppCompatActivity {

    private String TAG = IntroductionActivity.class.getSimpleName();
    private ImageView ivPlayIntroduction,ivHome;
    private LinearLayout linIntroMian;
    private RelativeLayout linSliderLayout;
    private RelativeLayout relCross;

    //Image slider
    private SliderLayout mDemoSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        Intent intent = getIntent();
        if (intent != null) {
            boolean isFromHome = intent.getBooleanExtra(KEY_INTENT_EXTRA_HOME, false);
            if (!isFromHome &&
                    SessionManager.getIntegerSetting(IntroductionActivity.this, SESSION_INTRODUCTION, 0) == 1) {
                Intent intentHome = new Intent(IntroductionActivity.this, HomeActivity.class);
                startActivity(intentHome);
                finish();
            } else {
                initUI();
            }
        }
        SessionManager.setIntegerSetting(IntroductionActivity.this, SESSION_INTRODUCTION, 1);
    }

    private void initUI() {
        ivPlayIntroduction = (ImageView) findViewById(R.id.iv_play_introduction);
        ivHome = (ImageView) findViewById(R.id.iv_home);
        linIntroMian = (LinearLayout) findViewById(R.id.lin_lay_intro_mian);

        ivPlayIntroduction.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                String socialLink = "https://www.youtube.com/watch?v=TjPaHzYyBks";

                try {
                    Uri uri = Uri.parse(socialLink);
                    Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, uri);
                    youtubeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(youtubeIntent);

                } catch (Exception e) {
                    Logger.d("youtube api", "exception: " + e.getMessage());
                    // Youtube is not installed. Open in the browser
                    Uri uri = Uri.parse(socialLink);
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            }
        });



        ivHome.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intent = new Intent(IntroductionActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        //Image slider
//        mDemoSlider.stopAutoCycle();
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}