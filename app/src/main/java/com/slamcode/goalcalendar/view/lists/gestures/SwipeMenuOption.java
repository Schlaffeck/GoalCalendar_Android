package com.slamcode.goalcalendar.view.lists.gestures;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Simple interface for swipe menu option
 */

public abstract class SwipeMenuOption {

    private final ViewInflater viewInflater;

    protected SwipeMenuOption(ViewInflater viewInflater)
    {
        this.viewInflater = viewInflater;
    }

    public abstract void optionSelectedAction();

    public View inflateView(Context context)
    {
        return this.viewInflater.inflateView(this, context , this.provideViewAttributes());
    }

    protected AttributeSet provideViewAttributes()
    {
        return null;
    }

    public interface ViewInflater
    {
        View inflateView(SwipeMenuOption menuOption, Context context, AttributeSet viewAttributes);
    }
}
