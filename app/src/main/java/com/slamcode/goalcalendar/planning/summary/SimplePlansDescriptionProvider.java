package com.slamcode.goalcalendar.planning.summary;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementSelector;
import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.CategoriesRepository;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import org.apache.commons.collections4.KeyValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        PlansSummaryDescription result = new PlansSummaryDescription();

        List<CategoryModel> categories = categoriesRepository.findForMonth(year, month);
        if(categories == null || categories.size() == 0) {
            result.setTitle(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_noCategories_title));
            result.setDetails(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_noCategories_description));
        }
        else
        {
            double monthPlansProgress = SummaryCalculatorUtils.calculateCategoryProgress(categories);
            DateTime dateTimeNow = this.applicationContext.getDateTimeNow();
            double monthProgress = SummaryCalculatorUtils.calculateMonthProgress(year, month, dateTimeNow);
            if(monthPlansProgress == 0.0)
            {
                if(monthProgress > 0) {
                    result.setTitle(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_nothingDone_title));
                    result.setDetails(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_nothingDone_description));
                }
                else
                {
                    result.setTitle(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_monthJustStarted_title));
                    result.setDetails(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_monthJustStarted_description));
                }
            }
            else{
                // calculate each category independently
                Map<CategoryModel, Double> map = SummaryCalculatorUtils.calculateProgressForEachCategory(categories);

                int notStartedCategories = CollectionUtils.sum(map.values(), new ElementSelector<Double, Integer>() {
                    @Override
                    public Integer select(Double parent) {
                        return parent == 0 ? 1 : 0;
                    }
                });

                if(notStartedCategories == 0)
                {
                    // all cats in progress
                    if(monthPlansProgress >= monthProgress + MONTH_TO_CATEGORY_PROGRESS_THRESHOLD)
                    {
                        result.setTitle(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_progressFaster_title));
                        result.setDetails(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_progressFaster_description));
                    }
                    else if(monthPlansProgress <= monthProgress - MONTH_TO_CATEGORY_PROGRESS_THRESHOLD)
                    {
                        result.setTitle(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_progressSlower_title));
                        result.setDetails(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_progressSlower_description));
                    }
                    else
                    {
                        result.setTitle(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_progressRegular_title));
                        result.setDetails(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_progressRegular_description));
                    }
                }
                else if(notStartedCategories == 1)
                {
                    // single category left alone
                    Map.Entry<CategoryModel, Double> notStarted = CollectionUtils.singleOrDefault(map.entrySet(), new ElementSelector<Map.Entry<CategoryModel, Double>, Boolean>() {
                        @Override
                        public Boolean select(Map.Entry<CategoryModel, Double> parent) {
                            return parent.getValue() == 0;
                        }
                    });
                    result.setTitle(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_oneCategoryNotStarted_title));
                    result.setDetails(
                            String.format(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_oneCategoryNotStarted_description), notStarted.getKey().getName()));
                }
                else
                {
                    // some in progress some not started
                    if(notStartedCategories < dateTimeNow.getDay())
                    {
                        // more not done than days
                        result.setTitle(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_someCategoriesNotStarted_title));
                        result.setDetails(
                                String.format(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_someCategoriesNotStarted_description), notStartedCategories));
                    }
                }

            }

        }
        return result;
    }

    @Override
    public String provideDescriptionMonthInCategory(int year, Month month, String categoryName) {
        List<CategoryModel> categoryModels = categoriesRepository.findForMonthWithName(year, month, categoryName);
        double monthProgress = SummaryCalculatorUtils.calculateMonthProgress(year, month, this.applicationContext.getDateTimeNow());
        double categoryProgress = SummaryCalculatorUtils.calculateCategoryProgress(categoryModels);

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
}
