package com.slamcode.goalcalendar.data.json;

import android.content.Context;

import com.google.gson.Gson;
import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementSelector;
import com.slamcode.goalcalendar.data.CategoryRepository;
import com.slamcode.goalcalendar.data.MonthlyPlansRepository;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.inmemory.InMemoryCategoriesRepository;
import com.slamcode.goalcalendar.data.inmemory.InMemoryMonthlyPlansRepository;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Collection;


/**
 * Created by moriasla on 03.01.2017.
 */

public class JsonFilePersistenceContext implements PersistenceContext {

    public static final String DEFAULT_FILE_NAME = "data.json";
    private JsonDataBundle dataBundle;
    private final Context appContext;
    private final String fileName;

    public JsonFilePersistenceContext(Context appContext, String fileName)
    {
        this.appContext = appContext;
        this.fileName = fileName;
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
            Gson gson = new Gson();
            fileStream = this.appContext.openFileOutput(this.fileName, Context.MODE_PRIVATE);
            fileStream.write(gson.toJson(this.dataBundle, JsonDataBundle.class).getBytes());
            fileStream.close();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    @Override
    public void initializePersistedData() {
        try {
            File bundleFile = new File(this.getFilePath());
            if(bundleFile.exists()) {
                FileReader fileReader = new FileReader(this.getFilePath());

                Gson gson = new Gson();
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

    private String getFilePath()
    {
        return this.appContext.getFilesDir() +"/" + this.fileName;
    }

    private static class JsonUnitOfWork implements UnitOfWork{

        private boolean working = false;
        private final InMemoryMonthlyPlansRepository monthlyPlansRepository;
        private final CategoryRepository categoryRepository;

        JsonUnitOfWork(JsonDataBundle dataBundle)
        {
            this.categoryRepository = new InMemoryCategoriesRepository(dataBundle.monthlyPlans);

            this.monthlyPlansRepository = new InMemoryMonthlyPlansRepository(dataBundle.monthlyPlans);
        }

        @Override
        public CategoryRepository getCategoryRepository() {
            return this.categoryRepository;
        }

        @Override
        public MonthlyPlansRepository getMonthlyPlansRepository() {
            return this.monthlyPlansRepository;
        }

        @Override
        public void complete() {
            this.working = false;
        }
    }
}
