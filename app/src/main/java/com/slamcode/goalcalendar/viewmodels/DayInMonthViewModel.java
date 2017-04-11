package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import com.slamcode.goalcalendar.planning.DateTimeHelper;

/**
 * Created by moriasla on 08.03.2017.
 */

public class DayInMonthViewModel extends BaseObservable implements Comparable<DayInMonthViewModel> {

    private final MonthViewModel monthViewModel;
    private final int dayNumber;

    public DayInMonthViewModel(MonthViewModel monthViewModel, int dayNumber)
    {
        this.monthViewModel = monthViewModel;
        this.dayNumber = dayNumber;
    }

    @Bindable
    public String getDayName()
    {
        return DateTimeHelper.getWeekDayNameShort(this.monthViewModel.getYear(), this.monthViewModel.getMonth().getNumValue(), this.getDayNumber());
    }

    @Bindable
    public int getDayNumber() {
        return dayNumber;
    }

    @Override
    public int compareTo(@NonNull DayInMonthViewModel other) {
        if(other == null)
            return 1;

        return this.getDayNumber() > other.getDayNumber() ? 1 :
                this.getDayNumber() < other.getDayNumber() ? -1 : 0;
    }
}
