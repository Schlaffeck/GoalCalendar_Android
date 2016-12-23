package com.slamcode.goalcalendar.view;

import android.app.Activity;
import android.content.Context;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moriasla on 21.12.2016.
 */

public class ResourcesHelper {

    public static FrequencyPeriod frequencyPeriodFromResourceString(Context context, String stringValue)
    {
        if(stringValue.equals(context.getString(R.string.frequency_period_month)))
        {
            return FrequencyPeriod.Month;
        }

        if(stringValue.equals(context.getString(R.string.frequency_period_week)))
        {
            return FrequencyPeriod.Week;
        }

        return null;
    }

    public static String[] frequencyPeriodResourceStrings(Context context)
    {
        final int size = FrequencyPeriod.values().length;
        String[] result = new String[size];

        for (int i = 0; i< size; i++) {
            result[i] = context.getString(toResourceStringId(FrequencyPeriod.values()[i]));
        }

        return result;
    }

    public static int toResourceStringId(FrequencyPeriod period)
    {
        switch (period)
        {
            case Week: return R.string.frequency_period_week;
            case Month: return R.string.frequency_period_month;
        }

        return -1;
    }

    public static String toResourceString(Context context, FrequencyPeriod period)
    {
        switch (period)
        {
            case Week: return context.getResources().getString(R.string.frequency_period_week);
            case Month: return context.getResources().getString(R.string.frequency_period_month);
        }

        return null;
    }

    public static int toResourceStringId(Month month)
    {
        switch(month)
        {
            case JANUARY: return R.string.month_january;
            case FEBRUARY: return R.string.month_february;
            case MARCH: return R.string.month_march;
            case APRIL: return R.string.month_april;
            case MAY: return R.string.month_may;
            case JUNE: return R.string.month_june;
            case JULY: return R.string.month_july;
            case AUGUST: return R.string.month_august;
            case SEPTEMBER: return R.string.month_september;
            case OCTOBER: return R.string.month_october;
            case NOVEMBER: return R.string.month_november;
            case DECEMBER: return R.string.month_december;
        }

        return 0;
    }

    public static String[] monthsResourceStrings(Context context) {
        final int size = Month.values().length;
        String[] result = new String[size];

        for (int i = 0; i< size; i++) {
            result[i] = context.getString(toResourceStringId(Month.values()[i]));
        }

        return result;
    }
}
