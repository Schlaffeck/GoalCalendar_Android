package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.slamcode.goalcalendar.planning.Month;

/**
 * View model for month data
 */

public class MonthViewModel extends BaseObservable {

    private int year;

    private Month month;

    public MonthViewModel(int year, Month month)
    {
        this.year = year;
        this.month = month;
    }

    @Bindable
    public int getYear() {
        return year;
    }

    @Bindable
    public Month getMonth() {
        return month;
    }
}
