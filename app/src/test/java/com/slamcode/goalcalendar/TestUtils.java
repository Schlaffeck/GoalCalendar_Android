package com.slamcode.goalcalendar;

import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

/**
 * Created by smoriak on 03/07/2017.
 */

public class TestUtils {

    public static CategoryModel createCategoryModel(int year, Month month, int id, String name, FrequencyPeriod frequencyPeriod, int frequencyRatio, int noOfTasksDone)
    {
        CategoryModel result = new CategoryModel(id, name, frequencyPeriod, frequencyRatio);

        result.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, month));
        if(noOfTasksDone > 0)
        {
            for(int i =0; i < noOfTasksDone && i < result.getDailyPlans().size(); i++)
                result.getDailyPlans().get(i).setStatus(PlanStatus.Success);
        }

        return result;
    }
}
