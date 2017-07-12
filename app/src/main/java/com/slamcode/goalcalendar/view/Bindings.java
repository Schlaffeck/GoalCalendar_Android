package com.slamcode.goalcalendar.view;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.dagger2.Dagger2ComponentContainer;
import com.slamcode.goalcalendar.planning.schedule.DateTimeChangeListenersRegistry;
import com.slamcode.goalcalendar.planning.YearMonthPair;
import com.slamcode.goalcalendar.view.lists.CategoryDailyPlansRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.lists.CategoryNameRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.lists.CategoryPlansSummaryRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.lists.DailyPlanHeaderRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.lists.ItemsCollectionAdapterProvider;
import com.slamcode.goalcalendar.viewmodels.*;

import java.util.Collection;

import javax.inject.Inject;

/**
 * Created by moriasla on 01.03.2017.
 */

public class Bindings {

    @BindingAdapter("bind:categorySummarySource")
    public static void setCategorySummariesItemsSource(RecyclerView recyclerView, ObservableList<CategoryPlansViewModel> itemsSource)
    {
        if(recyclerView == null)
            return;

        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        boolean adapterUpToDate = false;
        if(adapter == null)
        {
            Dagger2InjectData injectData = new Dagger2InjectData();
            Dagger2ComponentContainer.getApplicationDagger2Component().inject(injectData);

            adapter = injectData.itemsCollectionAdapterProvider
                    .providePlansSummaryForCategoriesRecyclerViewAdapter(
                            injectData.applicationContext.getDefaultContext(),
                            (LayoutInflater)injectData.applicationContext.getDefaultContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            recyclerView.setAdapter(adapter);
        }

        if(!adapterUpToDate && (adapter instanceof CategoryPlansSummaryRecyclerViewAdapter))
        {
            CategoryPlansSummaryRecyclerViewAdapter dataAdapter = (CategoryPlansSummaryRecyclerViewAdapter) adapter;
            dataAdapter.updateSourceCollection(itemsSource);
        }
    }

    @BindingAdapter("bind:categoryNamesSource")
    public static void setCategoryNamesItemsSource(RecyclerView recyclerView, ObservableList<CategoryPlansViewModel> itemsSource)
    {
        if(recyclerView == null)
            return;

        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        boolean adapterUpToDate = false;
        if(adapter == null)
        {
            Dagger2InjectData injectData = new Dagger2InjectData();
            Dagger2ComponentContainer.getApplicationDagger2Component().inject(injectData);

            adapter = injectData.itemsCollectionAdapterProvider
                    .provideCategoryNameListViewAdapter(
                            injectData.applicationContext.getDefaultContext(),
                            (LayoutInflater)injectData.applicationContext.getDefaultContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                            itemsSource);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        if(!adapterUpToDate && (adapter instanceof CategoryNameRecyclerViewAdapter))
        {
            CategoryNameRecyclerViewAdapter dataAdapter = (CategoryNameRecyclerViewAdapter) adapter;
            dataAdapter.updateSourceCollection(itemsSource);
        }
    }

    @BindingAdapter("bind:categoryPlansSource")
    public static void setCategoryPlansItemsSource(RecyclerView recyclerView, ObservableList<CategoryPlansViewModel> itemsSource)
    {
        if(recyclerView == null)
            return;

        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        boolean adapterUpToDate = false;
        if(adapter == null)
        {
            Dagger2InjectData injectData = new Dagger2InjectData();
            Dagger2ComponentContainer.getApplicationDagger2Component().inject(injectData);

            adapter = injectData.itemsCollectionAdapterProvider
                    .provideCategoryDailyPlansListViewAdapter(
                            injectData.applicationContext.getDefaultContext(),
                            (LayoutInflater)injectData.applicationContext.getDefaultContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                            injectData.dateTimeChangeListenersRegistry,
                            new YearMonthPair(),
                            itemsSource);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        if(!adapterUpToDate && (adapter instanceof CategoryDailyPlansRecyclerViewAdapter))
        {
            CategoryDailyPlansRecyclerViewAdapter dataAdapter = (CategoryDailyPlansRecyclerViewAdapter) adapter;
            dataAdapter.updateSourceCollection(itemsSource);
        }
    }



    @BindingAdapter("bind:daysListHeaderSource")
    public static void setDaysListHeaderSource(RecyclerView recyclerView, Collection<DayInMonthViewModel> itemsSource)
    {
        if(recyclerView == null)
            return;

        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        if(adapter == null)
        {
            Dagger2InjectData injectData = new Dagger2InjectData();
            Dagger2ComponentContainer.getApplicationDagger2Component().inject(injectData);

            adapter = injectData.itemsCollectionAdapterProvider
                    .provideDailyPlanHeaderRecyclerViewAdapter(
                            injectData.applicationContext.getDefaultContext(),
                            new YearMonthPair(),
                            (LayoutInflater)injectData.applicationContext.getDefaultContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                            injectData.dateTimeChangeListenersRegistry);
            recyclerView.setAdapter(adapter);
        }

        if(adapter instanceof DailyPlanHeaderRecyclerViewAdapter)
        {
            DailyPlanHeaderRecyclerViewAdapter dataAdapter = (DailyPlanHeaderRecyclerViewAdapter) adapter;
            dataAdapter.updateSourceCollection(itemsSource);
        }
    }

    @BindingAdapter(value = {"bind:selectedValue", "bind:selectedValueAttrChanged"}, requireAll = false)
    public static void bindSpinnerData(Spinner pAppCompatSpinner, final String newSelectedValue, final InverseBindingListener newTextAttrChanged) {
        pAppCompatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(newSelectedValue != null && newSelectedValue.equals(parent.getSelectedItem())){
                    return;
                }
                newTextAttrChanged.onChange();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (newSelectedValue != null) {
            int pos = ((ArrayAdapter<String>) pAppCompatSpinner.getAdapter()).getPosition(newSelectedValue);
            pAppCompatSpinner.setSelection(pos, true);
        }
    }

    @InverseBindingAdapter(attribute = "bind:selectedValue", event = "bind:selectedValueAttrChanged")
    public static String captureSelectedValue(Spinner pAppCompatSpinner) {
        return (String) pAppCompatSpinner.getSelectedItem();
    }

    public static class Dagger2InjectData{

        @Inject
        ItemsCollectionAdapterProvider itemsCollectionAdapterProvider;

        @Inject
        ApplicationContext applicationContext;

        @Inject
        DateTimeChangeListenersRegistry dateTimeChangeListenersRegistry;
    }
}
