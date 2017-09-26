package com.slamcode.goalcalendar.view.bottomsheet;

import android.os.Parcelable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;

/**
 * Bottom sheet behavior with more control over view statuses
 */

public class StatusControlledBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

    @State
    private int permanentRestoreState = STATE_COLLAPSED;

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

    public int getPermanentRestoreState() {
        return permanentRestoreState;
    }

    public void setPermanentRestoreState(int permanentRestoreState) {
        this.permanentRestoreState = permanentRestoreState;
    }
}
