package com.slamcode.goalcalendar.view.controls;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.view.utils.ResourcesHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moriasla on 10.01.2017.
 */

public class GoalPlanStatusButton extends android.support.v7.widget.AppCompatImageButton implements View.OnClickListener {

    private PlanStatus currentPlanStatus;
    private static Map<PlanStatus, ButtonStatusData> STATUS_TO_DATA_MAP;

    private boolean initialized;

    private List<OnStateChangedListener> onStateChangedListeners = new ArrayList<>();

    private List<PlanStatus> omittedStatuses;

    static
    {
        STATUS_TO_DATA_MAP = new HashMap<>();

        STATUS_TO_DATA_MAP.put(PlanStatus.Empty, new ButtonStatusData(
                R.attr.planningStateButton_stateEmpty_background,
                R.attr.planningStateButton_stateEmpty_foregroundColor,
                -1));

        STATUS_TO_DATA_MAP.put(PlanStatus.Failure, new ButtonStatusData(
                R.attr.planningStateButton_stateFailed_background,
                R.attr.planningStateButton_stateFailed_foregroundColor,
                R.drawable.ic_clear_white_24dp));

        STATUS_TO_DATA_MAP.put(PlanStatus.Success, new ButtonStatusData(
                R.attr.planningStateButton_stateSuccess_background,
                R.attr.planningStateButton_stateSuccess_foregroundColor,
                R.drawable.ic_done_white_24dp));

        STATUS_TO_DATA_MAP.put(PlanStatus.Planned, new ButtonStatusData(
                R.attr.planningStateButton_statePlanned_background,
                R.attr.planningStateButton_statePlanned_foregroundColor,
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

        this.setStatus(this.provideNextStatus());
    }

    private PlanStatus provideNextStatus() {

        PlanStatus result = this.currentPlanStatus;
        PlanStatus next = this.currentPlanStatus;
        boolean found = false;
        while(!found) {
            next = next.nextStatus();
            if(this.omittedStatuses == null || this.omittedStatuses.indexOf(next) == -1) {
                found = true;
                result = next;
            }
        }

        return result;
    }

    public void setOmittedStatuses(PlanStatus... omittedStatuses) {
        if(omittedStatuses == null)
            this.omittedStatuses = null;

        this.omittedStatuses = Arrays.asList(omittedStatuses);
    }

    public interface OnStateChangedListener{
        void onStateChanged(PlanStatus newState);
    }

    private void animateStatusChange(PlanStatus status)
    {
        if(this.initialized) {
            // scale out animation
            Animation outAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.scale_out);
            outAnimation.setDuration(android.R.integer.config_mediumAnimTime);

            Animation inAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.scale_in);
            outAnimation.setStartOffset(android.R.integer.config_shortAnimTime);
            outAnimation.setDuration(android.R.integer.config_mediumAnimTime);

            this.setBackgroundResource(ResourcesHelper.getResourceIdFromThemeAttribute(this, STATUS_TO_DATA_MAP.get(status).backgroundId));

            //scale in
            this.startAnimation(inAnimation);
        }
        else
        {
            this.setBackgroundResource(ResourcesHelper.getResourceIdFromThemeAttribute(this, STATUS_TO_DATA_MAP.get(status).backgroundId));
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
                            ResourcesHelper.getResourceIdFromThemeAttribute(this, STATUS_TO_DATA_MAP.get(status).foregroundColorAttributeId)),
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
        private final int foregroundColorAttributeId;
        private final int iconId;

        ButtonStatusData(int backgroundId, int foregroundColorAttributeId, int iconId)
        {
            this.backgroundId = backgroundId;
            this.foregroundColorAttributeId = foregroundColorAttributeId;
            this.iconId = iconId;
        }
    }
}
