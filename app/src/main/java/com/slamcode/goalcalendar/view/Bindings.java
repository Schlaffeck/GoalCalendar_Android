package com.slamcode.goalcalendar.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.R;
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
            adapterUpToDate = true;
        }

        if(!adapterUpToDate && (adapter instanceof CategoryNameRecyclerViewAdapter))
        {
            CategoryNameRecyclerViewAdapter dataAdapter = (CategoryNameRecyclerViewAdapter) adapter;
            dataAdapter.updateSourceCollectionOneByOne(itemsSource);
        }
    }

    @BindingAdapter("bind:categoryPlansSource")
    public static void setCategoryPlansItemsSource(RecyclerView recyclerView, MonthlyPlanningCategoryListViewModel monthlyPlanningCategoryListViewModel)
    {
        if(recyclerView == null)
            return;

        ObservableList<CategoryPlansViewModel> itemsSource = monthlyPlanningCategoryListViewModel.getCategoryPlansList();
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
                            new YearMonthPair(monthlyPlanningCategoryListViewModel.getMonthData().getYear(), monthlyPlanningCategoryListViewModel.getMonthData().getMonth()),
                            itemsSource);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapterUpToDate = true;
        }

        if(!adapterUpToDate && (adapter instanceof CategoryDailyPlansRecyclerViewAdapter))
        {
            CategoryDailyPlansRecyclerViewAdapter dataAdapter = (CategoryDailyPlansRecyclerViewAdapter) adapter;
            dataAdapter.setYearMonthPair(new YearMonthPair(monthlyPlanningCategoryListViewModel.getMonthData().getYear(), monthlyPlanningCategoryListViewModel.getMonthData().getMonth()));
            dataAdapter.updateSourceCollectionOneByOne(itemsSource);
        }
    }



    @BindingAdapter("bind:daysListHeaderSource")
    public static void setDaysListHeaderSource(RecyclerView recyclerView, MonthViewModel monthViewModel)
    {
        if(recyclerView == null)
            return;

        Collection<DayInMonthViewModel> itemsSource = monthViewModel.getDaysList();
        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        if(adapter == null)
        {
            Dagger2InjectData injectData = new Dagger2InjectData();
            Dagger2ComponentContainer.getApplicationDagger2Component().inject(injectData);

            adapter = injectData.itemsCollectionAdapterProvider
                    .provideDailyPlanHeaderRecyclerViewAdapter(
                            injectData.applicationContext.getDefaultContext(),
                            new YearMonthPair(monthViewModel.getYear(), monthViewModel.getMonth()),
                            (LayoutInflater)injectData.applicationContext.getDefaultContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                            injectData.dateTimeChangeListenersRegistry);
            recyclerView.setAdapter(adapter);
        }

        if(adapter instanceof DailyPlanHeaderRecyclerViewAdapter)
        {
            DailyPlanHeaderRecyclerViewAdapter dataAdapter = (DailyPlanHeaderRecyclerViewAdapter) adapter;
            dataAdapter.setYearMonthPair(new YearMonthPair(monthViewModel.getYear(), monthViewModel.getMonth()));
            dataAdapter.updateSourceCollectionOneByOne(itemsSource);
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

    @BindingAdapter("bind:animatedVisibility")
    public static void setVisibility(final View view,
                                     final int visibility) {
        // Were we animating before? If so, what was the visibility?
        Integer endAnimVisibility =
                (Integer) view.getTag(R.id.finalVisibility);
        int oldVisibility = endAnimVisibility == null
                ? view.getVisibility()
                : endAnimVisibility;

        if (oldVisibility == visibility) {
            // just let it finish any current animation.
            return;
        }

        boolean isVisibile = oldVisibility == View.VISIBLE;
        boolean willBeVisible = visibility == View.VISIBLE;

        view.setVisibility(View.VISIBLE);
        float startAlpha = isVisibile ? 1f : 0f;
        if (endAnimVisibility != null) {
            startAlpha = view.getAlpha();
        }
        float endAlpha = willBeVisible ? 1f : 0f;

        // Now create an animator
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view,
                View.ALPHA, startAlpha, endAlpha);

        alpha.addListener(new AnimatorListenerAdapter() {
            private boolean isCanceled;

            @Override
            public void onAnimationStart(Animator anim) {
                view.setTag(R.id.finalVisibility, visibility);
            }

            @Override
            public void onAnimationCancel(Animator anim) {
                isCanceled = true;
            }

            @Override
            public void onAnimationEnd(Animator anim) {
                view.setTag(R.id.finalVisibility, null);
                if (!isCanceled) {
                    view.setAlpha(1f);
                    view.setVisibility(visibility);
                }
            }
        });
        alpha.start();
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
