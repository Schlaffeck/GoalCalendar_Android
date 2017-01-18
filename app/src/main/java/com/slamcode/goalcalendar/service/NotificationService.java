package com.slamcode.goalcalendar.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.dagger2.ComposableService;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

import javax.inject.Inject;

/**
 * Created by moriasla on 18.01.2017.
 */

public final class NotificationService extends ComposableService implements StartupService{

    static final int NOTIFICATION_PLANNED_FOR_TODAY_ID = 1;

    @Inject
    PersistenceContext persistenceContext;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // todo: create binder implementation
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        this.sendNotificationForTasksPlannedForToday(intent);
        return result;
    }

    @Override
    protected void injectDependencies() {
        this.getApplicationComponent().inject(this);
    }

    private void sendNotificationForTasksPlannedForToday(Intent intent)
    {
        UnitOfWork uow = this.persistenceContext.createUnitOfWork();

        try {
            long countCategoriesPlannedForToday = this.countCategoriesPlannedForToday(uow);

            if(countCategoriesPlannedForToday > 0)
            {
                Intent resultIntent = new Intent(this, MonthlyGoalsActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                NotificationCompat.Builder notificationBuilder
                        = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_date_range_white_24dp)
                        .setContentTitle(this.getString(R.string.notification_plannedForToday_title))
                        .setContentText(
                                String.format(
                                        this.getString(R.string.notification_plannedForToday_content),
                                        countCategoriesPlannedForToday
                                ))
                        .setContentIntent(
                                PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                        );

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_PLANNED_FOR_TODAY_ID, notificationBuilder.build());
            }
        }
        finally {
            uow.complete();
        }
    }

    private long countCategoriesPlannedForToday(UnitOfWork uow) {

        MonthlyPlansModel monthlyPlansModel = uow.getMonthlyPlansRepository().findForMonth(
                DateTimeHelper.getCurrentYear(),
                Month.getCurrentMonth()
        );

        if (monthlyPlansModel == null
                || monthlyPlansModel.getCategories() == null)
            return 0;

        long countPlannedForToday = IterableUtils.countMatches(monthlyPlansModel.getCategories(), new Predicate<CategoryModel>() {
            @Override
            public boolean evaluate(CategoryModel categoryModel) {
                if(categoryModel == null)
                    return false;

                if(categoryModel.getDailyPlans() == null
                        || categoryModel.getDailyPlans().isEmpty())
                    return false;

                final DailyPlanModel dailyPlanModel = IterableUtils.find(
                        categoryModel.getDailyPlans(),
                        new Predicate<DailyPlanModel>() {
                            @Override
                            public boolean evaluate(DailyPlanModel dailyPlanModel) {
                                return dailyPlanModel != null
                                        && dailyPlanModel.getDayNumber() == DateTimeHelper.currentDayNumber()
                                        && dailyPlanModel.getStatus() == PlanStatus.Planned;
                            }
                        });

                return dailyPlanModel != null;
            }});

        return countPlannedForToday;
    }
}
