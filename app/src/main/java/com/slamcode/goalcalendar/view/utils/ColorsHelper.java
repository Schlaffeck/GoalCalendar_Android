package com.slamcode.goalcalendar.view.utils;

import android.support.v4.content.ContextCompat;
import android.view.View;

import com.slamcode.goalcalendar.R;

/**
 * Created by moriasla on 09.01.2017.
 */

public final class ColorsHelper {

    public static View setSecondAccentBackgroundColor(View view)
    {
        view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.listAccentColor));
        return view;
    }
}
