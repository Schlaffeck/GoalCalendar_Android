package com.slamcode.goalcalendar.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.view.controls.GoalPlanStatusButton;
import com.slamcode.goalcalendar.view.lists.RecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.ViewHolderBase;

import java.util.List;

import butterknife.BindView;

/**
 * Created by moriasla on 06.02.2017.
 */

public final class DailyPlanRecyclerViewAdapter extends RecyclerViewDataAdapter<DailyPlanModel, DailyPlanRecyclerViewAdapter.DailyPlanViewHolder> {

    protected DailyPlanRecyclerViewAdapter(Context context,
                                           LayoutInflater layoutInflater,
                                           List<DailyPlanModel> sourceList) {
        super(context, layoutInflater, sourceList);
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
