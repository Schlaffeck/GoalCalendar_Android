package com.slamcode.goalcalendar.data.dagger2;

import android.content.Context;

import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.json.JsonFilePersistenceContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by moriasla on 04.01.2017.
 */
@Module
public final class DataDagger2Module {

    private final Context context;

    public DataDagger2Module(Context context)
    {
        this.context = context;
    }

    @Provides
    public PersistenceContext providePersistenceContext()
    {
        // create and return proper persistence context used in app
        return new JsonFilePersistenceContext(this.context);
    }
}
