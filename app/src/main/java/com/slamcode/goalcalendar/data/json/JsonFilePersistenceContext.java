package com.slamcode.goalcalendar.data.json;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.slamcode.goalcalendar.data.BackupInfoRepository;
import com.slamcode.goalcalendar.data.CategoriesRepository;
import com.slamcode.goalcalendar.data.DataBundleAbstract;
import com.slamcode.goalcalendar.data.DataFormatter;
import com.slamcode.goalcalendar.data.MonthlyPlansRepository;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.inmemory.InMemoryBackupInfoRepository;
import com.slamcode.goalcalendar.data.inmemory.InMemoryCategoriesRepository;
import com.slamcode.goalcalendar.data.inmemory.InMemoryMonthlyPlansRepository;
import com.slamcode.goalcalendar.data.model.backup.BackupDataBundle;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;
import com.slamcode.goalcalendar.data.unitofwork.UnitOfWork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Collection;
import java.util.HashSet;

public class JsonFilePersistenceContext implements PersistenceContext {

    private static final String LOG_TAG = "GOAL_JsonPerCtx";
    private MonthlyPlansDataBundle monthlyPlansDataBundle;
    private BackupDataBundle backupDataBundle;
    private final Context appContext;
    private final DataFormatter dataFormatter;
    private final JsonPersistenceContextConfiguration contextConfiguration;

    private Collection<PersistenceContextChangedListener> contextChangedListeners;

    JsonFilePersistenceContext(Context appContext, DataFormatter dataFormatter, JsonPersistenceContextConfiguration contextConfiguration)
    {
        this.appContext = appContext;
        this.dataFormatter = dataFormatter;
        this.contextConfiguration = contextConfiguration;
        this.contextChangedListeners =  new HashSet<>();
    }

    public MonthlyPlansDataBundle getDataBundle()
    {
        return this.monthlyPlansDataBundle;
    }

    public void setDataBundle(MonthlyPlansDataBundle dataBundle)
    {
        if(dataBundle == null)
            throw new IllegalArgumentException("Data bundle can not be set to null");

        this.monthlyPlansDataBundle = dataBundle;
    }

    @Override
    public void persistData() {

        try{
            this.persistDataBundle(this.monthlyPlansDataBundle, this.monthlyPlansDataBundle.getClass(), this.contextConfiguration.getPlansDataFileName());
            this.persistDataBundle(this.backupDataBundle, this.backupDataBundle.getClass(), this.contextConfiguration.getBackupInfoDataFileName());
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
            this.monthlyPlansDataBundle = this.initializePersistedDataBundle(MonthlyPlansDataBundle.class, this.contextConfiguration.getPlansDataFileName());
            if(this.monthlyPlansDataBundle == null)
            {
                this.monthlyPlansDataBundle = new MonthlyPlansDataBundle();
            }

            this.backupDataBundle= this.initializePersistedDataBundle(BackupDataBundle.class, this.contextConfiguration.getBackupInfoDataFileName());
            if(this.backupDataBundle == null)
            {
                this.backupDataBundle = new BackupDataBundle();
            }
        }
        catch(Exception exception)
        {
            Log.e(LOG_TAG, "Reading json data file - FAILURE", exception);
        }
    }

    @Override
    public UnitOfWork createUnitOfWork() {
        return this.createUnitOfWork(false);
    }

    @Override
    public UnitOfWork createUnitOfWork(boolean readonly) {

        if(this.monthlyPlansDataBundle == null || this.backupDataBundle == null)
        {
            this.initializePersistedData();
        }

        return new JsonUnitOfWork(readonly, this.monthlyPlansDataBundle, this.backupDataBundle);
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

    private boolean persistDataBundle(DataBundleAbstract dataBundle, Class bundleType, String fileName) throws IOException {
        FileOutputStream fileStream;
            String formatted =  this.formatDataBundle(dataBundle, bundleType);
            Log.d(LOG_TAG, "Persisting json data file - START");
            fileStream = this.appContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileStream.write(formatted.getBytes());
            fileStream.close();

            this.onContextDataPersisted();
            Log.d(LOG_TAG, "Persisting json data file - END");
        return true;
    }

    private <DataBundle extends DataBundleAbstract> DataBundle initializePersistedDataBundle(Class<DataBundle> bundleType, String fileName) throws FileNotFoundException {
        DataBundle bundle = null;
            Log.d(LOG_TAG, "Reading json data file - START");
            File bundleFile = new File(this.getFilePath(fileName));
            if(bundleFile.exists()) {
                FileReader fileReader = new FileReader(this.getFilePath(fileName));

                Gson gson = new GsonBuilder()
                        .create();
                bundle = gson.fromJson(fileReader, bundleType);
            }

            Log.d(LOG_TAG, "Reading json data file - END");
            return bundle;
    }

    private String formatDataBundle(DataBundleAbstract dataBundle, Class bundleType) {
        if(bundleType == MonthlyPlansDataBundle.class)
        {
            return this.dataFormatter.formatDataBundle((MonthlyPlansDataBundle)dataBundle);
        }

        if(bundleType == BackupDataBundle.class)
        {
            return this.dataFormatter.formatDataBundle((BackupDataBundle) dataBundle);
        }

        throw new IllegalArgumentException("Formatting of type '" + bundleType.getName() +"' is not supported");
    }

    private String getFilePath(String fileName)
    {
        return this.appContext.getFilesDir() +"/" + fileName;
    }

    private class JsonUnitOfWork implements UnitOfWork{
;
        private boolean working = false;
        private final InMemoryMonthlyPlansRepository monthlyPlansRepository;
        private final BackupDataBundle backupInfoDataBundle;
        private final boolean readonly;
        private final CategoriesRepository categoriesRepository;
        private final InMemoryBackupInfoRepository backupInfoRepository;

        JsonUnitOfWork(boolean readonly, MonthlyPlansDataBundle dataBundle, BackupDataBundle backupInfoDataBundle)
        {
            this.readonly = readonly;
            this.categoriesRepository = new InMemoryCategoriesRepository(dataBundle.monthlyPlans);

            this.monthlyPlansRepository = new InMemoryMonthlyPlansRepository(dataBundle.monthlyPlans);
            this.backupInfoDataBundle = backupInfoDataBundle;

            this.backupInfoRepository = new InMemoryBackupInfoRepository(this.backupInfoDataBundle.getBackupInfos());
        }

        public CategoriesRepository getCategoriesRepository() {
            return this.categoriesRepository;
        }

        @Override
        public MonthlyPlansRepository getMonthlyPlansRepository() {
            return this.monthlyPlansRepository;
        }

        @Override
        public BackupInfoRepository getBackupInfoRepository() {
            return this.backupInfoRepository;
        }

        @Override
        public boolean isReadonly() {
            return this.readonly;
        }

        @Override
        public void complete() {
            this.complete(!this.readonly);
        }

        @Override
        public void complete(boolean persistData) {
            this.working = false;
            if(this.readonly && persistData)
                throw new IllegalArgumentException("Can not persist data on readonly unit of work");

            if(persistData)
                persistData();
        }
    }

}
