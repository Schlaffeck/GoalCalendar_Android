package com.slamcode.goalcalendar.view;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.view.controls.GoalPlanStatusButton;
import com.slamcode.goalcalendar.view.lists.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.RecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.ViewHolderBase;

import java.util.Collection;

import butterknife.BindView;

/**
 * Created by moriasla on 06.02.2017.
 */

public final class DailyPlanRecyclerViewAdapter extends RecyclerViewDataAdapter<DailyPlanModel, DailyPlanRecyclerViewAdapter.DailyPlanViewHolder> {

    protected DailyPlanRecyclerViewAdapter(Context context,
                                           LayoutInflater layoutInflater,
                                           Collection<DailyPlanModel> sourceCollection) {
        super(context, layoutInflater, new SortedList<>(DailyPlanModel.class, new ComparatorSortedListCallback<DailyPlanModel>(new DefaultComparator<DailyPlanModel>())));
        this.updateSourceCollection(sourceCollection);
    }

    @Override
    public DailyPlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getLayoutInflater().inflate(R.layout.monthly_goals_plan_status_cell, null);
        return new DailyPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DailyPlanViewHolder dailyPlanViewHolder, int position) {

        final DailyPlanModel dailyPlanModel = this.getItem(position);
        dailyPlanViewHolder.statusButton.setStatus(dailyPlanModel.getStatus());
        dailyPlanViewHolder.statusButton.addOnStateChangedListener(new GoalPlanStatusButton.OnStateChangedListener() {
            @Override
            public void onStateChanged(PlanStatus newState) {
                dailyPlanModel.setStatus(newState);
            }
        });
    }

    /**
     * View holder for daily plan
     */
    public class DailyPlanViewHolder extends ViewHolderBase<DailyPlanModel>{

        @BindView(R.id.plan_status_list_item_view_button)
        GoalPlanStatusButton statusButton;

        public DailyPlanViewHolder(View view) {
            super(view);
        }
    }
}
