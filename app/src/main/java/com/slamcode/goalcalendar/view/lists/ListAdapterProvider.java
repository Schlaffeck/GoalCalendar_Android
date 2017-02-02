package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.view.LayoutInflater;

import com.slamcode.goalcalendar.view.CategoryListViewAdapter;

/**
 * Created by moriasla on 02.02.2017.
 */

public interface ListAdapterProvider {

    CategoryListViewAdapter provideCategoryListViewAdapter(Context context, LayoutInflater inflater);
}
