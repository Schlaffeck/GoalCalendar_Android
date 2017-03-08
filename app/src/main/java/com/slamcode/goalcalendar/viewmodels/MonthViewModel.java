package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementCreator;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * View model for month data
 */

public class MonthViewModel extends BaseObservable {

    private int year;

    private Month month;

    private Collection<DayInMonthViewModel> daysList;

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

    @Bindable
    public Collection<DayInMonthViewModel> getDaysList() {
        if(this.daysList == null)
        {
            final MonthViewModel monthViewModel = this;
            this.daysList = CollectionUtils.createList(DateTimeHelper.getDaysCount(year, month),
                new ElementCreator<DayInMonthViewModel>() {
                    @Override
                    public DayInMonthViewModel Create(int index, List<DayInMonthViewModel> currentList) {
                        return new DayInMonthViewModel(monthViewModel, index+1);
                    }
                });
        }
        return daysList;
    }
}
