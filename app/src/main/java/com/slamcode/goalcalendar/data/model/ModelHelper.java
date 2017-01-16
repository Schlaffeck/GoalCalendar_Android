package com.slamcode.goalcalendar.data.model;

import com.slamcode.collections.ElementCreator;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Created by moriasla on 16.01.2017.
 */

public class ModelHelper {

    public static List<DailyPlanModel> createListOfDailyPlansForMonth(final int year, final Month month)
    {
        return com.slamcode.collections.CollectionUtils.createList(month.getDaysCount(year), new ElementCreator<DailyPlanModel>() {
            @Override
            public DailyPlanModel Create(int index, List<DailyPlanModel> currentList) {
                return new DailyPlanModel(index ^ month.getNumValue() ^ year, PlanStatus.Empty, index+1);
            }
        });
    }
}
