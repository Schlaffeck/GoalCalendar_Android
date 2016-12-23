package com.slamcode.goalcalendar.view.utils;

import android.widget.Spinner;

/**
 * Created by moriasla on 23.12.2016.
 */

public class SpinnerHelper {

    public static <T> int getValuePosition(Spinner spinner, T value)
    {
        int size = spinner.getAdapter().getCount();

        for(int i = 0; i < size; i++)
        {
            if(value.equals(spinner.getItemAtPosition(i)))
            {
                return i;
            }
        }

        return -1;
    }

    public static <T> boolean setSelectedValue(Spinner spinner, T valueToSet)
    {
        int position = getValuePosition(spinner, valueToSet);
        if(position >= 0)
        {
            return false;
        }

        spinner.setSelection(position);
        return true;
    }
}
