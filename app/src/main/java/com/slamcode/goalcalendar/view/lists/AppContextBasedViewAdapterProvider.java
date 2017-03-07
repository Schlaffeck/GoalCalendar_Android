package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.view.LayoutInflater;

import com.slamcode.goalcalendar.view.CategoryDailyPlansRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.CategoryNameRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.CategoryPlansSummaryRecyclerViewAdapter;

/**
 * Created by moriasla on 02.02.2017.
 */

public class AppContextBasedViewAdapterProvider implements ItemsCollectionAdapterProvider {

    @Override
    public CategoryNameRecyclerViewAdapter provideCategoryNameListViewAdapter(Context context, LayoutInflater inflater) {
        return new CategoryNameRecyclerViewAdapter(context, inflater);
    }

    @Override
    public CategoryDailyPlansRecyclerViewAdapter provideCategoryDailyPlansListViewAdapter(Context context, LayoutInflater layoutInflater) {
        return new CategoryDailyPlansRecyclerViewAdapter(context, layoutInflater);
    }

    @Override
    public CategoryPlansSummaryRecyclerViewAdapter providePlansSummaryForCategoriesRecyclerViewAdapter(Context context, LayoutInflater layoutInflater) {
        return new CategoryPlansSummaryRecyclerViewAdapter(context, layoutInflater);
    }
}
