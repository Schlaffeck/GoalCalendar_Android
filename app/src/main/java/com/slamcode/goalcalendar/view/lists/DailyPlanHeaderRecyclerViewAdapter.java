package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.YearMonthPair;
import com.slamcode.goalcalendar.view.lists.base.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.base.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.base.SortedListCallbackSet;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableRecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableViewHolderBase;
import com.slamcode.goalcalendar.view.lists.base.bindable.ObservableSortedList;
import com.slamcode.goalcalendar.view.utils.ColorsHelper;
import com.slamcode.goalcalendar.viewmodels.DailyPlansViewModel;
import com.slamcode.goalcalendar.viewmodels.DayInMonthViewModel;

/**
 * Created by moriasla on 08.03.2017.
 */

public class DailyPlanHeaderRecyclerViewAdapter extends BindableRecyclerViewDataAdapter<DayInMonthViewModel, DailyPlanHeaderRecyclerViewAdapter.DailyPlanHeaderViewHolder> {

    private final YearMonthPair yearMonthPair;

    protected DailyPlanHeaderRecyclerViewAdapter(Context context,
                                                 YearMonthPair yearMonthPair,
                                                 LayoutInflater layoutInflater) {
        this(context, yearMonthPair, layoutInflater, new ObservableSortedList<>(
                new ObservableArrayList<DayInMonthViewModel>(),
                DayInMonthViewModel.class,
                new SortedListCallbackSet<>(new ComparatorSortedListCallback<>(new DefaultComparator<DayInMonthViewModel>()), new DefaultComparator<DayInMonthViewModel>())));
    }

    protected DailyPlanHeaderRecyclerViewAdapter(Context context, YearMonthPair yearMonthPair,
                                                 LayoutInflater layoutInflater, ObservableSortedList<DayInMonthViewModel> sourceList) {
        super(context, layoutInflater, sourceList);
        this.yearMonthPair = yearMonthPair;
    }

    @Override
    public DailyPlanHeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getLayoutInflater().inflate(R.layout.monthly_goals_header_day_number_cell, null);
        return new DailyPlanHeaderViewHolder(view);
    }

    private boolean isCurrentDate(DayInMonthViewModel dailyPlanModel)
    {
        if(this.yearMonthPair == null)
            return false;

        int dayNumber = dailyPlanModel.getDayNumber();
        return DateTimeHelper.isCurrentDate(
                this.yearMonthPair.getYear(),
                this.yearMonthPair.getMonth(),
                dayNumber);
    }

    public class DailyPlanHeaderViewHolder extends BindableViewHolderBase<DayInMonthViewModel>
    {
        public DailyPlanHeaderViewHolder(View view) {
            super(view);
        }

        @Override
        public void bindToModel(DayInMonthViewModel modelObject) {
            super.bindToModel(modelObject);

            if(isCurrentDate(modelObject))
                ColorsHelper.setSecondAccentBackgroundColor(this.getView());
        }
    }
}
