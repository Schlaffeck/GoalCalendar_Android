package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.planning.YearMonthPair;
import com.slamcode.goalcalendar.view.controls.GoalPlanStatusButton;
import com.slamcode.goalcalendar.view.lists.base.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.base.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.base.RecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableViewHolderBase;
import com.slamcode.goalcalendar.view.utils.ColorsHelper;
import com.slamcode.goalcalendar.viewmodels.DailyPlansViewModel;

import java.util.Collection;

import butterknife.BindView;

/**
 * Created by moriasla on 06.02.2017.
 */

public class DailyPlanRecyclerViewAdapter extends RecyclerViewDataAdapter<DailyPlansViewModel, DailyPlanRecyclerViewAdapter.DailyPlanViewHolder> {

    private final YearMonthPair yearMonthPair;

    protected DailyPlanRecyclerViewAdapter(Context context,
                                           LayoutInflater layoutInflater,
                                           YearMonthPair yearMonthPair,
                                           Collection<DailyPlansViewModel> sourceCollection) {
        super(context, layoutInflater, new SortedList<>(DailyPlansViewModel.class, new ComparatorSortedListCallback<DailyPlansViewModel>(new DefaultComparator<DailyPlansViewModel>())));
        this.yearMonthPair = yearMonthPair;
        this.updateSourceCollection(sourceCollection);
    }

    @Override
    public DailyPlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getLayoutInflater().inflate(R.layout.monthly_goals_plan_status_cell, null);
        return new DailyPlanViewHolder(view);
    }

    private boolean isCurrentDate(DailyPlansViewModel dailyPlanModel)
    {
        if(this.yearMonthPair == null)
            return false;

        int dayNumber = dailyPlanModel.getDayNumber();
        return DateTimeHelper.isCurrentDate(
                this.yearMonthPair.getYear(),
                this.yearMonthPair.getMonth(),
                dayNumber);
    }

    /**
     * View holder for daily plan
     */
    public class DailyPlanViewHolder extends BindableViewHolderBase<DailyPlansViewModel> {

        @BindView(R.id.plan_status_list_item_view_button)
        GoalPlanStatusButton statusButton;

        public DailyPlanViewHolder(View view) {

            super(view);
        }

        @Override
        public void bindToModel(final DailyPlansViewModel modelObject) {
            super.bindToModel(modelObject);

            this.statusButton.addOnStateChangedListener(new GoalPlanStatusButton.OnStateChangedListener() {
                @Override
                public void onStateChanged(PlanStatus newState) {
                    modelObject.setStatus(newState);
                }
            });

            if(isCurrentDate(modelObject))
                ColorsHelper.setSecondAccentBackgroundColor(this.getView());
        }
    }
}
