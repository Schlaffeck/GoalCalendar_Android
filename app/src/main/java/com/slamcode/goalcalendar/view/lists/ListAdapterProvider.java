package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.view.LayoutInflater;

import com.slamcode.goalcalendar.view.CategoryDailyPlansRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.CategoryNameRecyclerViewAdapter;

/**
 * Created by moriasla on 02.02.2017.
 */

public interface ListAdapterProvider {

    CategoryNameRecyclerViewAdapter provideCategoryNameListViewAdapter(Context context, LayoutInflater inflater);

    CategoryDailyPlansRecyclerViewAdapter provideCategoryDailyPlansListViewAdapter(Context context, LayoutInflater layoutInflater);
}
