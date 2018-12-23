package com.slamcode.goalcalendar.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.dagger2.Dagger2ComponentContainer;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.YearMonthPair;
import com.slamcode.goalcalendar.planning.schedule.DateTimeChangeListenersRegistry;
import com.slamcode.goalcalendar.view.lists.BackupSourcesRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.lists.CategoryPlansRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.lists.CategoryPlansSummaryRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.lists.DailyPlanHeaderRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.lists.ItemsCollectionAdapterProvider;
import com.slamcode.goalcalendar.view.lists.gestures.ItemDragCallback;
import com.slamcode.goalcalendar.view.lists.scrolling.RecyclerViewSimultaneousScrollingController;
import com.slamcode.goalcalendar.viewmodels.BackupSourceViewModel;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;
import com.slamcode.goalcalendar.viewmodels.DayInMonthViewModel;
import com.slamcode.goalcalendar.viewmodels.MonthViewModel;
import com.slamcode.goalcalendar.viewmodels.MonthlyPlanningCategoryListViewModel;

import java.util.Collection;

import javax.inject.Inject;

/**
 * Created by moriasla on 01.03.2017.
 */

public class Bindings {

    private static final String LOG_TAG = "GOAL_Bindings";

    @BindingAdapter("bind:categorySummarySource")
    public static void setCategorySummariesItemsSource(RecyclerView recyclerView, ObservableList<CategoryPlansViewModel> itemsSource)
    {
        Log.d(LOG_TAG, "Binding category summary source - START");
        if(recyclerView == null)
            return;

        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        if(adapter == null)
        {
            Dagger2InjectData injectData = new Dagger2InjectData();
            Dagger2ComponentContainer.getApplicationDagger2Component().inject(injectData);

            Log.d(LOG_TAG, "Binding category summary source - creating new adapter");
            adapter = injectData.itemsCollectionAdapterProvider
                    .providePlansSummaryForCategoriesRecyclerViewAdapter();
            recyclerView.setAdapter(adapter);
        }

        if((adapter instanceof CategoryPlansSummaryRecyclerViewAdapter))
        {
            Log.d(LOG_TAG, "Binding category summary source - updating adapter source");
            CategoryPlansSummaryRecyclerViewAdapter dataAdapter = (CategoryPlansSummaryRecyclerViewAdapter) adapter;
            dataAdapter.updateSourceCollection(itemsSource);
        }

        Log.d(LOG_TAG, "Binding category summary source - END");
    }

    @BindingAdapter("bind:monthlyPlansSource")
    public static void setCategoriesMonthlyPlansSource(final RecyclerView recyclerView, MonthlyPlanningCategoryListViewModel monthlyPlanningCategoryListViewModel)
    {
        Log.d(LOG_TAG, "Binding category plans source - START");
        if(recyclerView == null)
            return;

        final ObservableList<CategoryPlansViewModel> itemsSource = monthlyPlanningCategoryListViewModel.getCategoryPlansList();
        final int year = monthlyPlanningCategoryListViewModel.getMonthData().getYear();
        final Month month = monthlyPlanningCategoryListViewModel.getMonthData().getMonth();

        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        if(adapter == null)
        {
            Log.d(LOG_TAG, "Binding category plans source - injecting services");
            Dagger2InjectData injectData = new Dagger2InjectData();
            Dagger2ComponentContainer.getApplicationDagger2Component().inject(injectData);

            Log.d(LOG_TAG, "Binding category plans source - creating new adapter");
            adapter = injectData.itemsCollectionAdapterProvider
                    .provideCategoryPlansRecyclerViewAdapter(
                            injectData.dateTimeChangeListenersRegistry,
                            new YearMonthPair(monthlyPlanningCategoryListViewModel.getMonthData().getYear(), monthlyPlanningCategoryListViewModel.getMonthData().getMonth()),
                            new ObservableArrayList<CategoryPlansViewModel>());

            ((CategoryPlansRecyclerViewAdapter)adapter).setUpItemDragging(injectData.categoryListItemDragCallback);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        if(adapter instanceof CategoryPlansRecyclerViewAdapter)
        {
            Log.d(LOG_TAG, "Binding category plans source - updating adapter source");
            CategoryPlansRecyclerViewAdapter dataAdapter = (CategoryPlansRecyclerViewAdapter) adapter;
            dataAdapter.setYearMonthPair(new YearMonthPair(year, month));
            dataAdapter.updateSourceCollection(itemsSource);

        }
        Log.d(LOG_TAG, "Binding category plans source - END");
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

            injectData.recyclerViewSimultaneousScrollingController.addForSimultaneousScrolling(recyclerView);
            adapter = injectData.itemsCollectionAdapterProvider
                    .provideDailyPlanHeaderRecyclerViewAdapter(
                            new YearMonthPair(monthViewModel.getYear(), monthViewModel.getMonth()),
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

    @BindingAdapter("bind:scrollSimultaneouslyWithHeader")
    public static void setScrollSimultaneouslyWithHeader(RecyclerView recyclerView, Boolean value)
    {
        if(recyclerView == null)
            return;

        Dagger2InjectData injectData = new Dagger2InjectData();
        Dagger2ComponentContainer.getApplicationDagger2Component().inject(injectData);

        if(value)
            injectData.recyclerViewSimultaneousScrollingController.addForSimultaneousScrolling(recyclerView);
        else
            injectData.recyclerViewSimultaneousScrollingController.removeFromSimultaneousScrolling(recyclerView);
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

    @BindingAdapter("bind:onLayoutReady")
    public static void setOnLayoutReadyCallback(final View view, final OnLayoutReadyCallback callback){
        if(view == null)
            return;

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (callback != null) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    callback.onLayoutReady(view);
                }
            }
        });
    }

    @BindingAdapter("bind:backupSourcesSource")
    public static void setBackupSourcesItemsSource(RecyclerView recyclerView, ObservableList<BackupSourceViewModel> itemsSource)
    {
        Log.d(LOG_TAG, "Binding backup providers source - START");
        if(recyclerView == null)
            return;

        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        if(adapter == null)
        {
            Dagger2InjectData injectData = new Dagger2InjectData();
            Dagger2ComponentContainer.getApplicationDagger2Component().inject(injectData);

            Log.d(LOG_TAG, "Binding backup providers source - creating new adapter");
            adapter = injectData.itemsCollectionAdapterProvider
                        .provideBackusSourcesRecyclerViewAdapter();
            recyclerView.setAdapter(adapter);
        }

        if((adapter instanceof BackupSourcesRecyclerViewAdapter))
        {
            Log.d(LOG_TAG, "Binding backup providers source - updating adapter source");
            BackupSourcesRecyclerViewAdapter dataAdapter = (BackupSourcesRecyclerViewAdapter) adapter;
            dataAdapter.updateSourceCollection(itemsSource);
        }

        Log.d(LOG_TAG, "Binding backup providers source - END");
    }

    public static class Dagger2InjectData{

        @Inject
        ItemsCollectionAdapterProvider itemsCollectionAdapterProvider;

        @Inject
        ApplicationContext applicationContext;

        @Inject
        DateTimeChangeListenersRegistry dateTimeChangeListenersRegistry;

        @Inject
        ItemDragCallback categoryListItemDragCallback;

        @Inject
        RecyclerViewSimultaneousScrollingController recyclerViewSimultaneousScrollingController;
    }
}
