package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.lists.base.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.base.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableRecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableViewHolderBase;
import com.slamcode.goalcalendar.viewmodels.DailyPlansViewModel;
import com.slamcode.goalcalendar.viewmodels.DayInMonthViewModel;

/**
 * Created by moriasla on 08.03.2017.
 */

public class DailyPlanHeaderRecyclerViewAdapter extends BindableRecyclerViewDataAdapter<DayInMonthViewModel, DailyPlanHeaderRecyclerViewAdapter.DailyPlanHeaderViewHolder> {

    protected DailyPlanHeaderRecyclerViewAdapter(Context context, LayoutInflater layoutInflater) {
        this(context, layoutInflater, new SortedList<>(DayInMonthViewModel.class, new ComparatorSortedListCallback<>(new DefaultComparator<DayInMonthViewModel>())));
    }

    protected DailyPlanHeaderRecyclerViewAdapter(Context context, LayoutInflater layoutInflater, SortedList<DayInMonthViewModel> sourceList) {
        super(context, layoutInflater, sourceList);
    }

    @Override
    public DailyPlanHeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getLayoutInflater().inflate(R.layout.monthly_goals_header_day_number_cell, null);
        return new DailyPlanHeaderViewHolder(view);
    }

    public class DailyPlanHeaderViewHolder extends BindableViewHolderBase<DayInMonthViewModel>
    {
        public DailyPlanHeaderViewHolder(View view) {
            super(view);
        }
    }
}
