package com.slamcode.goalcalendar.view.presenters;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationClient;
import com.slamcode.goalcalendar.backup.BackupSourceDataProvidersRegistry;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.activity.BackupActivityContract;
import com.slamcode.goalcalendar.view.activity.LoginActivityContract;
import com.slamcode.goalcalendar.view.activity.MonthlyGoalsActivityContract;
import com.slamcode.goalcalendar.view.lists.ItemsCollectionAdapterProvider;

import java.util.Map;

/**
 * Created by moriasla on 24.02.2017.
 */

public class CachedApplicationPresentersSource implements PresentersSource {

    private MonthlyGoalsPresenter monthlyGoalsPresenter;
    private BackupPresenter backupPresenter;
    private LoginPresenter loginPresenter;

    private PersistenceContext persistenceContext;

    private ApplicationContext applicationContext;

    private ItemsCollectionAdapterProvider listAdapterProvider;

    private PlansSummaryCalculator plansSummaryCalculator;
    private final BackupSourceDataProvidersRegistry backupSourceDataProvidersRegistry;
    private final Map<String, AuthenticationClient> authenticationClientMap;

    public CachedApplicationPresentersSource(ApplicationContext applicationContext,
                                             PersistenceContext persistenceContext,
                                             ItemsCollectionAdapterProvider listAdapterProvider,
                                             PlansSummaryCalculator plansSummaryCalculator,
                                             BackupSourceDataProvidersRegistry backupSourceDataProvidersRegistry,
                                             Map<String, AuthenticationClient> authenticationClientMap)
    {
        this.applicationContext = applicationContext;
        this.persistenceContext = persistenceContext;
        this.listAdapterProvider = listAdapterProvider;
        this.plansSummaryCalculator = plansSummaryCalculator;
        this.backupSourceDataProvidersRegistry = backupSourceDataProvidersRegistry;
        this.authenticationClientMap = authenticationClientMap;
    }

    @Override
    public MonthlyGoalsPresenter getMonthlyGoalsPresenter(MonthlyGoalsActivityContract.ActivityView activityView) {
        if(this.monthlyGoalsPresenter == null)
        {
            this.monthlyGoalsPresenter = new PersistentMonthlyGoalsPresenter(
                    this.applicationContext,
                    this.persistenceContext,
                    this.plansSummaryCalculator);
        }
        return this.monthlyGoalsPresenter;
    }

    @Override
    public BackupPresenter getBackupPresenter(BackupActivityContract.ActivityView activityView) {
        if(this.backupPresenter == null)
        {
            this.backupPresenter = new PersistentBackupPresenter(this.applicationContext, this.persistenceContext, this.backupSourceDataProvidersRegistry);
        }
        return this.backupPresenter;
    }

    @Override
    public LoginPresenter getLoginPresenter(LoginActivityContract.ActivityView activityView) {
        if(this.loginPresenter == null)
        {
            this.loginPresenter = new PersistentLoginPresenter(this.authenticationClientMap);
        }
        return this.loginPresenter;
    }
}
