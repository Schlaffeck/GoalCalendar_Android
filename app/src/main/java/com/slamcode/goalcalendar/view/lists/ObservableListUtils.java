package com.slamcode.goalcalendar.view.lists;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import java.util.Collection;

public class ObservableListUtils {

    public static <Item> ObservableList<Item> createObservableList(Collection<Item> baseCollection)
    {
        if(baseCollection == null)
            return null;

        ObservableArrayList<Item> result = new ObservableArrayList<Item>();

        result.addAll(baseCollection);

        return result;
    }
}
