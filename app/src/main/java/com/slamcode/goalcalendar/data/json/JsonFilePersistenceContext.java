package com.slamcode.goalcalendar.data.json;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.slamcode.goalcalendar.data.CategoriesRepository;
import com.slamcode.goalcalendar.data.DataFormatter;
import com.slamcode.goalcalendar.data.MonthlyPlansRepository;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.inmemory.InMemoryCategoriesRepository;
import com.slamcode.goalcalendar.data.inmemory.InMemoryMonthlyPlansRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashSet;

public class JsonFilePersistenceContext implements PersistenceContext {

    private static final String LOG_TAG = "GOAL_JsonPerCtx";
    private JsonMonthlyPlansDataBundle monthlyPlansDataBundle;
    private final Context appContext;
    private final DataFormatter<JsonMonthlyPlansDataBundle> dataFormatter;
    private final String fileName;

    private Collection<PersistenceContextChangedListener> contextChangedListeners;

    public JsonFilePersistenceContext(Context appContext, DataFormatter<JsonMonthlyPlansDataBundle> dataFormatter, String fileName)
    {
        this.appContext = appContext;
        this.dataFormatter = dataFormatter;
        this.fileName = fileName;
        this.contextChangedListeners =  new HashSet<>();
    }

    public JsonMonthlyPlansDataBundle getDataBundle()
    {
        return this.monthlyPlansDataBundle;
    }

    @Override
    public void persistData() {

        FileOutputStream fileStream;
        try{
            Log.d(LOG_TAG, "Persisting json data file - START");
            fileStream = this.appContext.openFileOutput(this.fileName, Context.MODE_PRIVATE);
            fileStream.write(this.dataFormatter.formatDataBundle(this.monthlyPlansDataBundle).getBytes());
            fileStream.close();

            this.onContextDataPersisted();
            Log.d(LOG_TAG, "Persisting json data file - END");
        }
        catch(Exception exception)
        {
            Log.e(LOG_TAG, "Persisting json data file - FAILURE", exception);
        }
    }

    private void onContextDataPersisted() {
        for(PersistenceContextChangedListener listener : this.contextChangedListeners)
            listener.onContextPersisted();
    }

    @Override
    public void initializePersistedData() {
        try {
            Log.d(LOG_TAG, "Reading json data file - START");
            File bundleFile = new File(this.getFilePath());
            if(bundleFile.exists()) {
                FileReader fileReader = new FileReader(this.getFilePath());

                Gson gson = new GsonBuilder()
                        .create();
                this.monthlyPlansDataBundle = gson.fromJson(fileReader, JsonMonthlyPlansDataBundle.class);
            }

            if(this.monthlyPlansDataBundle == null)
            {
                this.monthlyPlansDataBundle = new JsonMonthlyPlansDataBundle();
            }
            Log.d(LOG_TAG, "Reading json data file - END");
        }
        catch(Exception exception)
        {
            Log.e(LOG_TAG, "Reading json data file - FAILURE", exception);
        }
    }

    @Override
    public UnitOfWork createUnitOfWork() {
        if(this.monthlyPlansDataBundle == null)
        {
            this.initializePersistedData();
        }

        return new JsonUnitOfWork(this.monthlyPlansDataBundle);
    }

    @Override
    public void addContextChangedListener(PersistenceContextChangedListener listener) {
        this.contextChangedListeners.add(listener);
    }

    @Override
    public void removeContextChangedListener(PersistenceContextChangedListener listener) {
        this.contextChangedListeners.remove(listener);
    }

    @Override
    public void clearContextChangedListeners() {
        this.contextChangedListeners.clear();
    }

    private String getFilePath()
    {
        return this.appContext.getFilesDir() +"/" + this.fileName;
    }

    private class JsonUnitOfWork implements UnitOfWork{

        private boolean working = false;
        private final InMemoryMonthlyPlansRepository monthlyPlansRepository;
        private final CategoriesRepository categoriesRepository;

        JsonUnitOfWork(JsonMonthlyPlansDataBundle dataBundle)
        {
            this.categoriesRepository = new InMemoryCategoriesRepository(dataBundle.monthlyPlans);

            this.monthlyPlansRepository = new InMemoryMonthlyPlansRepository(dataBundle.monthlyPlans);
        }

        public CategoriesRepository getCategoriesRepository() {
            return this.categoriesRepository;
        }

        @Override
        public MonthlyPlansRepository getMonthlyPlansRepository() {
            return this.monthlyPlansRepository;
        }

        @Override
        public void complete() {
            this.complete(true);
        }

        @Override
        public void complete(boolean persistData) {
            this.working = false;
            if(persistData)
                persistData();
        }
    }

}
