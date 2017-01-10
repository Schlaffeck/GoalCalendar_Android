package com.slamcode.goalcalendar.view.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.planning.PlanStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by moriasla on 10.01.2017.
 */

public class GoalPlanStatusButton extends Button implements View.OnClickListener {

    private PlanStatus currentPlanStatus;
    private static Map<PlanStatus, Integer> STATUS_TO_BACKGROUND_MAP;
    private static Map<PlanStatus, Integer> STATUS_TO_FOREGROUND_COLOR_MAP;

    static
    {
        STATUS_TO_BACKGROUND_MAP = new HashMap<>();
        STATUS_TO_BACKGROUND_MAP.put(PlanStatus.Empty, R.drawable.planning_button_state_empty);
        STATUS_TO_BACKGROUND_MAP.put(PlanStatus.Failure, R.drawable.planning_button_state_failed);
        STATUS_TO_BACKGROUND_MAP.put(PlanStatus.Success, R.drawable.planning_button_state_success);
        STATUS_TO_BACKGROUND_MAP.put(PlanStatus.Planned, R.drawable.planning_button_state_planned);

        STATUS_TO_FOREGROUND_COLOR_MAP = new HashMap<>();
        STATUS_TO_FOREGROUND_COLOR_MAP.put(PlanStatus.Empty, R.color.planningStateButton_stateEmpty_foregroundColor);
        STATUS_TO_FOREGROUND_COLOR_MAP.put(PlanStatus.Failure, R.color.planningStateButton_stateFailed_foregroundColor);
        STATUS_TO_FOREGROUND_COLOR_MAP.put(PlanStatus.Success, R.color.planningStateButton_stateSuccess_foregroundColor);
        STATUS_TO_FOREGROUND_COLOR_MAP.put(PlanStatus.Planned, R.color.planningStateButton_statePlanned_foregroundColor);
    }

    public GoalPlanStatusButton(Context context) {
        super(context);
        super.setOnClickListener(this);
    }

    public GoalPlanStatusButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnClickListener(this);
    }

    public PlanStatus getStatus()
    {
        return this.currentPlanStatus;
    }

    public void setStatus(PlanStatus status)
    {
        this.currentPlanStatus = status;
        this.setBackgroundResource(STATUS_TO_BACKGROUND_MAP.get(status));
        this.setTextColor(STATUS_TO_FOREGROUND_COLOR_MAP.get(status));
        this.setText(status.toString());
    }

    @Override
    public void onClick(View view) {
        if(view != this)
        {
            return;
        }

        this.setStatus(this.currentPlanStatus.nextStatus());
    }
}
