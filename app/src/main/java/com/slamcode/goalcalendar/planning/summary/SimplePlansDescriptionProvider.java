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
    private static final double MONTH_PROGRESS_THRESHOLD = 0.16;
    private static final double END_OF_MONTH_THRESHOLD = 0.96;

    private final ApplicationContext applicationContext;
    private final CategoriesRepository categoriesRepository;

    public SimplePlansDescriptionProvider(ApplicationContext applicationContext, CategoriesRepository categoriesRepository)
    {
        this.applicationContext = applicationContext;
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public PlansSummaryDescription provideDescriptionForMonth(int year, Month month) {
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

        if(monthProgress < 0.0) {
            if(categoryProgress > 0)
                return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_monthNotStartedYet_somethingDone);

            return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_monthNotStartedYet);
        }
        double monthToCategoryProgressDiff = monthProgress - (categoryProgress > 1 ? 1.0 : categoryProgress);

        boolean isBeginningOfMonth = monthProgress == 0.0;
        boolean isLittleAfterBeginningOfMonth = monthProgress < MONTH_PROGRESS_THRESHOLD;
        boolean isEndOfMonth = monthProgress > END_OF_MONTH_THRESHOLD;
        boolean isAlmostEndOfMonth = 1.0 - monthProgress < MONTH_PROGRESS_THRESHOLD;

        boolean isWorkDone = categoryProgress == 1.0;
        boolean isWorkOverdone = categoryProgress > 1.0;

        boolean isBehindInPlans = monthToCategoryProgressDiff > MONTH_TO_CATEGORY_PROGRESS_THRESHOLD;
        boolean isAheadOfPlans = monthToCategoryProgressDiff < -MONTH_TO_CATEGORY_PROGRESS_THRESHOLD;

        if(isBeginningOfMonth)
        {
            return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_eq_zero);
        }
        else if(isLittleAfterBeginningOfMonth)
        {
            if(categoryProgress == 0)
                return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_gt_progressCategory_eq_zero);

            return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_lt_progressCategory_eq_zero);
        }
        else if(isEndOfMonth)
        {
            if(isWorkDone)
                return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_done);

            if(isWorkOverdone)
                return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_overDone);

            return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_notDone);
        }
        else if(isAlmostEndOfMonth && !isWorkDone && !isWorkOverdone)
        {
            return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_notDoneBeforeEndOfMonth);
        }

        // middle of the month
        if(categoryProgress == 0)
            return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_gt_progressCategory_eq_zero);

        if(isWorkDone)
            return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_doneBeforeEndOfMonth);

        if(isWorkOverdone)
            return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_overDoneBeforeEndOfMonth);

        if(isAheadOfPlans)
            return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_lt_progressCategory_neq_zero);

        if(isBehindInPlans)
            return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_gt_progressCategory_neq_zero);

        return this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_neq_zero);
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
