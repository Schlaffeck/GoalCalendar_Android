package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.view.LayoutInflater;

import com.slamcode.goalcalendar.view.CategoryDailyPlansRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.CategoryNameRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.CategoryPlansSummaryRecyclerViewAdapter;

/**
 * Created by moriasla on 02.02.2017.
 */

public interface ItemsCollectionAdapterProvider {

    CategoryNameRecyclerViewAdapter provideCategoryNameListViewAdapter(Context context, LayoutInflater inflater);

    CategoryDailyPlansRecyclerViewAdapter provideCategoryDailyPlansListViewAdapter(Context context, LayoutInflater layoutInflater);

    CategoryPlansSummaryRecyclerViewAdapter providePlansSummaryForCategoriesRecyclerViewAdapter(Context context, LayoutInflater layoutInflater);

    CategoryPlansRecyclerViewAdapter provideCategoryPlansRecyclerViewAdapter(Context context, LayoutInflater layoutInflater);
}
