package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.databinding.ObservableList;
import android.view.LayoutInflater;

import com.slamcode.goalcalendar.planning.YearMonthPair;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

/**
 * Created by moriasla on 02.02.2017.
 */

public interface ItemsCollectionAdapterProvider {

    CategoryNameRecyclerViewAdapter provideCategoryNameListViewAdapter(Context context, LayoutInflater inflater, ObservableList<CategoryPlansViewModel> itemsSource);

    CategoryDailyPlansRecyclerViewAdapter provideCategoryDailyPlansListViewAdapter(Context context, LayoutInflater layoutInflater, YearMonthPair yearMonthPair, ObservableList<CategoryPlansViewModel> itemsSource);

    CategoryPlansSummaryRecyclerViewAdapter providePlansSummaryForCategoriesRecyclerViewAdapter(Context context, LayoutInflater layoutInflater);

    DailyPlanHeaderRecyclerViewAdapter provideDailyPlanHeaderRecyclerViewAdapter(Context context, YearMonthPair yearMonthPair, LayoutInflater layoutInflater);
}
