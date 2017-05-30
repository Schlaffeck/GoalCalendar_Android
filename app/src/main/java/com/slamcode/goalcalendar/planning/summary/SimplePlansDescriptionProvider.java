package com.slamcode.goalcalendar.planning.summary;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.CategoriesRepository;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import java.util.List;

/**
 * Created by smoriak on 29/05/2017.
 */

public class SimplePlansDescriptionProvider implements PlansSummaryDescriptionProvider {

    private static final double MONTH_TO_CATEGORY_PROGRESS_THRESHOLD = 0.1;

    private final ApplicationContext applicationContext;
    private final CategoriesRepository categoriesRepository;

    public SimplePlansDescriptionProvider(ApplicationContext applicationContext, CategoriesRepository categoriesRepository)
    {
        this.applicationContext = applicationContext;
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public String provideDescriptionForMonth(int year, Month month) {
        return null;
    }

    @Override
    public String provideDescriptionMonthInCategory(int year, Month month, String categoryName) {
        List<CategoryModel> categoryModels = categoriesRepository.findForMonthWithName(year, month, categoryName);
        double monthProgress = calculateMonthProgress(year, month);
        double categoryProgress = calculateCategoryProgress(categoryModels);

        String generalProgressDescription = this.provideGeneralProgressDescription(monthProgress, categoryProgress);

        return generalProgressDescription;
    }

    private String provideGeneralProgressDescription(double monthProgress, double categoryProgress) {

        double monthToCategoryProgressDiff = monthProgress - (categoryProgress > 1 ? 1.0 : categoryProgress);

        if(monthToCategoryProgressDiff > MONTH_TO_CATEGORY_PROGRESS_THRESHOLD)
        {
            if(categoryProgress > 0)
                return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_gt_progressCategory_neq_zero);

            return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_gt_progressCategory_eq_zero);
        }

        if(monthToCategoryProgressDiff < -MONTH_TO_CATEGORY_PROGRESS_THRESHOLD) {
            if (categoryProgress == 1.0)
                return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_doneBeforeEndOfMonth);

            if(categoryProgress > 1)
                return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_overDoneBeforeEndOfMonth);

                return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_lt_progressCategory_neq_zero);
        }

        if(categoryProgress == 1.0)
            return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_done);

        if(categoryProgress > 1)
            return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_overDone);

        if(categoryProgress > 0)
            return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_neq_zero);

        return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_eq_zero);
    }

    private double calculateCategoryProgress(List<CategoryModel> categoryModels) {

        int sumOfTasks = 0;
        int tasksDone = 0;
        for (CategoryModel categoryModel : categoryModels)
        {
            sumOfTasks += ModelHelper.countNoOfExpectedTasks(categoryModel);
            tasksDone += ModelHelper.countNoOfTasksWithStatus(categoryModel, PlanStatus.Success);
        }

        return 1.0 * tasksDone / sumOfTasks;
    }

    private double calculateMonthProgress(int year, Month month)
    {
        DateTime dateTimeNow = this.applicationContext.getDateTimeNow();
        double monthProgress = 1.0 * (dateTimeNow.getDay() -1) / DateTimeHelper.getDaysCount(year, month);
        return monthProgress;
    }
}
