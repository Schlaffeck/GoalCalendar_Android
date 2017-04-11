package com.slamcode.goalcalendar.view.presenters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.android.internal.util.Predicate;
import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.SourceChangeRequestNotifier;
import com.slamcode.goalcalendar.view.activity.MonthlyGoalsActivityContract;
import com.slamcode.goalcalendar.view.dialogs.AddEditCategoryViewModelDialog;
import com.slamcode.goalcalendar.view.dialogs.base.AddEditDialog;
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

    private CategoryPlansViewModelChangeRequestListener categoryChangeRequestListener = new CategoryPlansViewModelChangeRequestListener();

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
            this.setData(new MonthlyGoalsViewModel(applicationContext, persistenceContext, summaryCalculator, categoryChangeRequestListener));
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
       this.showAddOrEditCategoryDialog(null);
    }

    private void showAddOrEditCategoryDialog(CategoryPlansViewModel viewModel)
    {
        final AddEditCategoryViewModelDialog dialog = new AddEditCategoryViewModelDialog();
        final MonthlyPlanningCategoryListViewModel monthlyPlans = this.data.getMonthlyPlans();
        dialog.setModel(viewModel);
        dialog.setMonthViewModel(this.data.getMonthlyPlans().getMonthData());
        dialog.setPlansSummaryCalculator(this.summaryCalculator);
        dialog.setDialogStateChangedListener(new AddEditDialog.DialogStateChangedListener() {
            @Override
            public void onDialogClosed(boolean confirmed) {
                if(confirmed)
                {
                    CategoryPlansViewModel newCategory = dialog.getModel();
                    if(!monthlyPlans.getCategoryPlansList().contains(newCategory)) {
                        newCategory.addSourceChangeRequestListener(new CategoryPlansViewModelChangeRequestListener());
                        monthlyPlans.getCategoryPlansList().add(newCategory);
                    }
                }
            }
        });

        this.activityView.showDialog(dialog);
    }

    private void showDeleteCategoryDialog(final CategoryPlansViewModel viewModel)
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder((Activity)this.activityView);

        dialogBuilder
                .setTitle(R.string.confirm_delete_category_dialog_header)
                .setMessage(viewModel.getName())
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        data.getMonthlyPlans().getCategoryPlansList().remove(viewModel);
                        dialogInterface.dismiss();
                        data.getMonthlyPlans().notifyPropertyChanged(BR.plansSummaryPercentage);
                    }
                })
                .setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private CategoryPlansViewModel createCategoryPlansViewModel(CategoryModel categoryModel)
    {
        CategoryPlansViewModel viewModel = new CategoryPlansViewModel(this.data.getMonthlyPlans().getMonthData(), categoryModel, this.summaryCalculator);
        viewModel.addSourceChangeRequestListener(this.categoryChangeRequestListener);
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

    private class CategoryPlansViewModelChangeRequestListener implements SourceChangeRequestNotifier.SourceChangeRequestListener<CategoryPlansViewModel>
    {
        @Override
        public void sourceChangeRequested(CategoryPlansViewModel sender, SourceChangeRequestNotifier.SourceChangeRequest request) {
            switch(request.getId()) {
                case CategoryPlansViewModel.REQUEST_REMOVE_ITEM:
                    showDeleteCategoryDialog(sender);
                    break;
                case CategoryPlansViewModel.REQUEST_MODIFY_ITEM:
                    showAddOrEditCategoryDialog(sender);
                    break;
            }
        }
    }
}
