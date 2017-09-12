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

    public static double calculateCategoryPlannedTasksPercentage(List<CategoryModel> categoryModels) {

        int sumOfTasks = 0;
        int tasksPlanned = 0;
        for (CategoryModel categoryModel : categoryModels)
        {
            sumOfTasks += ModelHelper.countNoOfExpectedTasks(categoryModel);
            tasksPlanned += ModelHelper.countNoOfTasksWithStatus(categoryModel, PlanStatus.Planned);
        }

        return 1.0 * tasksPlanned / sumOfTasks;
    }

    public static double calculateCategoryPlannedTasksPercentage(CategoryModel categoryModel) {

        int noOfExpectedTasks = ModelHelper.countNoOfExpectedTasks(categoryModel);
        int noOfPlannedTasks = ModelHelper.countNoOfTasksWithStatus(categoryModel, PlanStatus.Planned);

        return 1.0 * noOfPlannedTasks / noOfExpectedTasks;
    }

    public static Map<CategoryModel, CategoryProgressData> calculateProgressForEachCategory(List<CategoryModel> categoryModels)
    {
        HashMap<CategoryModel, CategoryProgressData> map = new HashMap<>();
        for(CategoryModel model : categoryModels)
            map.put(model, new CategoryProgressData(calculateCategoryProgress(model), calculateCategoryPlannedTasksPercentage(model)));

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

    public static class CategoryProgressData
    {
        private Double doneTasksPercentage;

        private Double plannedTasksPercentage;

        public CategoryProgressData(Double doneTasksPercentage, Double plannedTasksPercentage) {
            this.doneTasksPercentage = doneTasksPercentage;
            this.plannedTasksPercentage = plannedTasksPercentage;
        }

        public Double getDoneTasksPercentage() {
            return doneTasksPercentage;
        }

        public Double getPlannedTasksPercentage() {
            return plannedTasksPercentage;
        }
    }
}
