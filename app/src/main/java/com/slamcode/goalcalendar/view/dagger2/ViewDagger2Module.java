package com.slamcode.goalcalendar.view.dagger2;

import android.content.Context;
import android.view.LayoutInflater;

import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.view.CategoryListViewAdapter;
import com.slamcode.goalcalendar.view.activity.ActivityViewStateProvider;
import com.slamcode.goalcalendar.view.lists.ListAdapterProvider;
import com.slamcode.goalcalendar.view.lists.ListViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.SimpleListViewAdapterProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by moriasla on 13.01.2017.
 */
@Module
public final class ViewDagger2Module {

    @Provides
    @Singleton
    public ActivityViewStateProvider provideActivityViewStateProvider()
    {
        return new ActivityViewStateProvider();
    }

    @Provides
    @Singleton
    public ListAdapterProvider provideListViewAdapterProvider()
    {
        SimpleListViewAdapterProvider provider = new SimpleListViewAdapterProvider();
        return provider;
    }
}
