package com.slamcode.goalcalendar.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.slamcode.goalcalendar.dagger2.ComposableService;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by moriasla on 10.02.2017.
 */

public class DefaultAutoMarkTasksService extends ComposableService implements AutoMarkTasksService, ApplicationStartupService {

    private static final String LOG_TAG = "GOAL_AutoMarkSvc";

    @Inject
    PersistenceContext persistenceContext;

    @Inject
    AppSettingsManager settingsManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public DefaultAutoMarkTasksService(PersistenceContext persistenceContext, AppSettingsManager settingsManager)
    {
        this.persistenceContext = persistenceContext;
        this.settingsManager = settingsManager;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        return result;
    }

    @Override
    protected void injectDependencies() {
        this.getApplicationComponent().inject(this);
    }

    @Override
    public AutoMarkResult markUnfinishedTasksAsFailed() {
        AutoMarkResult result = new AutoMarkResult();

        if(this.settingsManager.getAutomaticallyMarkUncompletedTask()) {

            UnitOfWork uow = this.persistenceContext.createUnitOfWork();

            DateTime yesterday = DateTimeHelper.getYesterdayDateTime();
            List<CategoryModel> categoryModels = uow.getCategoriesRepository()
                    .findForDateWithStatus(yesterday.getYear(), yesterday.getMonth(), yesterday.getDay(), PlanStatus.Planned);

            for(CategoryModel model : categoryModels)
            {
                if(model.getDailyPlans().size() >= yesterday.getDay())
                {
                    DailyPlanModel dailyPlanModel = model.getDailyPlans().get(yesterday.getDay()-1);
                    dailyPlanModel.setStatus(PlanStatus.Failure);
                }
            }

            result.setWasRun(true);
            result.setUnfinishedTasksMarkedFailedCount(categoryModels.size());

            uow.complete();
        }

        return result;
    }

    @Override
    public void serviceStartup(Intent intent) {
        this.onStartCommand(intent, 0, 0);
    }
}
