package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.dagger2.Dagger2ComponentContainer;
import com.slamcode.goalcalendar.view.PlansSummaryForCategoriesRecyclerViewAdapter;
import com.slamcode.goalcalendar.viewmodels.PlansSummaryForCategoryViewModel;

import java.util.Collection;

import javax.inject.Inject;

/**
 * Created by moriasla on 01.03.2017.
 */

public class Bindings {

    @BindingAdapter("categoryList")
    public static void setItemsSource(RecyclerView recyclerView, Collection<PlansSummaryForCategoryViewModel> itemsSource)
    {
        if(recyclerView == null)
            return;

        RecyclerView.Adapter adapter = recyclerView.getAdapter();

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

        if(adapter instanceof PlansSummaryForCategoriesRecyclerViewAdapter)
        {
            PlansSummaryForCategoriesRecyclerViewAdapter dataAdapter = (PlansSummaryForCategoriesRecyclerViewAdapter) adapter;
            dataAdapter.updateSourceCollection(itemsSource);
        }
    }

    public static class Dagger2InjectData{

        @Inject
        ItemsCollectionAdapterProvider itemsCollectionAdapterProvider;

        @Inject
        ApplicationContext applicationContext;

    }
}
