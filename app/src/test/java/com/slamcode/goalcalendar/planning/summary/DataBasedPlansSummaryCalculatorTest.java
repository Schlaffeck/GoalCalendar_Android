package com.slamcode.goalcalendar.planning.summary;

import com.slamcode.goalcalendar.data.CategoriesRepository;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by moriasla on 16.02.2017.
 */
public class DataBasedPlansSummaryCalculatorTest {

    @Test
    public void dataBasedPlansSummaryCalculator_calculatePlansSummaryForMonth_noDataFromMonth_test() throws Exception {
        int year = 2017;
        Month month = Month.MARCH;
        CategoriesRepository repository = mockRepositoryWithDataForMonth(year, month, null);
        PlansSummaryDescriptionProvider descriptionProvider = mockDescriptionProviderWithDataForMonth(year, month, null);

        DataBasedPlansSummaryCalculator calculator = new DataBasedPlansSummaryCalculator(repository, descriptionProvider);

        PlansSummaryCalculator.PlansSummary summary = calculator.calculatePlansSummaryForMonth(year, month);
        assertNotNull(summary);
        assertEquals(false, summary.dataAvailable);
        assertEquals(0, summary.getProgressPercentage(), 0);

        verifyRepositoryWithDataForMonth(repository, year, month, null, 1);
    }

    @Test
    public void dataBasedPlansSummaryCalculator_calculatePlansSummaryForMonth_zeroFrequencyForMonth_test() throws Exception {

        int year = 2017;
        Month month = Month.MARCH;
        CategoryModel c1 = new CategoryModel(1, "C1", FrequencyPeriod.Month, 0);
        CategoriesRepository repository = mockRepositoryWithDataForMonth(year, month, null, c1);
        PlansSummaryDescriptionProvider descriptionProvider = mockDescriptionProviderWithDataForMonth(year, month, null);

        DataBasedPlansSummaryCalculator calculator = new DataBasedPlansSummaryCalculator(repository, descriptionProvider);

        PlansSummaryCalculator.PlansSummary summary = calculator.calculatePlansSummaryForMonth(year, month);
        assertNotNull(summary);
        assertEquals(false, summary.dataAvailable);
        assertEquals(0, summary.getProgressPercentage(), 0);

        verifyRepositoryWithDataForMonth(repository, year, month, null, 1);
    }

    @Test
    public void dataBasedPlansSummaryCalculator_calculatePlansSummaryForMonth_zeroFrequencyForMonth_multipleCategories_test() throws Exception {

        int year = 2017;
        Month month = Month.MARCH;
        CategoryModel c1 = new CategoryModel(1, "C1", FrequencyPeriod.Month, 0);
        CategoryModel c2 = new CategoryModel(2, "C2", FrequencyPeriod.Month, 0);
        CategoriesRepository repository = mockRepositoryWithDataForMonth(year, month, null, c1, c2);
        PlansSummaryDescriptionProvider descriptionProvider = mockDescriptionProviderWithDataForMonth(year, month, null);

        DataBasedPlansSummaryCalculator calculator = new DataBasedPlansSummaryCalculator(repository, descriptionProvider);

        PlansSummaryCalculator.PlansSummary summary = calculator.calculatePlansSummaryForMonth(year, month);
        assertNotNull(summary);
        assertEquals(false, summary.dataAvailable);
        assertEquals(0, summary.getProgressPercentage(), 0);

        verifyRepositoryWithDataForMonth(repository, year, month, null, 1);
    }

    @Test
    public void dataBasedPlansSummaryCalculator_calculatePlansSummaryForMonth_nonZeroFrequencyForMonth_singleCategory_test() throws Exception {

        int year = 2017;
        Month month = Month.MARCH;

        CategoryModel c1 = new CategoryModel(1, "C1", FrequencyPeriod.Month, 2);
        c1.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, month));

        CategoriesRepository repository = mockRepositoryWithDataForMonth(year, month, null, c1);
        PlansSummaryDescriptionProvider descriptionProvider = mockDescriptionProviderWithDataForMonth(year, month, null);

        DataBasedPlansSummaryCalculator calculator = new DataBasedPlansSummaryCalculator(repository, descriptionProvider);

        // 1 nothing done = 0%
        PlansSummaryCalculator.PlansSummary summary = calculator.calculatePlansSummaryForMonth(year, month);
        assertNotNull(summary);
        assertEquals(true, summary.dataAvailable);
        assertEquals(0, summary.getProgressPercentage(), 0);
        assertEquals(2, summary.noOfExpectedTasks);
        assertEquals(0, summary.noOfFailedTasks);
        assertEquals(0, summary.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repository, year, month, null, 1);


        // 2. one done = 50%
        c1.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        summary = calculator.calculatePlansSummaryForMonth(year, month);
        assertNotNull(summary);
        assertEquals(true, summary.dataAvailable);
        assertEquals(50, summary.getProgressPercentage(), 0);
        assertEquals(2, summary.noOfExpectedTasks);
        assertEquals(0, summary.noOfFailedTasks);
        assertEquals(1, summary.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repository, year, month, null, 2);


        // 3. two done = 100%
        c1.getDailyPlans().get(1).setStatus(PlanStatus.Success);
        summary = calculator.calculatePlansSummaryForMonth(year, month);
        assertNotNull(summary);
        assertEquals(true, summary.dataAvailable);
        assertEquals(100, summary.getProgressPercentage(), 0);
        assertEquals(2, summary.noOfExpectedTasks);
        assertEquals(0, summary.noOfFailedTasks);
        assertEquals(2, summary.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repository, year, month, null, 3);

        // 4. three done = 100% (ignored over 100%)
        c1.getDailyPlans().get(2).setStatus(PlanStatus.Success);
        summary = calculator.calculatePlansSummaryForMonth(year, month);
        assertNotNull(summary);
        assertEquals(true, summary.dataAvailable);
        assertEquals(100, summary.getProgressPercentage(), 0);
        assertEquals(2, summary.noOfExpectedTasks);
        assertEquals(0, summary.noOfFailedTasks);
        assertEquals(3, summary.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repository, year, month, null, 4);

        // 4. one done one failed = 50%
        c1.getDailyPlans().get(1).setStatus(PlanStatus.Failure);
        c1.getDailyPlans().get(2).setStatus(PlanStatus.Planned);
        summary = calculator.calculatePlansSummaryForMonth(year, month);
        assertNotNull(summary);
        assertEquals(true, summary.dataAvailable);
        assertEquals(50, summary.getProgressPercentage(), 0);
        assertEquals(2, summary.noOfExpectedTasks);
        assertEquals(1, summary.noOfFailedTasks);
        assertEquals(1, summary.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repository, year, month, null, 5);


        // 4. none done three failed = 0%
        c1.getDailyPlans().get(0).setStatus(PlanStatus.Failure);
        c1.getDailyPlans().get(1).setStatus(PlanStatus.Failure);
        c1.getDailyPlans().get(2).setStatus(PlanStatus.Failure);
        summary = calculator.calculatePlansSummaryForMonth(year, month);
        assertNotNull(summary);
        assertEquals(true, summary.dataAvailable);
        assertEquals(0, summary.getProgressPercentage(), 0);
        assertEquals(2, summary.noOfExpectedTasks);
        assertEquals(3, summary.noOfFailedTasks);
        assertEquals(0, summary.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repository, year, month, null, 6);
    }

    @Test
    public void dataBasedPlansSummaryCalculator_calculatePlansSummaryForMonth_nonZeroFrequency_multipleCategories_test() throws Exception {

        int year = 2017;
        Month month = Month.MARCH;

        CategoryModel c1 = new CategoryModel(1, "C1", FrequencyPeriod.Month, 2);
        c1.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, month));
        CategoryModel c2 = new CategoryModel(1, "C2", FrequencyPeriod.Week, 1);
        c2.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, month));

        CategoriesRepository repository = mockRepositoryWithDataForMonth(year, month, null, c1, c2);
        PlansSummaryDescriptionProvider descriptionProvider = mockDescriptionProviderWithDataForMonth(year, month, null, c1, c2);

        DataBasedPlansSummaryCalculator calculator = new DataBasedPlansSummaryCalculator(repository, descriptionProvider);

        // 1. nothing done = 0%
        PlansSummaryCalculator.PlansSummary summary = calculator.calculatePlansSummaryForMonth(year, month);
        assertNotNull(summary);
        assertEquals(true, summary.dataAvailable);
        assertEquals(0, summary.getProgressPercentage(), 0);
        assertEquals(6, summary.noOfExpectedTasks);
        assertEquals(0, summary.noOfFailedTasks);
        assertEquals(0, summary.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repository, year, month, null, 1);

        // 1. c1 1 (50%) task done c2 none (0%) =  (0.5 * 2 + 0*4)/6 = 16.(6)
        c1.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        summary = calculator.calculatePlansSummaryForMonth(year, month);
        assertNotNull(summary);
        assertEquals(true, summary.dataAvailable);
        assertEquals(16.66, summary.getProgressPercentage(), 0.01);
        assertEquals(6, summary.noOfExpectedTasks);
        assertEquals(0, summary.noOfFailedTasks);
        assertEquals(1, summary.noOfSuccessfulTasks);

        // 2. c1 1 (50%) task done c2 1 done = (0.5 * 2 + 0.25 * 4)/6 = 33.(3)%
        c2.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        summary = calculator.calculatePlansSummaryForMonth(year, month);
        assertNotNull(summary);
        assertEquals(true, summary.dataAvailable);
        assertEquals(33.33, summary.getProgressPercentage(), 0.01);
        assertEquals(6, summary.noOfExpectedTasks);
        assertEquals(0, summary.noOfFailedTasks);
        assertEquals(2, summary.noOfSuccessfulTasks);

        // 2. c1 1 done 1 failed && c2 2 done 1 failed = (0.5 * 2 + 0.5 * 4)/6 = 50%
        c1.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(1).setStatus(PlanStatus.Failure);
        c2.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(1).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(2).setStatus(PlanStatus.Failure);

        summary = calculator.calculatePlansSummaryForMonth(year, month);
        assertNotNull(summary);
        assertEquals(true, summary.dataAvailable);
        assertEquals(50, summary.getProgressPercentage(), 0);
        assertEquals(6, summary.noOfExpectedTasks);
        assertEquals(2, summary.noOfFailedTasks);
        assertEquals(3, summary.noOfSuccessfulTasks);

        // 2. c1 2 done 1 failed && c2 3 done 2 failed = (1 * 2 + 0.75 * 4)/6 = 83.33
        c1.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(1).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(2).setStatus(PlanStatus.Failure);

        c2.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(1).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(2).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(3).setStatus(PlanStatus.Failure);
        c2.getDailyPlans().get(4).setStatus(PlanStatus.Failure);
        summary = calculator.calculatePlansSummaryForMonth(year, month);
        assertNotNull(summary);
        assertEquals(true, summary.dataAvailable);
        assertEquals(83.33, summary.getProgressPercentage(), 0.01);
        assertEquals(6, summary.noOfExpectedTasks);
        assertEquals(3, summary.noOfFailedTasks);
        assertEquals(5, summary.noOfSuccessfulTasks);
    }

    @Test
    public void dataBasedPlansSummaryCalculator_calculatePlansSummaryForDay() throws Exception {

    }

    @Test
    public void  dataBasedPlansSummaryCalculator_calculatePlansSummaryForMonthInCategory_noData() throws Exception {

        int year = 2017;
        Month month = Month.APRIL;
        String categoryName = "C1";

        CategoriesRepository repositoryMock =  mockRepositoryWithDataForMonth(year, month, categoryName);
        PlansSummaryDescriptionProvider descriptionProvider = mockDescriptionProviderWithDataForMonth(year, month, null, categoryName);

        DataBasedPlansSummaryCalculator calculator = new DataBasedPlansSummaryCalculator(repositoryMock, descriptionProvider);

        // no plans - data not available
        PlansSummaryCalculator.PlansSummary result = calculator.calculatePlansSummaryForMonthInCategory(year, month, categoryName);
        assertEquals(false, result.dataAvailable);
        assertEquals(0, result.getProgressPercentage(), 0);
        assertEquals(0, result.noOfExpectedTasks);
        assertEquals(0, result.noOfFailedTasks);
        assertEquals(0, result.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repositoryMock, year, month, categoryName, 1);
    }

    @Test
    public void  dataBasedPlansSummaryCalculator_calculatePlansSummaryForMonthInCategory_singleCategory_zeroFrequency() throws Exception {

        int year = 2017;
        Month month = Month.APRIL;
        String categoryName = "C1";

        CategoryModel categoryModel = new CategoryModel(1, categoryName, FrequencyPeriod.Month, 0);
        CategoriesRepository repositoryMock =  mockRepositoryWithDataForMonth(year, month, categoryName, categoryModel);
        PlansSummaryDescriptionProvider descriptionProvider = mockDescriptionProviderWithDataForMonth(year, month, null, categoryName);

        DataBasedPlansSummaryCalculator calculator = new DataBasedPlansSummaryCalculator(repositoryMock, descriptionProvider);

        // zero freq - data not available
        PlansSummaryCalculator.PlansSummary result = calculator.calculatePlansSummaryForMonthInCategory(year, month, categoryName);
        assertEquals(false, result.dataAvailable);
        assertEquals(0, result.getProgressPercentage(), 0);
        assertEquals(0, result.noOfExpectedTasks);
        assertEquals(0, result.noOfFailedTasks);
        assertEquals(0, result.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repositoryMock, year, month, categoryName, 1);
    }

    @Test
    public void  dataBasedPlansSummaryCalculator_calculatePlansSummaryForMonthInCategory_multipleCategories_zeroFrequency() throws Exception {

        int year = 2017;
        Month month = Month.APRIL;
        String categoryName = "C1";

        CategoryModel c1 = new CategoryModel(1, categoryName, FrequencyPeriod.Month, 0);
        CategoryModel c2 = new CategoryModel(2, categoryName, FrequencyPeriod.Month, 0);
        CategoriesRepository repositoryMock =  mockRepositoryWithDataForMonth(year, month, categoryName, c1, c2);
        PlansSummaryDescriptionProvider descriptionProvider = mockDescriptionProviderWithDataForMonth(year, month, null, c1, c2);

        DataBasedPlansSummaryCalculator calculator = new DataBasedPlansSummaryCalculator(repositoryMock, descriptionProvider);

        // zero freq - data not available
        PlansSummaryCalculator.PlansSummary result = calculator.calculatePlansSummaryForMonthInCategory(year, month, categoryName);
        assertEquals(false, result.dataAvailable);
        assertEquals(0, result.getProgressPercentage(), 0);
        assertEquals(0, result.noOfExpectedTasks);
        assertEquals(0, result.noOfFailedTasks);
        assertEquals(0, result.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repositoryMock, year, month, categoryName, 1);
    }

    @Test
    public void  dataBasedPlansSummaryCalculator_calculatePlansSummaryForMonthInCategory_singleCategory_nonZeroFrequency() throws Exception {

        int year = 2017;
        Month month = Month.APRIL;
        String categoryName = "C1";

        CategoryModel categoryModel = new CategoryModel(1, categoryName, FrequencyPeriod.Month, 5);
        categoryModel.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, month));
        CategoriesRepository repositoryMock =  mockRepositoryWithDataForMonth(year, month, categoryName, categoryModel);
        PlansSummaryDescriptionProvider descriptionProvider = mockDescriptionProviderWithDataForMonth(year, month, null, categoryName);

        DataBasedPlansSummaryCalculator calculator = new DataBasedPlansSummaryCalculator(repositoryMock, descriptionProvider);

        // 1. nothing done - 0%
        PlansSummaryCalculator.PlansSummary result = calculator.calculatePlansSummaryForMonthInCategory(year, month, categoryName);
        assertEquals(true, result.dataAvailable);
        assertEquals(0, result.getProgressPercentage(), 0);
        assertEquals(5, result.noOfExpectedTasks);
        assertEquals(0, result.noOfFailedTasks);
        assertEquals(0, result.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repositoryMock, year, month, categoryName, 1);

        // 2. 2 tasks done 1 failed = 40%
        categoryModel.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        categoryModel.getDailyPlans().get(1).setStatus(PlanStatus.Success);
        categoryModel.getDailyPlans().get(2).setStatus(PlanStatus.Failure);

        result = calculator.calculatePlansSummaryForMonthInCategory(year, month, categoryName);
        assertEquals(true, result.dataAvailable);
        assertEquals(40, result.getProgressPercentage(), 0);
        assertEquals(5, result.noOfExpectedTasks);
        assertEquals(1, result.noOfFailedTasks);
        assertEquals(2, result.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repositoryMock, year, month, categoryName, 2);

        // 3. 3 tasks done 2 failed = 60%
        categoryModel.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        categoryModel.getDailyPlans().get(1).setStatus(PlanStatus.Success);
        categoryModel.getDailyPlans().get(2).setStatus(PlanStatus.Failure);
        categoryModel.getDailyPlans().get(3).setStatus(PlanStatus.Success);
        categoryModel.getDailyPlans().get(4).setStatus(PlanStatus.Failure);

        result = calculator.calculatePlansSummaryForMonthInCategory(year, month, categoryName);
        assertEquals(true, result.dataAvailable);
        assertEquals(60, result.getProgressPercentage(), 0);
        assertEquals(5, result.noOfExpectedTasks);
        assertEquals(2, result.noOfFailedTasks);
        assertEquals(3, result.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repositoryMock, year, month, categoryName, 3);

        // 4. 3 tasks done 2 failed  2 planned (ignored) = 60%
        categoryModel.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        categoryModel.getDailyPlans().get(1).setStatus(PlanStatus.Success);
        categoryModel.getDailyPlans().get(2).setStatus(PlanStatus.Failure);
        categoryModel.getDailyPlans().get(3).setStatus(PlanStatus.Success);
        categoryModel.getDailyPlans().get(4).setStatus(PlanStatus.Failure);
        categoryModel.getDailyPlans().get(5).setStatus(PlanStatus.Planned);
        categoryModel.getDailyPlans().get(6).setStatus(PlanStatus.Planned);

        result = calculator.calculatePlansSummaryForMonthInCategory(year, month, categoryName);
        assertEquals(true, result.dataAvailable);
        assertEquals(60, result.getProgressPercentage(), 0);
        assertEquals(5, result.noOfExpectedTasks);
        assertEquals(2, result.noOfFailedTasks);
        assertEquals(3, result.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repositoryMock, year, month, categoryName, 4);
    }

    @Test
    public void  dataBasedPlansSummaryCalculator_calculatePlansSummaryForMonthInCategory_multipleCategories_mixedFrequencies() throws Exception {

        int year = 2017;
        Month month = Month.APRIL;
        String categoryName = "C1";

        CategoryModel c1 = new CategoryModel(1, categoryName, FrequencyPeriod.Month, 5);
        c1.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, month));
        CategoryModel c2 = new CategoryModel(2, categoryName, FrequencyPeriod.Week, 0);
        c2.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, month));

        CategoriesRepository repositoryMock =  mockRepositoryWithDataForMonth(year, month, categoryName, c1, c2);
        PlansSummaryDescriptionProvider descriptionProvider = mockDescriptionProviderWithDataForMonth(year, month, null, c1, c2);

        DataBasedPlansSummaryCalculator calculator = new DataBasedPlansSummaryCalculator(repositoryMock, descriptionProvider);

        // zero freq - data not available
        PlansSummaryCalculator.PlansSummary result = calculator.calculatePlansSummaryForMonthInCategory(year, month, categoryName);
        assertEquals(true, result.dataAvailable);
        assertEquals(0, result.getProgressPercentage(), 0);
        assertEquals(5, result.noOfExpectedTasks);
        assertEquals(0, result.noOfFailedTasks);
        assertEquals(0, result.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repositoryMock, year, month, categoryName, 1);
    }

    @Test
    public void  dataBasedPlansSummaryCalculator_calculatePlansSummaryForMonthInCategory_multipleCategories_nonZeroFrequencies() throws Exception {

        int year = 2017;
        Month month = Month.APRIL;
        String categoryName = "C1";

        CategoryModel c1 = new CategoryModel(1, categoryName, FrequencyPeriod.Month, 6);
        c1.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, month));
        CategoryModel c2 = new CategoryModel(2, categoryName, FrequencyPeriod.Week, 1);
        c2.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, month));

        CategoriesRepository repositoryMock =  mockRepositoryWithDataForMonth(year, month, categoryName, c1, c2);
        PlansSummaryDescriptionProvider descriptionProvider = mockDescriptionProviderWithDataForMonth(year, month, null, c1, c2);

        DataBasedPlansSummaryCalculator calculator = new DataBasedPlansSummaryCalculator(repositoryMock, descriptionProvider);

        // 1. nothing done
        PlansSummaryCalculator.PlansSummary result = calculator.calculatePlansSummaryForMonthInCategory(year, month, categoryName);
        assertEquals(true, result.dataAvailable);
        assertEquals(0, result.getProgressPercentage(), 0);
        assertEquals(10, result.noOfExpectedTasks);
        assertEquals(0, result.noOfFailedTasks);
        assertEquals(0, result.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repositoryMock, year, month, categoryName, 1);

        // 2. some things done
        c1.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(1).setStatus(PlanStatus.Failure);
        c1.getDailyPlans().get(2).setStatus(PlanStatus.Planned);

        c2.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(1).setStatus(PlanStatus.Failure);
        c2.getDailyPlans().get(2).setStatus(PlanStatus.Planned);

        result = calculator.calculatePlansSummaryForMonthInCategory(year, month, categoryName);
        assertEquals(true, result.dataAvailable);
        assertEquals(20, result.getProgressPercentage(), 0);
        assertEquals(10, result.noOfExpectedTasks);
        assertEquals(2, result.noOfFailedTasks);
        assertEquals(2, result.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repositoryMock, year, month, categoryName, 2);

        // 3. everything done
        c1.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(1).setStatus(PlanStatus.Failure);
        c1.getDailyPlans().get(2).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(3).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(4).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(5).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(6).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(7).setStatus(PlanStatus.Failure);

        c2.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(1).setStatus(PlanStatus.Failure);
        c2.getDailyPlans().get(2).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(3).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(4).setStatus(PlanStatus.Failure);
        c2.getDailyPlans().get(5).setStatus(PlanStatus.Success);

        result = calculator.calculatePlansSummaryForMonthInCategory(year, month, categoryName);
        assertEquals(true, result.dataAvailable);
        assertEquals(100, result.getProgressPercentage(), 0);
        assertEquals(10, result.noOfExpectedTasks);
        assertEquals(4, result.noOfFailedTasks);
        assertEquals(10, result.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repositoryMock, year, month, categoryName, 3);

        // 4. more than everything done (ignored over 100%)
        c1.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(1).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(2).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(3).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(4).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(5).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(6).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(7).setStatus(PlanStatus.Failure);

        c2.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(1).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(2).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(3).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(4).setStatus(PlanStatus.Failure);
        c2.getDailyPlans().get(5).setStatus(PlanStatus.Success);

        result = calculator.calculatePlansSummaryForMonthInCategory(year, month, categoryName);
        assertEquals(true, result.dataAvailable);
        assertEquals(100, result.getProgressPercentage(), 0);
        assertEquals(10, result.noOfExpectedTasks);
        assertEquals(2, result.noOfFailedTasks);
        assertEquals(12, result.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repositoryMock, year, month, categoryName, 4);


        // 5. more than everything done in one, not done in other (ignored over 100%)
        // (2/3*6 + 1*4)/10  = 80%
        c1.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(1).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(2).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(3).setStatus(PlanStatus.Success);
        c1.getDailyPlans().get(4).setStatus(PlanStatus.Failure);
        c1.getDailyPlans().get(5).setStatus(PlanStatus.Planned);
        c1.getDailyPlans().get(6).setStatus(PlanStatus.Planned);
        c1.getDailyPlans().get(7).setStatus(PlanStatus.Failure);

        c2.getDailyPlans().get(0).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(1).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(2).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(3).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(4).setStatus(PlanStatus.Failure);
        c2.getDailyPlans().get(5).setStatus(PlanStatus.Success);
        c2.getDailyPlans().get(6).setStatus(PlanStatus.Success);

        result = calculator.calculatePlansSummaryForMonthInCategory(year, month, categoryName);
        assertEquals(true, result.dataAvailable);
        assertEquals(80, result.getProgressPercentage(), 0);
        assertEquals(10, result.noOfExpectedTasks);
        assertEquals(3, result.noOfFailedTasks);
        assertEquals(10, result.noOfSuccessfulTasks);

        verifyRepositoryWithDataForMonth(repositoryMock, year, month, categoryName, 5);
    }

    private PlansSummaryDescriptionProvider mockDescriptionProviderWithDataForMonth(int year, Month month, String description, CategoryModel... categories)
    {
        PlansSummaryDescriptionProvider descriptionProvider = mock(PlansSummaryDescriptionProvider.class);

        if(categories.length == 0)
            when(descriptionProvider.provideDescriptionForMonth(year, month)).thenReturn(new PlansSummaryDescriptionProvider.PlansSummaryDescription(null, description));
        else {
            for(CategoryModel categoryModel : categories)
            when(descriptionProvider.provideDescriptionMonthInCategory(year, month, categoryModel.getName())).thenReturn(description);
        }

        return descriptionProvider;
    }

    private PlansSummaryDescriptionProvider mockDescriptionProviderWithDataForMonth(int year, Month month, String description, String categoryName)
    {
        PlansSummaryDescriptionProvider descriptionProvider = mock(PlansSummaryDescriptionProvider.class);

        if(categoryName == null)
            when(descriptionProvider.provideDescriptionForMonth(year, month)).thenReturn(new PlansSummaryDescriptionProvider.PlansSummaryDescription(null, description));
        else
                when(descriptionProvider.provideDescriptionMonthInCategory(year, month, categoryName)).thenReturn(description);

        return descriptionProvider;
    }

    private CategoriesRepository mockRepositoryWithDataForMonth(int year, Month month, String categoryName, CategoryModel... categories)
    {
        CategoriesRepository repository = mock(CategoriesRepository.class);

        if(categoryName == null)
        when(repository.findForMonth(year, month))
                .thenReturn(Arrays.asList(categories));
        else
            when(repository.findForMonthWithName(year, month, categoryName))
                    .thenReturn(Arrays.asList(categories));

        return repository;
    }

    private void verifyRepositoryWithDataForMonth(CategoriesRepository repository, int year, Month month, String categoryName, int runsNo)
    {
        if(categoryName == null)
        verify(repository, times(runsNo)).findForMonth(year, month);
        else verify(repository, times(runsNo)).findForMonthWithName(year, month, categoryName);
    }
}