package com.slamcode.goalcalendar.view.presenters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.LayoutInflater;

import com.android.databinding.library.baseAdapters.BR;
import com.android.internal.util.Predicate;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.AddEditCategoryDialog;
import com.slamcode.goalcalendar.view.CategoryDailyPlansRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.CategoryNameRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.lists.ListAdapterProvider;
import com.slamcode.goalcalendar.view.viewmodels.MonthlyProgressSummaryViewModel;

/**
 * Created by moriasla on 16.01.2017.
 */

public class PersistentMonthlyGoalsPresenter extends BaseObservable implements MonthlyGoalsPresenter {

    private MonthlyPlansModel selectedMonthlyPlans;

    private MonthlyProgressSummaryViewModel progressSummaryValue;

    private final CategoryNameRecyclerViewAdapter categoryNamesRecyclerViewAdapter;
    private final CategoryDailyPlansRecyclerViewAdapter categoryDailyPlansRecyclerViewAdapter;

    private final Context context;

    private final PersistenceContext persistenceContext;

    private PlansSummaryCalculator summaryCalculator;

    public PersistentMonthlyGoalsPresenter(Context context,
                                           LayoutInflater layoutInflater,
                                           PersistenceContext persistenceContext,
                                           ListAdapterProvider listAdapterProvider, PlansSummaryCalculator summaryCalculator)
    {
        this(context, layoutInflater, persistenceContext, listAdapterProvider, false, summaryCalculator);
    }

    public PersistentMonthlyGoalsPresenter(Context context,
                                           LayoutInflater layoutInflater,
                                           PersistenceContext persistenceContext,
                                           ListAdapterProvider listAdapterProvider,
                                           boolean setupCategoriesList, PlansSummaryCalculator summaryCalculator)
    {
        this.context = context;
        this.persistenceContext = persistenceContext;
        this.summaryCalculator = summaryCalculator;
        this.categoryNamesRecyclerViewAdapter = listAdapterProvider.provideCategoryNameListViewAdapter(context,layoutInflater);
        this.categoryDailyPlansRecyclerViewAdapter = listAdapterProvider.provideCategoryDailyPlansListViewAdapter(context,layoutInflater);
        if(setupCategoriesList)
            this.setYearAndMonth(this.getSelectedYear(), this.getSelectedMonth());
    }

    @Override
    public void setYearAndMonth(int year, Month month) {

        this.categoryNamesRecyclerViewAdapter.updateMonthlyPlans(null);
        this.categoryDailyPlansRecyclerViewAdapter.updateMonthlyPlans(null);
        UnitOfWork uow = persistenceContext.createUnitOfWork();

        MonthlyPlansModel model = uow.getMonthlyPlansRepository().findForMonth(year, month);

        if(model == null)
        {
            model = new MonthlyPlansModel();
            // todo: find good way to assign ids
            model.setId(year ^ month.getNumValue());
            model.setYear(year);
            model.setMonth(month);
            uow.getMonthlyPlansRepository().add(model);
        }

        uow.complete();

        this.selectedMonthlyPlans = model;
        this.progressSummaryValue = null;
        this.categoryNamesRecyclerViewAdapter.updateMonthlyPlans(selectedMonthlyPlans);
        this.categoryDailyPlansRecyclerViewAdapter.updateMonthlyPlans(selectedMonthlyPlans);
    }

    @Override
    public CategoryNameRecyclerViewAdapter getCategoryNamesRecyclerViewAdapter() {
        return categoryNamesRecyclerViewAdapter;
    }

    @Override
    public CategoryDailyPlansRecyclerViewAdapter getCategoryDailyPlansRecyclerViewAdapter() {
        return this.categoryDailyPlansRecyclerViewAdapter;
    }

    @Override
    public boolean isEmptyCategoriesList()
    {
        return this.selectedMonthlyPlans == null
            || this.selectedMonthlyPlans.getCategories() == null
            || this.selectedMonthlyPlans.getCategories().isEmpty();
    }

    @Override
    public boolean canCopyCategoriesFromPreviousMonth()
    {
        return this.isEmptyCategoriesList()
                && this.findPreviousMonthlyPlansModelWithCategories() != null;
    }

    @Override
    public int getSelectedYear()
    {
        if(this.selectedMonthlyPlans == null)
        {
            return DateTimeHelper.getCurrentYear();
        }

        return this.selectedMonthlyPlans.getYear() > 0 ?
                this.selectedMonthlyPlans.getYear()
                : DateTimeHelper.getCurrentYear();
    }

    @Override
    public Month getSelectedMonth()
    {
        if(this.selectedMonthlyPlans == null)
        {
            return Month.getCurrentMonth();
        }

        return this.selectedMonthlyPlans.getMonth();
    }

    @Override
    public String getCategoryNameOnPosition(int categoryPosition)
    {
        CategoryModel categoryModel = this.categoryNamesRecyclerViewAdapter.getItem(categoryPosition);
        return categoryModel == null ? null : categoryModel.getName();
    }

    @Override
    public void copyCategoriesFromPreviousMonth()
    {
        MonthlyPlansModel previousMonthModel = findPreviousMonthlyPlansModelWithCategories();
        if(previousMonthModel == null || previousMonthModel.getCategories() == null)
            return;

        int year = this.selectedMonthlyPlans.getYear();
        Month currentMonth = this.selectedMonthlyPlans.getMonth();
        for(CategoryModel category : previousMonthModel.getCategories())
        {
            CategoryModel newCategory = new CategoryModel(
                    category.getId() ^ currentMonth.getNumValue(),
                    category.getName(),
                    category.getPeriod(),
                    category.getFrequencyValue());
            newCategory.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, currentMonth));

            this.categoryNamesRecyclerViewAdapter.addOrUpdateItem(newCategory);
            this.categoryDailyPlansRecyclerViewAdapter.addOrUpdateItem(newCategory);
            notifyPropertyChanged(BR.progressSummaryValue);
            refreshPlansSummaryData();
        }
    }

    @Override
    public AddEditCategoryDialog createAddEditCategoryDialog(int categoryPosition)
    {
        final CategoryModel model = categoryPosition >= 0 ?
                this.categoryNamesRecyclerViewAdapter.getItem(categoryPosition)
                : null;
        final AddEditCategoryDialog dialog = new AddEditCategoryDialog();
        dialog.setYearAndMonth(this.getSelectedYear(), this.getSelectedMonth());
        dialog.setModel(model);
        dialog.setDialogStateChangedListener(new AddEditCategoryDialog.DialogStateChangedListener() {
            @Override
            public void onDialogClosed(boolean confirmed) {
                if(confirmed)
                {
                    CategoryModel newCategory = dialog.getModel();
                    if(!selectedMonthlyPlans.getCategories().contains(newCategory))
                        selectedMonthlyPlans.getCategories().add(newCategory);
                    categoryNamesRecyclerViewAdapter.addOrUpdateItem(newCategory);
                    categoryDailyPlansRecyclerViewAdapter.addOrUpdateItem(newCategory);
                    notifyPropertyChanged(BR.progressSummaryValue);
                    refreshPlansSummaryData();
                }
            }
        });

        return dialog;
    }

    @Override
    public AlertDialog createDeleteCategoryDialog(int categoryPosition)
    {
        final CategoryModel model = this.categoryNamesRecyclerViewAdapter.getItem(categoryPosition);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);

        dialogBuilder
                .setTitle(R.string.confirm_delete_category_dialog_header)
                .setMessage(model.getName())
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedMonthlyPlans.getCategories().remove(model);
                        categoryNamesRecyclerViewAdapter.removeItem(model);
                        categoryDailyPlansRecyclerViewAdapter.removeItem(model);
                        dialogInterface.dismiss();
                        notifyPropertyChanged(BR.progressSummaryValue);
                        refreshPlansSummaryData();
                    }
                })
                .setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        dialogInterface.dismiss();
                    }
                });

        return dialogBuilder.create();
    }

    @Override
    @Bindable
    public MonthlyProgressSummaryViewModel getProgressSummaryValue() {
        if(this.progressSummaryValue == null)
            this.progressSummaryValue = new MonthlyProgressSummaryViewModel(this.summaryCalculator, this.selectedMonthlyPlans);
        return this.progressSummaryValue;
    }

    private void refreshPlansSummaryData()
    {
        if(this.progressSummaryValue != null)
            this.progressSummaryValue.refreshData();
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
