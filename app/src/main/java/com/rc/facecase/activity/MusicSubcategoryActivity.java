package com.rc.facecase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.rc.facecase.R;
import com.rc.facecase.adapter.MusicSubCategoryListAdapter;
import com.rc.facecase.base.BaseActivity;
import com.rc.facecase.decoration.ItemOffsetDecoration;
import com.rc.facecase.model.Category;
import com.rc.facecase.util.AllConstants;
import com.rc.facecase.util.Logger;
import com.reversecoder.library.event.OnSingleClickListener;

import org.parceler.Parcels;

public class MusicSubcategoryActivity extends BaseActivity {
    private TextView tvTitle;
    private ImageView ivHome,ivBack,ivFamousCommercials;
    private Category musicCategory;
    private RecyclerView rvSubCategory;
   // private NestedScrollView mainScrollView;
    private MusicSubCategoryListAdapter subCategoryListAdapter;
    @Override
    public String[] initActivityPermissions() {
        return new String[]{};

    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_subcategory_screen;
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
            Parcelable mParcelableMusicCategory = intent.getParcelableExtra(AllConstants.SESSION_KEY_MUSIC_CATEGORY);
            if (mParcelableMusicCategory != null) {
                musicCategory = Parcels.unwrap(mParcelableMusicCategory);
                Logger.d(TAG, TAG + " >>> " + "musicCategory: " + musicCategory.toString());
            }
        }
    }

    @Override
    public void initActivityViews() {
     //   mainScrollView = (NestedScrollView)findViewById(R.id.nested_scroll);
        rvSubCategory= (RecyclerView)findViewById(R.id.rv_subcategory);
        tvTitle= (TextView)findViewById(R.id.tv_title);
        ivBack= (ImageView)findViewById(R.id.iv_back);
        ivHome = (ImageView) findViewById(R.id.iv_home);
        //  ivFamousCommercials= (ImageView)findViewById(R.id.iv_famous_commercials);
        tvTitle.setText(getResources().getString(R.string.txt_music_category));
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        subCategoryListAdapter = new MusicSubCategoryListAdapter( getApplicationContext());
        rvSubCategory.setNestedScrollingEnabled(false);
        rvSubCategory.setLayoutManager( new GridLayoutManager( getActivity(), 4) );
        rvSubCategory.setHasFixedSize( true );
        rvSubCategory.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.dp_2_5));
        rvSubCategory.scrollToPosition(0);
        initSubCategoryData(musicCategory);
    }

    @Override
    public void initActivityActions(Bundle savedInstanceState) {
        // Wait until my scrollView is ready
//        mainScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                // Ready, move up
//                mainScrollView.fullScroll(View.FOCUS_UP);
//            }
//        });
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

    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }
    private void initSubCategoryData(Category musicCategory ) {
        if (musicCategory !=null) {
            if (subCategoryListAdapter != null) {
                subCategoryListAdapter.addAll(musicCategory.getSub_categories());
                rvSubCategory.setAdapter(subCategoryListAdapter);
                Log.e("getMusicCategorys",musicCategory.getSub_categories().toString()+"");
                subCategoryListAdapter.notifyDataSetChanged();
            }
        }
    }

}
