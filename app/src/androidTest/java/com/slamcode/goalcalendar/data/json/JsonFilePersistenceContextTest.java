package com.slamcode.goalcalendar.data.json;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.slamcode.collections.CollectionUtils;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
/**
 * Created by moriasla on 03.01.2017.
 */
@RunWith(AndroidJUnit4.class)
public class JsonFilePersistenceContextTest{

    private static final String BUNDLE_FILE_NAME = "test_json.data";
    private Context appContext;
    private JsonDataBundle bundle;

    @Before
    public void setUp() throws Exception {
        this.bundle = new JsonDataBundle();
        this.bundle.monthlyPlans
            = CollectionUtils.createList(
                createPlansModel(1, Month.JANUARY, 3),
                createPlansModel(2, Month.FEBRUARY, 2),
                createPlansModel(3, Month.MARCH, 4)
        );
        this.appContext = InstrumentationRegistry.getTargetContext();
        Gson gson = new Gson();

        FileOutputStream fileStream = this.appContext.openFileOutput(BUNDLE_FILE_NAME, Context.MODE_PRIVATE);
        fileStream.write(gson.toJson(this.bundle).getBytes());
        fileStream.close();
    }

    @After
    public void tearDown() throws Exception {
        File bundleFile = new File(this.appContext.getFilesDir() + "/" + BUNDLE_FILE_NAME);
        if(bundleFile.exists())
        {
            bundleFile.delete();
        }
        this.bundle = null;
    }

    @Test
    public void jsonFilePersistenceContext_initialize_nonExistingFile_test() throws IOException {

        File bundleFile = new File(this.appContext.getFilesDir() + "/" + BUNDLE_FILE_NAME);
        bundleFile.delete();
        assertFalse(bundleFile.exists());

        JsonFilePersistenceContext context = new JsonFilePersistenceContext(this.appContext, BUNDLE_FILE_NAME);
        assertNull(context.getDataBundle());

        context.initializePersistedData();
        assertNotNull(context.getDataBundle());
        assertNotNull(context.getDataBundle().monthlyPlans);
        assertEquals(0, context.getDataBundle().monthlyPlans.size());
    }

    @Test
    public void jsonFilePersistenceContext_initialize_emptyFile_test() throws IOException {
        // clear file
        FileWriter writer = new FileWriter(this.appContext.getFilesDir() + "/" + BUNDLE_FILE_NAME);
        writer.write(new char[0]);
        writer.close();

        JsonFilePersistenceContext context = new JsonFilePersistenceContext(this.appContext, BUNDLE_FILE_NAME);
        assertNull(context.getDataBundle());

        context.initializePersistedData();
        assertNotNull(context.getDataBundle());
        assertNotNull(context.getDataBundle().monthlyPlans);
        assertEquals(0, context.getDataBundle().monthlyPlans.size());
    }

    @Test
    public void jsonFilePersistenceContext_initialize_test()
    {
        JsonFilePersistenceContext context = new JsonFilePersistenceContext(this.appContext, BUNDLE_FILE_NAME);
        assertNull(context.getDataBundle());

        context.initializePersistedData();
        assertEquals(3, context.getDataBundle().monthlyPlans.size());
    }

    @Test
    public void jsonFilePersistenceContext_changePersistRead_test()
    {
        JsonFilePersistenceContext primalContext = new JsonFilePersistenceContext(this.appContext, BUNDLE_FILE_NAME);
        assertNull(primalContext.getDataBundle());

        primalContext.initializePersistedData();

        assertEquals(3, primalContext.getDataBundle().monthlyPlans.size());
        MonthlyPlansModel plan1 = primalContext.getDataBundle().monthlyPlans.get(0);
        assertEquals(Month.JANUARY, plan1.getMonth());

        // change
        plan1.setMonth(Month.APRIL);
        primalContext.getDataBundle().monthlyPlans.add(createPlansModel(4, Month.AUGUST, 3));
        primalContext.persistData();

        // read in new context
        JsonFilePersistenceContext newContext = new JsonFilePersistenceContext(this.appContext, BUNDLE_FILE_NAME);
        assertNull(newContext.getDataBundle());

        newContext.initializePersistedData();
        assertNotNull(newContext.getDataBundle());

        // check changes
        assertEquals(4, newContext.getDataBundle().monthlyPlans.size());
        MonthlyPlansModel newPlan1 = newContext.getDataBundle().monthlyPlans.get(0);
        assertEquals(Month.APRIL, newPlan1.getMonth());
    }

    @Test
    public void jsonFilePersistenceContext_createUnitOfWork_test()
    {
        JsonFilePersistenceContext context = new JsonFilePersistenceContext(this.appContext, BUNDLE_FILE_NAME);
        assertNull(context.getDataBundle());

        UnitOfWork uow = context.createUnitOfWork();

        assertNotNull(context.getDataBundle());
        assertNotNull(uow.getCategoryRepository());
        assertEquals(9, uow.getCategoryRepository().findAll().size());

        assertNotNull(uow.getMonthlyPlansRepository());
        assertEquals(3, uow.getMonthlyPlansRepository().findAll().size());
    }

    private MonthlyPlansModel createPlansModel(int id, Month month, int noOfCategories)
    {
        MonthlyPlansModel plans = new MonthlyPlansModel();
        plans.setId(id);
        plans.setMonth(month);

        List<CategoryModel> categories = new ArrayList<>();
        for(int i = 0; i< noOfCategories; i++)
        {
            categories.add(createCategoryModel((i+1) * id, "cat " + (i+1), FrequencyPeriod.Week, 2));
        }
        plans.setCategories(categories);

        return plans;
    }

    private CategoryModel createCategoryModel(int id, String name, FrequencyPeriod frequencyPeriod, int frequencyValue)
    {
        CategoryModel model = new CategoryModel(id);
        model.setFrequencyValue(frequencyValue);
        model.setPeriod(frequencyPeriod);
        model.setName(name);

        return model;
    }
}