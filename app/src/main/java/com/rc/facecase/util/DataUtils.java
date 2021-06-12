package com.rc.facecase.util;

import com.rc.facecase.enumeration.MODE;
import com.rc.facecase.model.Category;
import com.rc.facecase.model.Items;
import com.rc.facecase.model.SubCategory;

import java.util.ArrayList;
import java.util.List;

import static com.rc.facecase.util.AllConstants.CATEGORY_MUSIC;
import static com.rc.facecase.util.AllConstants.CATEGORY_PICTURE;

public final class DataUtils {

    public static List<Items> getItemsList(Category category, String SubCategoryNames) {
        {
            //List<Items> itemsList = new ArrayList<>();
            if (category!=null) {

                if (category.getSub_categories().size()>0) {
                    for (int i = 0; i < category.getSub_categories().size(); i++) {
                        String subCategoryName = category.getSub_categories().get(i).getSub_category_name();
                        if (subCategoryName.equalsIgnoreCase(SubCategoryNames)) {
                            //searchMatches.
                            return category.getSub_categories().get(i).getItems();
                        } else if (subCategoryName.equalsIgnoreCase(SubCategoryNames)) {
                            return category.getSub_categories().get(i).getItems();
                        } else if (subCategoryName.equalsIgnoreCase(SubCategoryNames)) {
                            return category.getSub_categories().get(i).getItems();
                        } else if (subCategoryName.equalsIgnoreCase(SubCategoryNames)) {
                            return category.getSub_categories().get(i).getItems();
                        }
                    }
                }

            }
            return null;
        }
    }


    public static List<Category> getCategoryList(List<Category> categoryList, MODE mode) {

        {
            List<Category> categoryLists = new ArrayList<>();
            if (categoryList!=null) {
                if (categoryList.size()>0) {
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getCategory_name().trim().toLowerCase().equalsIgnoreCase(CATEGORY_PICTURE.toLowerCase()) ||
                                categoryList.get(i).getCategory_name().trim().toLowerCase().equalsIgnoreCase(CATEGORY_MUSIC.toLowerCase())) {
                               if (mode == MODE.PUBLIC){
                                   categoryLists.add(categoryList.get(i));
                               }
                        } else {
                            if (mode == MODE.ADDITIONAL){
                                categoryLists.add(categoryList.get(i));
                            }
                        }
                    }
                }

            }
            return categoryLists;
        }
    }
}
