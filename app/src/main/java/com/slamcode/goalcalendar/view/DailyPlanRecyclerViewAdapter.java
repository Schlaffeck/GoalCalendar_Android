package com.slamcode.goalcalendar.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ViewDataBinding;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.BR;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.view.controls.GoalPlanStatusButton;
import com.slamcode.goalcalendar.view.lists.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.RecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.ViewHolderBase;
import com.slamcode.goalcalendar.view.utils.ColorsHelper;

import java.util.Collection;

import butterknife.BindView;

/**
 * Created by moriasla on 06.02.2017.
 */

public class DailyPlanRecyclerViewAdapter extends RecyclerViewDataAdapter<DailyPlanModel, DailyPlanRecyclerViewAdapter.DailyPlanViewHolder> {

    private final MonthlyPlansModel monthlyPlans;

    protected DailyPlanRecyclerViewAdapter(Context context,
                                           LayoutInflater layoutInflater,
                                           MonthlyPlansModel monthlyPlans,
                                           Collection<DailyPlanModel> sourceCollection) {
        super(context, layoutInflater, new SortedList<>(DailyPlanModel.class, new ComparatorSortedListCallback<DailyPlanModel>(new DefaultComparator<DailyPlanModel>())));
        this.monthlyPlans = monthlyPlans;
        this.updateSourceCollection(sourceCollection);
    }

    @Override
    public DailyPlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getLayoutInflater().inflate(R.layout.monthly_goals_plan_status_cell, null);
        return new DailyPlanViewHolder(view);
    }

    private boolean isCurrentDate(DailyPlanModel dailyPlanModel)
    {
        if(this.monthlyPlans == null)
            return false;

        int dayNumber = dailyPlanModel.getDayNumber();
        return DateTimeHelper.isCurrentDate(
                this.monthlyPlans.getYear(),
                this.monthlyPlans.getMonth(),
                dayNumber);
    }

    /**
     * View holder for daily plan
     */
    public class DailyPlanViewHolder extends ViewHolderBase<DailyPlanModel>{

        @BindView(R.id.plan_status_list_item_view_button)
        GoalPlanStatusButton statusButton;

        private final ViewDataBinding binding;

        public DailyPlanViewHolder(View view) {

            super(view);
            this.binding = DataBindingUtil.bind(view);
        }

        @Override
        public void bindToModel(final DailyPlanModel modelObject) {
            super.bindToModel(modelObject);
            this.binding.setVariable(BR.vm, modelObject);

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
