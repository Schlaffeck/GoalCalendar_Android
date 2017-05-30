package com.slamcode.goalcalendar.planning.summary;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.CategoriesRepository;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by smoriak on 29/05/2017.
 */
public class SimplePlansDescriptionProviderTest {

    @Test
    public void provideDescriptionForMonthInCategory_noDataAvailable_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(new ArrayList<CategoryModel>());

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 1));

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals(null, actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
    }

    @Test
    public void provideDescriptionForMonthInCategory_nothingDone_beginningOfMonth_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName);

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 1));
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_eq_zero))
                .thenReturn("PM=PC=0");

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC=0", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_eq_zero);
    }

    @Test
    public void provideDescriptionForMonthInCategory_nothingDone_monthProgressLessThanThreshold_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        // nothing done
        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 0);

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 4));
        // less than 10% of month passed
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_eq_zero))
                .thenReturn("PM=PC=0");

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC=0", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_eq_zero);
    }

    @Test
    public void provideDescriptionForMonthInCategory_nothingDone_monthProgressMoreThanThreshold_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        // nothing done
        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 0);

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 5));
        // more than 10% of month passed, nothing done still
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_gt_progressCategory_eq_zero))
                .thenReturn("PM>PC=0");

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM>PC=0", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_gt_progressCategory_eq_zero);
    }

    @Test
    public void provideDescriptionForMonthInCategory_10PercentDone_monthProgressLessThanThreshold_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 2);
        // 2 of 20 done (10%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // ~17% of month passed -> ~17%-10% = 7% < 10% threshold
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 5));
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_neq_zero))
                .thenReturn("PM=PC<>0");

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC<>0", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_neq_zero);
    }

    @Test
    public void provideDescriptionForMonthInCategory_doneMoreThanPlanned_lessThanThreshold_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 4);
        // 4 of 20 done (20%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // ~17% of month passed -> 20%-17% = 3% < 10% threshold
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 5));
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_neq_zero))
                .thenReturn("PM=PC<>0");

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC<>0", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_neq_zero);
    }

    @Test
    public void provideDescriptionForMonthInCategory_doneMoreThanPlanned_moreThanThreshold_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 7);
        // 4 of 20 done (20%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // ~22% of month passed -> 22% - 35% = -13% > -10% threshold
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 7));
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_lt_progressCategory_neq_zero))
                .thenReturn("PM<PC<>0");

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM<PC<>0", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_lt_progressCategory_neq_zero);
    }

    @Test
    public void provideDescriptionForMonthInCategory_allDone_monthFinished_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 20);
        // 20 of 20 done (100%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // month passed
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 31));
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_done))
                .thenReturn("PM=PC=1");

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC=1", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_done);
    }

    @Test
    public void provideDescriptionForMonthInCategory_allDone_moreThanThresholdTillEndOfMonth_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 20);
        // 20 of 20 done (100%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // still 6/31 = ~19% till the eond of week > 10% threshold
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 25));
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_doneBeforeEndOfMonth))
                .thenReturn("PM<PC=1");

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM<PC=1", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_doneBeforeEndOfMonth);
    }

    @Test
    public void provideDescriptionForMonthInCategory_overDone_moreThanThresholdTillEndOfMonth_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 21);
        // 21 of 20 done (1005%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // still 4/31 = ~12% till the end of week > 10% threshold
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 28));
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_overDoneBeforeEndOfMonth))
                .thenReturn("PM<PC>1");

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM<PC>1", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_overDoneBeforeEndOfMonth);
    }

    @Test
    public void provideDescriptionForMonthInCategory_overDone_lessThanThresholdTillEndOfMonth_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 21);
        // 21 of 20 done (1005%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // still 3/31 = ~9% till the end of week < 10% threshold
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 29));
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_overDone))
                .thenReturn("PM<PC>1");

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM<PC>1", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_overDone);
    }

    private CategoryModel createCategoryModel(int year, Month month, String categoryName)
    {
        return createCategoryModel(year, month, categoryName, FrequencyPeriod.Week, 1, 0);
    }

    private CategoryModel createCategoryModel(int year, Month month, String categoryName, FrequencyPeriod frequencyPeriod, int frequencyRatio, int noOfSuccessfulTasks)
    {
        CategoryModel categoryModel = new CategoryModel(1, categoryName, frequencyPeriod, frequencyRatio);
        categoryModel.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, month));

        for(int i =0; i<noOfSuccessfulTasks; i++)
        {
            categoryModel.getDailyPlans().get(i).setStatus(PlanStatus.Success);
        }
        return categoryModel;
    }
}