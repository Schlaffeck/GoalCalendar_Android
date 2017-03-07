package com.slamcode.goalcalendar.view.presenters;

import android.view.View;

import com.android.internal.util.Predicate;
import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.activity.MonthlyGoalsActivityContract;
import com.slamcode.goalcalendar.view.dialogs.AddEditCategoryViewModelDialog;
import com.slamcode.goalcalendar.view.dialogs.AddEditDialog;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;
import com.slamcode.goalcalendar.viewmodels.MonthlyGoalsViewModel;
import com.slamcode.goalcalendar.viewmodels.MonthlyPlanningCategoryListViewModel;

/**
 * Created by moriasla on 16.01.2017.
 */

public class PersistentMonthlyGoalsPresenter implements MonthlyGoalsPresenter {

    private final ApplicationContext applicationContext;

    private final PersistenceContext persistenceContext;

    private PlansSummaryCalculator summaryCalculator;

    private MonthlyGoalsViewModel data;

    private MonthlyGoalsActivityContract.ActivityView activityView;

    public PersistentMonthlyGoalsPresenter(
            ApplicationContext applicationContext,
            PersistenceContext persistenceContext,
            PlansSummaryCalculator summaryCalculator)
    {
        this.applicationContext = applicationContext;
        this.persistenceContext = persistenceContext;
        this.summaryCalculator = summaryCalculator;
    }

    @Override
    public MonthlyGoalsViewModel getData() {
        return this.data;
    }

    @Override
    public void setData(MonthlyGoalsViewModel data) {
        if(this.data != data)
        {
            this.data = data;
            this.activityView.onDataSet(data);
        }
    }

    @Override
    public void initializeWithView(MonthlyGoalsActivityContract.ActivityView view) {
        this.activityView = view;
        if(this.data == null)
            this.setData(new MonthlyGoalsViewModel(applicationContext, persistenceContext, summaryCalculator));
    }

    @Override
    public boolean isPreviousMonthWithCategoriesAvailable()
    {
        return this.findPreviousMonthlyPlansModelWithCategories() != null;
    }

    @Override
    public void copyCategoriesFromPreviousMonth(View view) {
        MonthlyPlansModel previousMonthModel = findPreviousMonthlyPlansModelWithCategories();
        if(previousMonthModel == null || previousMonthModel.getCategories() == null)
            return;

        int year = this.data.getYear();
        Month currentMonth = this.data.getMonth();
        for(CategoryModel category : previousMonthModel.getCategories())
        {
            CategoryModel newCategory = new CategoryModel(
                    category.getId() ^ currentMonth.getNumValue(),
                    category.getName(),
                    category.getPeriod(),
                    category.getFrequencyValue());
            newCategory.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, currentMonth));

            this.data.getMonthlyPlans().getCategoryPlansList()
                    .add(this.createCategoryPlansViewModel(category));
        }
    }

    @Override
    public void goToNextYear(View view) {
        this.data.setYear(this.data.getYear() + 1);
    }

    @Override
    public void goToPreviousYear(View view) {
        this.data.setYear(this.data.getYear() - 1);
    }

    @Override
    public void goToNextMonth(View view) {
        this.data.setMonth(Month.getNextMonth(this.data.getMonth()));
    }

    @Override
    public void goToPreviousMonth(View view) {
        this.data.setMonth(Month.getPreviousMonth(this.data.getMonth()));
    }

    @Override
    public void showAddNewCategoryDialog(View view) {
        final AddEditCategoryViewModelDialog dialog = new AddEditCategoryViewModelDialog();
        final MonthlyPlanningCategoryListViewModel monthlyPlans = this.data.getMonthlyPlans();
        dialog.setMonthViewModel(this.data.getMonthlyPlans().getMonthData());
        dialog.setPlansSummaryCalculator(this.summaryCalculator);
        dialog.setDialogStateChangedListener(new AddEditDialog.DialogStateChangedListener() {
            @Override
            public void onDialogClosed(boolean confirmed) {
                if(confirmed)
                {
                    CategoryPlansViewModel newCategory = dialog.getModel();
                    if(!monthlyPlans.getCategoryPlansList().contains(newCategory))
                        monthlyPlans.getCategoryPlansList().add(newCategory);
                }
            }
        });

        this.activityView.showDialog(dialog);
    }

    private CategoryPlansViewModel createCategoryPlansViewModel(CategoryModel categoryModel)
    {
        CategoryPlansViewModel viewModel = new CategoryPlansViewModel(this.data.getMonthlyPlans().getMonthData(), categoryModel, this.summaryCalculator);
        return viewModel;
    }

    private MonthlyPlansModel findPreviousMonthlyPlansModelWithCategories()
    {
        UnitOfWork uow = this.persistenceContext.createUnitOfWork();
        MonthlyPlansModel previousMonthWithCategories = uow.getMonthlyPlansRepository().findLast(new Predicate<MonthlyPlansModel>() {
            @Override
            public boolean apply(MonthlyPlansModel monthlyPlansModel) {
                if(monthlyPlansModel.getMonth().getNumValue() < monthlyPlansModel.getMonth().getNumValue())
                {
                    return false;
                }

                return !monthlyPlansModel.getCategories().isEmpty();
            }
        });
        uow.complete(false);

        return previousMonthWithCategories;
    }
}
