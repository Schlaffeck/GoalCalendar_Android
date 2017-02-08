package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.view.LayoutInflater;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.view.CategoryDailyPlansRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.CategoryNameRecyclerViewAdapter;

/**
 * Created by moriasla on 02.02.2017.
 */

public class SimpleListViewAdapterProvider implements ListAdapterProvider {

    @Override
    public CategoryNameRecyclerViewAdapter provideCategoryNameListViewAdapter(Context context, LayoutInflater inflater) {
        return new CategoryNameRecyclerViewAdapter(context, inflater);
    }

    @Override
    public CategoryDailyPlansRecyclerViewAdapter provideCategoryDailyPlansListViewAdapter(Context context, LayoutInflater layoutInflater) {
        return new CategoryDailyPlansRecyclerViewAdapter(context, layoutInflater);
    }
}
