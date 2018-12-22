package com.slamcode.goalcalendar.service.commands;

import android.app.Activity;

import com.slamcode.goalcalendar.commands.CommandBaseImpl;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.unitofwork.UnitOfWork;
import com.slamcode.goalcalendar.data.model.plans.CategoryModel;
import com.slamcode.goalcalendar.data.query.NumericalComparisonOperator;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryDescriptionProvider;
import com.slamcode.goalcalendar.settings.AppSettingsManager;
import com.slamcode.goalcalendar.view.dialogs.DailyProgressDialog;

import java.util.List;
import java.util.Random;

/**
 * Command showing daily progress dialog on given activity
 */

public class ShowDialogDailyProgressInfoCommand extends CommandBaseImpl<Activity, Boolean> implements DailyProgressInfoCommand {

    private final AppSettingsManager settingsManager;
    private final PlansSummaryDescriptionProvider descriptionProvider;
    private final PersistenceContext persistenceContext;

    private boolean launchTimeSet;

    public ShowDialogDailyProgressInfoCommand(AppSettingsManager settingsManager,
                                              PlansSummaryDescriptionProvider descriptionProvider,
                                              PersistenceContext persistenceContext)
    {
        this.settingsManager = settingsManager;
        this.descriptionProvider = descriptionProvider;
        this.persistenceContext = persistenceContext;
    }

    @Override
    protected Boolean executeCore(Activity activity) {
        DateTime lastLaunchDateTime = this.settingsManager.getLastLaunchDateTime();

        if(!this.launchTimeSet
                && !this.isFirstLaunch()
                && DateTimeHelper.isDateBefore(lastLaunchDateTime, DateTimeHelper.getTodayDateTime()))
        {
            this.setLaunchTime();
            int year = DateTimeHelper.getCurrentYear();
            Month month = DateTimeHelper.getCurrentMonth();
            PlansSummaryDescriptionProvider.PlansSummaryDescription description
                    = this.descriptionProvider.provideDescriptionForMonth(year, month);
            List<CategoryModel> unfinishedCategories = this.getUnfinishedCategories(year, month);
            if(unfinishedCategories.size() > 0) {
                CategoryModel randomCat = unfinishedCategories.get((new Random()).nextInt(unfinishedCategories.size()));
                String randomUnfinishedCatDescription = this.descriptionProvider.provideDescriptionMonthInCategory(year, month, randomCat.getName());

                if(randomUnfinishedCatDescription != null)
                    description.setDetails(String.format("%s %n%n%s - %s",  description.getDetails(), randomCat.getName(), randomUnfinishedCatDescription));
            }

            if(description != null) {
                DailyProgressDialog dialog = new DailyProgressDialog();
                dialog.setModel(description);
                dialog.show(activity.getFragmentManager(), "DailyProgressDialog");
            }

            return true;
        }

        setLaunchTime();

        return false;
    }

    @Override
    public boolean canRevert() {
        return false;
    }

    @Override
    protected void revertCore() {
    }

    private boolean isFirstLaunch()
    {
        return this.settingsManager.getLastLaunchDateTime() == null;
    }

    private void setLaunchTime()
    {
        if(!this.launchTimeSet) {
            this.settingsManager.setLastLaunchDateTimeMillis(DateTimeHelper.getNowDateTime());
            this.launchTimeSet = true;
        }
    }

    private List<CategoryModel> getUnfinishedCategories(int year, Month month)
    {
        UnitOfWork uow = this.persistenceContext.createUnitOfWork();

        List<CategoryModel> categoryModelList =  uow.getCategoriesRepository().findWithProgressInMonth(year, month, NumericalComparisonOperator.LESS_THAN, 1.0f);
        uow.complete(true);
        return categoryModelList;
    }
}
