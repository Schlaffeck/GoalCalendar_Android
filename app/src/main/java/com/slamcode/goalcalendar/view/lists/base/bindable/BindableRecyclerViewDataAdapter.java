package com.slamcode.goalcalendar.view.lists.base.bindable;

import android.content.Context;
import android.databinding.Observable;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;

import com.slamcode.goalcalendar.view.lists.base.RecyclerViewDataAdapter;

/**
 * Created by moriasla on 03.03.2017.
 */

public abstract class BindableRecyclerViewDataAdapter<Item extends Observable, ViewHolder extends BindableViewHolderBase<Item>> extends RecyclerViewDataAdapter<Item, ViewHolder> {

    private final ObservableSortedList<Item> sourceList;

    protected BindableRecyclerViewDataAdapter(Context context, LayoutInflater layoutInflater, ObservableSortedList<Item> sourceList) {
        super(context, layoutInflater, sourceList);
        this.sourceList = sourceList;
    }

    protected ObservableSortedList<Item> getSourceList() {
        return sourceList;
    }
}
