package com.rc.facecase.viewholder;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.rc.facecase.R;
import com.rc.facecase.activity.MusicSubcategoryActivity;
import com.rc.facecase.activity.PictureSubcategoryActivity;
import com.rc.facecase.model.Category;
import com.rc.facecase.util.AllConstants;

import org.parceler.Parcels;

import static com.rc.facecase.util.AllConstants.CATEGORY_MUSIC;
import static com.rc.facecase.util.AllConstants.CATEGORY_PICTURE;


/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class CategoryViewHolder extends BaseViewHolder<Category> {

    private TextView tvCategory;
    private LinearLayout llCategory;

    public CategoryViewHolder(ViewGroup parent) {
        super(parent, R.layout.row_category_item);
        tvCategory = (TextView) $(R.id.tv_category);
        llCategory = (LinearLayout) $(R.id.ll_category);
    }

    @Override
    public void setData(final Category data) {

//        if (data.getCategory_name().trim().toLowerCase().equalsIgnoreCase(CATEGORY_PICTURE.toLowerCase())){
//            ivCategory.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_pictures));
//         //   AppUtil.loadImage(getContext(), ivCategory, getContext().getResources().getDrawable(R.drawable.ic_pictures), false, false, false);
//        } else if (data.getCategory_name().trim().toLowerCase().equalsIgnoreCase(CATEGORY_MUSIC.toLowerCase())){
////            AppUtil.loadImage(getContext(), ivCategory, getContext().getResources().getDrawable(R.drawable.ic_music), false, false, false);
//            ivCategory.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_music));
//
//        }

        if (data.getCategory_name().trim().toLowerCase().equalsIgnoreCase(CATEGORY_PICTURE.toLowerCase())) {
            llCategory.setBackgroundResource(R.drawable.ic_picture1);
            tvCategory.setVisibility(View.GONE);
        } else if (data.getCategory_name().trim().toLowerCase().equalsIgnoreCase(CATEGORY_MUSIC.toLowerCase())) {
            llCategory.setBackgroundResource(R.drawable.ic_music1);
            tvCategory.setVisibility(View.GONE);
        }

        tvCategory.setText(data.getCategory_name());
        Log.e("Categorydata>>>>", data.toString() + "");

        llCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (data.getCategory_name().trim().toLowerCase().equalsIgnoreCase(CATEGORY_PICTURE.toLowerCase())) {
                    Intent iFacePlay = new Intent(getContext(), PictureSubcategoryActivity.class);
                    iFacePlay.putExtra(AllConstants.SESSION_KEY_PICTURE_CATEGORY, Parcels.wrap(data));
                    iFacePlay.putExtra(AllConstants.CATEGORY_NAME, AllConstants.PICTURE_TITLE );
                    getContext().startActivity(iFacePlay);
                } else if (data.getCategory_name().trim().toLowerCase().equalsIgnoreCase(CATEGORY_MUSIC.toLowerCase())) {
                    Intent iFaceMusicPlay = new Intent(getContext(), MusicSubcategoryActivity.class);
                    iFaceMusicPlay.putExtra(AllConstants.SESSION_KEY_MUSIC_CATEGORY, Parcels.wrap(data));
                    getContext().startActivity(iFaceMusicPlay);
                }
                Log.e("data>>>>", data.toString() + "");

            }
        });
    }
}