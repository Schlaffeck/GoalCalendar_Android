package com.slamcode.goalcalendar.service.commands;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.commands.CommandBaseImpl;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by moriasla on 10.02.2017.
 */

public class SnackbarShowUpAutoMarkTasksCommand extends CommandBaseImpl<View, AutoMarkTasksCommand.AutoMarkResult> implements AutoMarkTasksCommand {

    private static final String LOG_TAG = "GOAL_AutoMarkCmd";

    private final ApplicationContext context;
    private PersistenceContext persistenceContext;

    private AppSettingsManager settingsManager;

    private Collection<DailyPlanModel> autoMarkedDailyPlans = new ArrayList<>();

    public SnackbarShowUpAutoMarkTasksCommand(ApplicationContext context, PersistenceContext persistenceContext, AppSettingsManager settingsManager)
    {
        this.context = context;
        this.persistenceContext = persistenceContext;
        this.settingsManager = settingsManager;
    }

    @Override
    public AutoMarkResult executeCore(View view) {

        AutoMarkResult result = new AutoMarkResult();

        if(!this.settingsManager.getAutomaticallyMarkUncompletedTask())
            return result;

            UnitOfWork uow = this.persistenceContext.createUnitOfWork();

            DateTime yesterday = DateTimeHelper.getYesterdayDateTime();
            List<CategoryModel> categoryModels = uow.getCategoriesRepository()
                    .findForDateWithStatus(yesterday.getYear(), yesterday.getMonth(), yesterday.getDay(), PlanStatus.Planned);

            for(CategoryModel model : categoryModels)
            {
                if(model.getDailyPlans().size() >= yesterday.getDay())
                {
                    DailyPlanModel dailyPlanModel = model.getDailyPlans().get(yesterday.getDay()-1);
                    this.autoMarkedDailyPlans.add(dailyPlanModel);
                    dailyPlanModel.setStatus(PlanStatus.Failure);
                }
            }

            result.setWasRun(true);
            result.setUnfinishedTasksMarkedFailedCount(categoryModels.size());

            uow.complete();

        if(autoMarkedDailyPlans.size() > 0)
            this.context.showSnackbar(
                    view,
                    this.context.getStringFromResources(R.string.notification_autoMarked_snackbar_content),
                    Snackbar.LENGTH_LONG,
                    this.context.getStringFromResources(R.string.notification_autoMarked_snackbar_undoAction),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            revert();
                        }
                    });
        return result;
    }

    @Override
    protected void revertCore() {

        UnitOfWork uow = this.persistenceContext.createUnitOfWork();

        for(DailyPlanModel dailyPlanModel : this.autoMarkedDailyPlans)
            dailyPlanModel.setStatus(PlanStatus.Planned);

        uow.complete();
    }
}
