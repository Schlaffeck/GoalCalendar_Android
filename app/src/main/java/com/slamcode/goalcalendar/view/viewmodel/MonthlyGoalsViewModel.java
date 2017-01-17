package com.slamcode.goalcalendar.view.viewmodel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import com.android.internal.util.Predicate;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.view.AddEditCategoryDialog;
import com.slamcode.goalcalendar.view.CategoryListViewAdapter;

/**
 * Created by moriasla on 16.01.2017.
 */

public class MonthlyGoalsViewModel {

    private MonthlyPlansModel currentMonthlyPlans;

    private final CategoryListViewAdapter monthlyPlannedCategoryListViewAdapter;

    private final Context context;

    private final PersistenceContext persistenceContext;

    public MonthlyGoalsViewModel(Context context, LayoutInflater layoutInflater, PersistenceContext persistenceContext)
    {
        this.context = context;
        this.persistenceContext = persistenceContext;
        monthlyPlannedCategoryListViewAdapter = new CategoryListViewAdapter(context,layoutInflater);
    }

    public void setYearAndMonth(int year, Month month) {
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

        this.currentMonthlyPlans = model;
        this.monthlyPlannedCategoryListViewAdapter.updateMonthlyPlans(currentMonthlyPlans);
    }

    public CategoryListViewAdapter getMonthlyPlannedCategoryListViewAdapter() {
        return monthlyPlannedCategoryListViewAdapter;
    }

    public boolean isEmptyCategoriesList()
    {
        return this.currentMonthlyPlans == null
            || this.currentMonthlyPlans.getCategories() == null
            || this.currentMonthlyPlans.getCategories().isEmpty();
    }

    public boolean canCopyCategoriesFromPreviousMonth()
    {
        return this.isEmptyCategoriesList()
                && this.findPreviousMonthlyPlansModelWithCategories() != null;
    }

    public int getCurrentYear()
    {
        if(this.currentMonthlyPlans == null)
        {
            return DateTimeHelper.getCurrentYear();
        }

        return this.currentMonthlyPlans.getYear() > 0 ?
                this.currentMonthlyPlans.getYear()
                : DateTimeHelper.getCurrentYear();
    }

    public Month getCurrentMonth()
    {
        if(this.currentMonthlyPlans == null)
        {
            return Month.getCurrentMonth();
        }

        return this.currentMonthlyPlans.getMonth();
    }

    public String getCategoryNameOnPosition(int categoryPosition)
    {
        CategoryModel categoryModel = this.monthlyPlannedCategoryListViewAdapter.getItem(categoryPosition);
        return categoryModel == null ? null : categoryModel.getName();
    }

    public void copyCategoriesFromPreviousMonth()
    {
        MonthlyPlansModel previousMonthModel = findPreviousMonthlyPlansModelWithCategories();
        if(previousMonthModel == null || previousMonthModel.getCategories() == null)
            return;

        int year = this.currentMonthlyPlans.getYear();
        Month currentMonth = this.currentMonthlyPlans.getMonth();
        for(CategoryModel category : previousMonthModel.getCategories())
        {
            CategoryModel newCategory = new CategoryModel(
                    category.getId() ^ currentMonth.getNumValue(),
                    category.getName(),
                    category.getPeriod(),
                    category.getFrequencyValue());
            newCategory.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, currentMonth));

            this.monthlyPlannedCategoryListViewAdapter.addOrUpdateItem(newCategory);
        }
    }

    public AddEditCategoryDialog createAddEditCategoryDialog(int categoryPosition)
    {
        final CategoryModel model = categoryPosition >= 0 ?
                this.monthlyPlannedCategoryListViewAdapter.getItem(categoryPosition)
                : null;
        final AddEditCategoryDialog dialog = new AddEditCategoryDialog();
        dialog.setModel(model);
        dialog.setDialogStateChangedListener(new AddEditCategoryDialog.DialogStateChangedListener() {
            @Override
            public void onDialogClosed(boolean confirmed) {
                if(confirmed)
                {
                    CategoryModel newCategory = dialog.getModel();
                    monthlyPlannedCategoryListViewAdapter.addOrUpdateItem(newCategory);
                }
            }
        });

        return dialog;
    }

    public AlertDialog createDeleteCategoryDialog(int categoryPosition)
    {
        final CategoryModel model = this.monthlyPlannedCategoryListViewAdapter.getItem(categoryPosition);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);

        dialogBuilder
                .setTitle(R.string.confirm_delete_category_dialog_header)
                .setMessage(model.getName())
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        monthlyPlannedCategoryListViewAdapter.removeItem(model);
                        dialogInterface.dismiss();
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
        uow.complete();

        return previousMonthWithCategories;
    }
}
