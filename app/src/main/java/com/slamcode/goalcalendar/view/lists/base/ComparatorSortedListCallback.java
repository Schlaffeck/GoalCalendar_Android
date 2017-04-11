package com.slamcode.goalcalendar.view.lists.base;

import android.support.v7.util.SortedList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by moriasla on 08.02.2017.
 */

public class ComparatorSortedListCallback<Model> extends SortedList.Callback<Model> {

    private final Comparator<Model> comparator;

    private Set<ItemsChangeListener> listeners = new HashSet<>();

    public ComparatorSortedListCallback(Comparator<Model> comparator)
    {
        this.comparator = comparator;
    }

    public void addItemsChangeListener(ItemsChangeListener listener)
    {
        this.listeners.add(listener);
    }

    public void removeItemsChangeListener(ItemsChangeListener listener)
    {
        this.listeners.remove(listener);
    }

    public void clearItemsChangeListener()
    {
        this.listeners.clear();
    }

    @Override
    public int compare(Model o1, Model o2) {
        return this.comparator.compare(o1, o2);
    }

    @Override
    public void onChanged(int position, int count) {

        if(this.listeners.isEmpty())
            return;

        for (ItemsChangeListener listener :
                this.listeners) {
            listener.onItemChanged(position, count);
        }
    }

    @Override
    public boolean areContentsTheSame(Model oldItem, Model newItem) {
        return comparator.compare(oldItem, newItem) == 0;
    }

    @Override
    public boolean areItemsTheSame(Model item1, Model item2) {

        return item1 == item2;
    }

    @Override
    public void onInserted(int position, int count) {

        if(this.listeners.isEmpty())
            return;

        for (ItemsChangeListener listener :
                this.listeners) {
            listener.onItemInserted(position, count);
        }
    }

    @Override
    public void onRemoved(int position, int count) {

        if(this.listeners.isEmpty())
            return;

        for (ItemsChangeListener listener :
                this.listeners) {
            listener.onItemRemoved(position, count);
        }
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {

        if(this.listeners.isEmpty())
            return;

        for (ItemsChangeListener listener :
                this.listeners) {
            listener.onItemMoved(fromPosition, toPosition);
        }
    }

    public interface ItemsChangeListener
    {
        void onItemMoved(int fromPosition, int toPosition);

        void onItemInserted(int position, int count);

        void onItemRemoved(int position, int count);

        void onItemChanged(int position, int count);
    }
}
