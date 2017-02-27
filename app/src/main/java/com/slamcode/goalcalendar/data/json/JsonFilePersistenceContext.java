package com.slamcode.goalcalendar.data.json;

import android.content.Context;
import android.databinding.Observable;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.slamcode.goalcalendar.data.CategoriesRepository;
import com.slamcode.goalcalendar.data.MonthlyPlansRepository;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.inmemory.InMemoryCategoriesRepository;
import com.slamcode.goalcalendar.data.inmemory.InMemoryMonthlyPlansRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by moriasla on 03.01.2017.
 */

public class JsonFilePersistenceContext implements PersistenceContext {

    public static final String DEFAULT_FILE_NAME = "data.json";
    private JsonDataBundle dataBundle;
    private final Context appContext;
    private final String fileName;

    private Collection<PersistenceContextChangedListener> contextChangedListeners;

    public JsonFilePersistenceContext(Context appContext, String fileName)
    {
        this.appContext = appContext;
        this.fileName = fileName;
        this.contextChangedListeners =  new HashSet<>();
    }

    public JsonFilePersistenceContext(Context appContext)
    {
        this(appContext, DEFAULT_FILE_NAME);
    }

    public JsonDataBundle getDataBundle()
    {
        return this.dataBundle;
    }

    @Override
    public void persistData() {

        FileOutputStream fileStream;
        try{
            Gson gson = new GsonBuilder()
                    .create();
            fileStream = this.appContext.openFileOutput(this.fileName, Context.MODE_PRIVATE);
            fileStream.write(gson.toJson(this.dataBundle, JsonDataBundle.class).getBytes());
            fileStream.close();

            this.onContextDataPersisted();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private void onContextDataPersisted() {
        for(PersistenceContextChangedListener listener : this.contextChangedListeners)
            listener.onContextPersisted();
    }

    @Override
    public void initializePersistedData() {
        try {
            File bundleFile = new File(this.getFilePath());
            if(bundleFile.exists()) {
                FileReader fileReader = new FileReader(this.getFilePath());

                Gson gson = new GsonBuilder()
                        .create();
                this.dataBundle = gson.fromJson(fileReader, JsonDataBundle.class);
            }

            if(this.dataBundle == null)
            {
                this.dataBundle = new JsonDataBundle();
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    @Override
    public UnitOfWork createUnitOfWork() {
        if(this.dataBundle == null)
        {
            this.initializePersistedData();
        }

        return new JsonUnitOfWork(this.dataBundle);
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

        JsonUnitOfWork(JsonDataBundle dataBundle)
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
