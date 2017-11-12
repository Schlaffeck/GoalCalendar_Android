package com.slamcode.goalcalendar.view.bottomsheet;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Bottom sheet behavior with more control over view statuses
 */

public class StatusControlledBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

    @State
    private int permanentRestoreState = STATE_COLLAPSED;
    private boolean disableLayoutChange = false;

    public StatusControlledBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onRestoreInstanceState(CoordinatorLayout parent, V child, Parcelable state) {
        super.onRestoreInstanceState(parent, child, state);
        this.setState(this.permanentRestoreState);
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, V child) {
        this.setState(this.permanentRestoreState);
        return super.onSaveInstanceState(parent, child);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        return !this.disableLayoutChange || super.onLayoutChild(parent, child, layoutDirection);
    }

    public int getPermanentRestoreState() {
        return permanentRestoreState;
    }

    public void setPermanentRestoreState(int permanentRestoreState) {
        this.permanentRestoreState = permanentRestoreState;
    }

    public boolean isDisableLayoutChange() {
        return disableLayoutChange;
    }

    public void setDisableLayoutChange(boolean disableLayoutChange) {
        this.disableLayoutChange = disableLayoutChange;
    }
}
