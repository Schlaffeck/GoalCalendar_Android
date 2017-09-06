package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.databinding.ObservableList;
import android.view.LayoutInflater;

import com.slamcode.goalcalendar.planning.schedule.DateTimeChangeListenersRegistry;
import com.slamcode.goalcalendar.planning.YearMonthPair;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

/**
 * Created by moriasla on 02.02.2017.
 */

public class AppContextBasedViewAdapterProvider implements ItemsCollectionAdapterProvider {

    @Override
    public CategoryPlansSummaryRecyclerViewAdapter providePlansSummaryForCategoriesRecyclerViewAdapter(Context context, LayoutInflater layoutInflater) {
        return new CategoryPlansSummaryRecyclerViewAdapter(context, layoutInflater);
    }

    @Override
    public DailyPlanHeaderRecyclerViewAdapter provideDailyPlanHeaderRecyclerViewAdapter(Context context,
                                                                                        YearMonthPair yearMonthPair,
                                                                                        LayoutInflater layoutInflater,
                                                                                        DateTimeChangeListenersRegistry dateTimeChangeListenersRegistry) {
        return new DailyPlanHeaderRecyclerViewAdapter(context, yearMonthPair, layoutInflater, dateTimeChangeListenersRegistry);
    }

    @Override
    public CategoryPlansRecyclerViewAdapter provideCategoryPlansRecyclerViewAdapter(Context context, LayoutInflater layoutInflater, DateTimeChangeListenersRegistry dateTimeChangeListenersRegistry, YearMonthPair yearMonthPair, ObservableList<CategoryPlansViewModel> itemsSource) {
        return new CategoryPlansRecyclerViewAdapter(context, layoutInflater, dateTimeChangeListenersRegistry, yearMonthPair, itemsSource);
    }
}
