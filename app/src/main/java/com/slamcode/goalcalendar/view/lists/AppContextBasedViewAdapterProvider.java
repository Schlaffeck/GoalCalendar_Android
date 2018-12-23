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
    public CategoryPlansSummaryRecyclerViewAdapter providePlansSummaryForCategoriesRecyclerViewAdapter() {
        return new CategoryPlansSummaryRecyclerViewAdapter();
    }

    @Override
    public DailyPlanHeaderRecyclerViewAdapter provideDailyPlanHeaderRecyclerViewAdapter(YearMonthPair yearMonthPair,
                                                                                        DateTimeChangeListenersRegistry dateTimeChangeListenersRegistry) {
        return new DailyPlanHeaderRecyclerViewAdapter( yearMonthPair,  dateTimeChangeListenersRegistry);
    }

    @Override
    public CategoryPlansRecyclerViewAdapter provideCategoryPlansRecyclerViewAdapter(DateTimeChangeListenersRegistry dateTimeChangeListenersRegistry, YearMonthPair yearMonthPair, ObservableList<CategoryPlansViewModel> itemsSource) {
        return new CategoryPlansRecyclerViewAdapter(dateTimeChangeListenersRegistry, yearMonthPair, itemsSource);
    }

    @Override
    public BackupSourcesRecyclerViewAdapter provideBackusSourcesRecyclerViewAdapter() {
        return new BackupSourcesRecyclerViewAdapter();
    }
}
