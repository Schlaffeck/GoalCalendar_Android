package com.slamcode.goalcalendar;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by moriasla on 15.12.2016.
 */

public class CategoriesArrayAdapter extends ArrayAdapter<CategoryInMonthListElement> {

    public CategoriesArrayAdapter(Context context, int resource, List<CategoryInMonthListElement> elems) {
        super(context, resource, elems);
    }

}
