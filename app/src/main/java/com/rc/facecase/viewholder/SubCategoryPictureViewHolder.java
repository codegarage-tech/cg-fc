package com.rc.facecase.viewholder;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.rc.facecase.R;
import com.rc.facecase.activity.PictureGamePlayActivity;
import com.rc.facecase.enumeration.CategoryType;
import com.rc.facecase.model.Items;
import com.rc.facecase.model.SubCategory;
import com.rc.facecase.util.AllConstants;
import com.rc.facecase.util.Logger;

import org.parceler.Parcels;

import java.util.List;

import static com.rc.facecase.util.AllConstants.INTENT_KEY_CATEGORY_TYPE;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class SubCategoryPictureViewHolder extends BaseViewHolder<SubCategory> {

    private String TAG = SubCategoryPictureViewHolder.class.getSimpleName();
    private TextView tvSubcategory;
    private LinearLayout llSubcategory;

    public SubCategoryPictureViewHolder(ViewGroup parent) {
        super(parent, R.layout.row_sub_category_item);

        tvSubcategory = (TextView) $(R.id.tv_subcategory);
        llSubcategory = (LinearLayout) $(R.id.ll_subcategory);
    }

    @Override
    public void setData(final SubCategory data) {

        tvSubcategory.setText(data.getSub_category_name().trim());

        Log.e("getSub_name>>>>", data.getSub_category_name() + "");
        Log.e("subCategory>>>>", data.toString() + "");

        llSubcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                List<Items> itemsList = data.getItems();
                if (itemsList != null && itemsList.size() > 0) {
                    Items mItem = getSelectedItem(itemsList);
                    if (mItem != null) {
                        Logger.d(TAG, "Break item is: " + mItem.toString());
                        switchActivity(data.getSub_category_name(), mItem);
                    }
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Items getSelectedItem(List<Items> itemsList) {
        Items selectedItem = null;
        for (int i = 0; i < itemsList.size(); i++) {
            if (!itemsList.get(i).isShown()) {
                selectedItem = itemsList.get(i);
                selectedItem.setShown(true);
                Logger.d(TAG, "Break item at: " + i);
                break;
            }
        }

        // Reverse the list item
        if (selectedItem == null) {
            Logger.d(TAG, "All items has been shown");
            for (int i = 0; i < itemsList.size(); i++) {
                itemsList.get(i).setShown(false);
            }
            Logger.d(TAG, "All items are set as false, going for next recursion!");
            for (int i = 0; i < itemsList.size(); i++) {
                if (!itemsList.get(i).isShown()) {
                    selectedItem = itemsList.get(i);
                    selectedItem.setShown(true);
                    Logger.d(TAG, "Break item at: " + i);
                    break;
                }
            }
        }

        return selectedItem;
    }

    private void switchActivity(String subCategoryName, Items item) {
        Intent iFacePlay = new Intent(getContext(), PictureGamePlayActivity.class);
        iFacePlay.putExtra(INTENT_KEY_CATEGORY_TYPE, CategoryType.PICTURE_TO_CATEGORY.name());
        iFacePlay.putExtra(AllConstants.SUB_CATEGORY_NAME, subCategoryName);
        iFacePlay.putExtra(AllConstants.INTENT_KEY_ITEM, Parcels.wrap(item));
        getContext().startActivity(iFacePlay);
    }
}