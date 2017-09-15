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

public interface ItemsCollectionAdapterProvider {

    CategoryPlansSummaryRecyclerViewAdapter providePlansSummaryForCategoriesRecyclerViewAdapter();

    DailyPlanHeaderRecyclerViewAdapter provideDailyPlanHeaderRecyclerViewAdapter(YearMonthPair yearMonthPair,
                                                                                 DateTimeChangeListenersRegistry dateTimeChangeListenersRegistry);

    CategoryPlansRecyclerViewAdapter provideCategoryPlansRecyclerViewAdapter(DateTimeChangeListenersRegistry dateTimeChangeListenersRegistry,
                                                                                   YearMonthPair yearMonthPair,
                                                                                   ObservableList<CategoryPlansViewModel> itemsSource);
}
