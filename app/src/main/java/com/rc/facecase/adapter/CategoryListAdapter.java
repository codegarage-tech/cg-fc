package com.rc.facecase.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.rc.facecase.model.Category;
import com.rc.facecase.viewholder.CategoryViewHolder;

import java.security.InvalidParameterException;


/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class CategoryListAdapter extends RecyclerArrayAdapter<Category> {

    private static final int VIEW_TYPE_REGULAR = 1;
    public CategoryListAdapter(Context context) {
        super(context);
    }

    @Override
    public int getViewType(int position) {
        return VIEW_TYPE_REGULAR;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_REGULAR:
                return new CategoryViewHolder(parent);
            default:
                throw new InvalidParameterException();
        }
    }
}