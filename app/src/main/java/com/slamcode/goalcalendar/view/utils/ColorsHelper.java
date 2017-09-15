package com.slamcode.goalcalendar.view.utils;

import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;

import com.slamcode.goalcalendar.R;

/**
 * Created by moriasla on 09.01.2017.
 */

public final class ColorsHelper {

    public static View setSecondAccentBackgroundColor(View view)
    {
        TypedValue typedValue = new TypedValue();
        view.getContext().getTheme().resolveAttribute(R.attr.listAccentColor, typedValue, true);
        view.setBackgroundColor(ContextCompat.getColor(view.getContext(), typedValue.resourceId));
        return view;
    }

    public static View setListItemBackgroundColor(View view)
    {
        TypedValue typedValue = new TypedValue();
        view.getContext().getTheme().resolveAttribute(R.attr.listItemBackgroundColor, typedValue, true);
        view.setBackgroundColor(ContextCompat.getColor(view.getContext(), typedValue.resourceId));
        return view;
    }
}
