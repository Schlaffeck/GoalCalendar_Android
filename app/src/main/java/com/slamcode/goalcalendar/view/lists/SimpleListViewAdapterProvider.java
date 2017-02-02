package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.view.LayoutInflater;

import com.slamcode.goalcalendar.view.CategoryListViewAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by moriasla on 02.02.2017.
 */

public class SimpleListViewAdapterProvider implements ListAdapterProvider {

    @Override
    public CategoryListViewAdapter provideCategoryListViewAdapter(Context context, LayoutInflater inflater) {
        return new CategoryListViewAdapter(context, inflater);
    }
}
