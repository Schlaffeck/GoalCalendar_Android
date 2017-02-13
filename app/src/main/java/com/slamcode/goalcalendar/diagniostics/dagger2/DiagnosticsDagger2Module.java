package com.slamcode.goalcalendar.diagniostics.dagger2;

import com.slamcode.goalcalendar.diagniostics.Logger;
import com.slamcode.goalcalendar.diagniostics.SystemLogger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by moriasla on 13.02.2017.
 */
@Module
public class DiagnosticsDagger2Module {

    @Provides
    @Singleton
    public Logger provideLogger()
    {
        return new SystemLogger();
    }
}
