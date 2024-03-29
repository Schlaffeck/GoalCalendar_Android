package com.slamcode.goalcalendar.view.controls;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import com.slamcode.goalcalendar.BR;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.planning.PlanStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moriasla on 10.01.2017.
 */

public class GoalPlanStatusButton extends ImageButton implements View.OnClickListener {

    private PlanStatus currentPlanStatus;
    private static Map<PlanStatus, ButtonStatusData> STATUS_TO_DATA_MAP;

    private boolean initialized;

    private List<OnStateChangedListener> onStateChangedListeners = new ArrayList<>();

    static
    {
        STATUS_TO_DATA_MAP = new HashMap<>();

        STATUS_TO_DATA_MAP.put(PlanStatus.Empty, new ButtonStatusData(
                R.drawable.planning_button_state_empty,
                R.color.planningStateButton_stateEmpty_foregroundColor,
                -1));

        STATUS_TO_DATA_MAP.put(PlanStatus.Failure, new ButtonStatusData(
                R.drawable.planning_button_state_failed,
                R.color.planningStateButton_stateFailed_foregroundColor,
                R.drawable.ic_clear_white_24dp));

        STATUS_TO_DATA_MAP.put(PlanStatus.Success, new ButtonStatusData(
                R.drawable.planning_button_state_success,
                R.color.planningStateButton_stateSuccess_foregroundColor,
                R.drawable.ic_done_white_24dp));

        STATUS_TO_DATA_MAP.put(PlanStatus.Planned, new ButtonStatusData(
                R.drawable.planning_button_state_planned,
                R.color.planningStateButton_statePlanned_foregroundColor,
                R.drawable.ic_date_range_white_24dp));
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
        if(this.currentPlanStatus == status)
            return;

        this.currentPlanStatus = status;
        this.animateStatusChange(status);
        this.notifyOnStateChanged(status);
    }

    public void addOnStateChangedListener(OnStateChangedListener listener)
    {
        if(this.onStateChangedListeners.contains(listener))
        {
            return;
        }
        this.onStateChangedListeners.add(listener);
    }

    public void removeOnStateChangedListener(OnStateChangedListener listener)
    {
        if(!this.onStateChangedListeners.contains(listener))
        {
            return;
        }
        this.onStateChangedListeners.remove(listener);
    }

    @Override
    public void onClick(View view) {
        if(view != this)
        {
            return;
        }

        this.setStatus(this.currentPlanStatus.nextStatus());
    }

    public interface OnStateChangedListener{
        void onStateChanged(PlanStatus newState);
    }

    private void animateStatusChange(PlanStatus status)
    {
        if(this.initialized) {
            // scale out animation
            Animation outAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.scale_out);
            outAnimation.setDuration(android.R.integer.config_shortAnimTime);

            Animation inAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.scale_in);
            outAnimation.setStartOffset(android.R.integer.config_shortAnimTime);
            outAnimation.setDuration(android.R.integer.config_shortAnimTime);

            this.setBackgroundResource(STATUS_TO_DATA_MAP.get(status).backgroundId);

            //scale in
            this.startAnimation(inAnimation);
        }
        else
        {
            this.setBackgroundResource(STATUS_TO_DATA_MAP.get(status).backgroundId);
            this.initialized = true;
        }

        if(STATUS_TO_DATA_MAP.get(status).iconId == -1)
        {
            this.setImageDrawable(null);
            this.setColorFilter(null);
        }
        else {
            Drawable drawable = ContextCompat.getDrawable(
                    this.getContext(),
                    STATUS_TO_DATA_MAP.get(status).iconId);
            this.setImageDrawable(drawable);

            this.setColorFilter(null);
            this.setColorFilter(
                    ContextCompat.getColor(
                            this.getContext(),
                            STATUS_TO_DATA_MAP.get(status).foregroundColorId),
                    PorterDuff.Mode.MULTIPLY);
        }
    }

    private void notifyOnStateChanged(PlanStatus status) {

        for (OnStateChangedListener listener :
                this.onStateChangedListeners) {
            if(listener != null)
            {
                listener.onStateChanged(status);
            }
        }
    }

    private static class ButtonStatusData
    {
        private final int backgroundId;
        private final int foregroundColorId;
        private final int iconId;

        ButtonStatusData(int backgroundId, int foregroundColorId, int iconId)
        {
            this.backgroundId = backgroundId;
            this.foregroundColorId = foregroundColorId;
            this.iconId = iconId;
        }
    }
}
