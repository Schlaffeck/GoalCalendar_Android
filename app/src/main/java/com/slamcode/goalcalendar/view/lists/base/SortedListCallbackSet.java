package com.slamcode.goalcalendar.view.lists.base;

import android.support.v7.util.SortedList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by moriasla on 13.03.2017.
 */

public class SortedListCallbackSet<Item> extends SortedList.Callback<Item> {

    private Set<SortedList.Callback<Item>> callbacks = new HashSet<>();
    private Comparator<Item> itemComparator;

    public SortedListCallbackSet(Comparator<Item> itemComparator) {
        this(Collections.<SortedList.Callback<Item>>emptyList(), itemComparator);
    }

    public SortedListCallbackSet(SortedList.Callback<Item> callback, Comparator<Item> itemComparator) {
        this(Arrays.asList(callback), itemComparator);
    }

    public SortedListCallbackSet(Collection<SortedList.Callback<Item>> callbacks) {
        this(callbacks, null);
    }

    public SortedListCallbackSet(Collection<SortedList.Callback<Item>> callbacks, Comparator<Item> itemComparator) {
        this.callbacks.addAll(callbacks);
        this.itemComparator = itemComparator;
    }

    public void addCallback(SortedList.Callback<Item> callback){
        this.callbacks.add(callback);
    }

    public void removeCallback(SortedList.Callback<Item> callback){
        this.callbacks.remove(callback);
    }

    public void clearCallbacks(){
        this.callbacks.clear();
    }

    @Override
    public int compare(Item o1, Item o2) {

        if(this.itemComparator != null)
            return itemComparator.compare(o1, o2);

        if(this.callbacks == null || this.callbacks.isEmpty())
            return 0;

        Integer value = null;
        for (SortedList.Callback<Item> callback : this.callbacks){
            int comparisonResult = callback.compare(o1, o2);
            if(value!= null && comparisonResult != value)
                break;
        }

        return value!=null ? value : 0;
    }

    @Override
    public void onChanged(int position, int count) {
        for (SortedList.Callback<Item> callback : this.callbacks){
            callback.onChanged(position, count);
        }
    }

    @Override
    public boolean areContentsTheSame(Item oldItem, Item newItem) {
        return this.compare(oldItem, newItem) == 0;
    }

    @Override
    public boolean areItemsTheSame(Item item1, Item item2) {
        return item1 == item2;
    }

    @Override
    public void onInserted(int position, int count) {
        for (SortedList.Callback<Item> callback : this.callbacks){
            callback.onInserted(position, count);
        }
    }

    @Override
    public void onRemoved(int position, int count) {
        for (SortedList.Callback<Item> callback : this.callbacks){
            callback.onRemoved(position, count);
        }
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        for (SortedList.Callback<Item> callback : this.callbacks){
            callback.onMoved(fromPosition, toPosition);
        }
    }
}
