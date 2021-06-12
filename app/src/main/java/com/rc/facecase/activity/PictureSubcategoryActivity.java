package com.rc.facecase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rc.facecase.R;
import com.rc.facecase.adapter.SubCategoryListAdapter;
import com.rc.facecase.base.BaseActivity;
import com.rc.facecase.decoration.ItemOffsetDecoration;
import com.rc.facecase.model.Category;
import com.rc.facecase.model.SubCategory;
import com.rc.facecase.util.AllConstants;
import com.rc.facecase.util.Logger;
import com.reversecoder.library.event.OnSingleClickListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.rc.facecase.util.AllConstants.CATEGORY_NAME;

public class PictureSubcategoryActivity extends BaseActivity {

    private ImageView ivHome,ivBack;
    private TextView tvTitle;
    private Category pictureCategory;
  //  SubCategory subCategory;
    private List<SubCategory> subCategories = new ArrayList<>();
  //  private NestedScrollView mainScrollView;
    private RecyclerView rvSubCategory;
    private SubCategoryListAdapter subCategoryListAdapter;
    private String categoryName = "";
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
            categoryName = getIntent().getExtras().getString(CATEGORY_NAME);

            Parcelable mParcelablePictureCategory = intent.getParcelableExtra(AllConstants.SESSION_KEY_PICTURE_CATEGORY);
            if (mParcelablePictureCategory != null) {
                pictureCategory = Parcels.unwrap(mParcelablePictureCategory);
                Logger.d(TAG, TAG + " >>> " + "pictureCategory: " + pictureCategory.toString());
            }
        }
    }

    @Override
    public void initActivityViews() {
       // mainScrollView = (NestedScrollView)findViewById(R.id.nested_scroll);
        rvSubCategory= (RecyclerView)findViewById(R.id.rv_subcategory);
        ivBack= (ImageView)findViewById(R.id.iv_back);
        ivHome = (ImageView) findViewById(R.id.iv_home);
        tvTitle = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {

        subCategoryListAdapter = new SubCategoryListAdapter( getApplicationContext());
        rvSubCategory.setNestedScrollingEnabled(false);
        rvSubCategory.setLayoutManager( new GridLayoutManager( getActivity(), 4) );
        rvSubCategory.setHasFixedSize( true );
        // For spacing among items
       // rvSubCategory.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.HORIZONTAL));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.dp_2_5);
        rvSubCategory.addItemDecoration(itemDecoration);

        rvSubCategory.scrollToPosition(0);
        tvTitle.setText(categoryName);
        initSubCategoryData(pictureCategory);
    }

    @Override
    public void initActivityActions(Bundle savedInstanceState) {
//        // Wait until my scrollView is ready
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
    private void initSubCategoryData(Category pictureCategories ) {
        if (pictureCategories !=null) {
            if (subCategoryListAdapter != null) {
                subCategoryListAdapter.addAll(pictureCategories.getSub_categories());
                rvSubCategory.setAdapter(subCategoryListAdapter);
                Log.e("getSub_categories",pictureCategories.getSub_categories().size()+"");
                subCategoryListAdapter.notifyDataSetChanged();
            }
        }
    }
}
