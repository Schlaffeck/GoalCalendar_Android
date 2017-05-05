package com.slamcode.goalcalendar.dagger2;

import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.data.dagger2.DataDagger2Module;
import com.slamcode.goalcalendar.diagniostics.dagger2.DiagnosticsDagger2Module;
import com.slamcode.goalcalendar.planning.dagger2.PlanningDagger2Module;
import com.slamcode.goalcalendar.service.commands.SnackbarShowUpAutoMarkTasksCommand;
import com.slamcode.goalcalendar.service.dagger2.ServiceDagger2Module;
import com.slamcode.goalcalendar.service.notification.NotificationPublisher;
import com.slamcode.goalcalendar.service.notification.NotificationScheduler;
import com.slamcode.goalcalendar.settings.dagger2.SettingsDagger2Module;
import com.slamcode.goalcalendar.view.charts.ChartsBindings;
import com.slamcode.goalcalendar.view.dagger2.ViewDagger2Module;
import com.slamcode.goalcalendar.view.Bindings;
import com.slamcode.goalcalendar.view.presenters.CachedApplicationPresentersSource;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by moriasla on 04.01.2017.
 */
@Singleton
@Component(modules = {
        AppDagger2Module.class,
        DataDagger2Module.class,
        ViewDagger2Module.class,
        ServiceDagger2Module.class,
        SettingsDagger2Module.class,
        DiagnosticsDagger2Module.class,
        PlanningDagger2Module.class})
public interface ApplicationDagger2Component {

    void inject(MonthlyGoalsActivity activity);

    void inject(NotificationScheduler notificationScheduler);

    void inject(NotificationPublisher notificationPublisher);

    void inject(SnackbarShowUpAutoMarkTasksCommand defaultAutoMarkTasksService);

    void inject(CachedApplicationPresentersSource cachedApplicationPresentersSource);

    void inject(Bindings.Dagger2InjectData listsBindingsInjectData);

    void inject(ChartsBindings.Dagger2InjectData listsBindingsInjectData);
}
