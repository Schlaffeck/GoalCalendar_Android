package com.slamcode.goalcalendar.planning.summary;

import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by smoriak on 10/07/2017.
 */

public class SummaryCalculatorUtils {

    public static double calculateCategoryProgress(CategoryModel categoryModel)
    {
        int noOfExpectedTasks = ModelHelper.countNoOfExpectedTasks(categoryModel);
        int noOfSuccessfulTasks = ModelHelper.countNoOfTasksWithStatus(categoryModel, PlanStatus.Success);

        return 1.0 * noOfSuccessfulTasks / noOfExpectedTasks;
    }

    public static double calculateCategoryProgress(List<CategoryModel> categoryModels) {

        int sumOfTasks = 0;
        int tasksDone = 0;
        for (CategoryModel categoryModel : categoryModels)
        {
            sumOfTasks += ModelHelper.countNoOfExpectedTasks(categoryModel);
            tasksDone += ModelHelper.countNoOfTasksWithStatus(categoryModel, PlanStatus.Success);
        }

        return 1.0 * tasksDone / sumOfTasks;
    }

    public static Map<CategoryModel, Double> calculateProgressForEachCategory(List<CategoryModel> categoryModels)
    {
        HashMap<CategoryModel, Double> map = new HashMap<>();
        for(CategoryModel model : categoryModels)
            map.put(model, calculateCategoryProgress(model));

        return map;
    }

    public static double calculateMonthProgress(int year, Month month, DateTime dateTimeNow)
    {
        if(dateTimeNow.getYear() > year
                || dateTimeNow.getYear() == year && dateTimeNow.getMonth().getNumValue() > month.getNumValue())
            return 1.0;

        if(dateTimeNow.getYear() < year
                || dateTimeNow.getYear() == year && dateTimeNow.getMonth().getNumValue() < month.getNumValue())
            return -1.0;

        double monthProgress = 1.0 * (dateTimeNow.getDay() -1) / DateTimeHelper.getDaysCount(year, month);
        return monthProgress;
    }
}
