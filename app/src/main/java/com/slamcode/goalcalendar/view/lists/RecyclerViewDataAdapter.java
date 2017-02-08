package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.data.model.CategoryModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public abstract class RecyclerViewDataAdapter<Item, ViewHolder extends ViewHolderBase<Item>> extends RecyclerView.Adapter<ViewHolder> {

    private SortedList<Item> list;
    private Context context;
    private LayoutInflater layoutInflater;
    private List<ItemsSourceChangedEventListener> adapterSourceChangedEventListeners;

    private List<Item> modifiedItems;

    protected RecyclerViewDataAdapter(Context context, LayoutInflater layoutInflater, SortedList<Item> sourceList)
    {
        this.list = sourceList;
        this.modifiedItems = new ArrayList<>();
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.adapterSourceChangedEventListeners = new ArrayList<>();
    }

    public Item getItem(int position)
    {
        Item result = null;
        try
        {
            result =  this.list != null ? this.list.get(position) : null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("List item read", e.getMessage());
        }

        return result;
    }

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(ViewHolder holder, int position);

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.list != null ? this.list.size() : 0;
    }

    public void addOrUpdateItem(Item item)
    {
        if(this.list.indexOf(item) == -1)
        {
            this.list.add(item);
            this.notifyDataSetChanged();
            this.notifyItemAdded(this.list.indexOf(item));
        }
        else {
            this.modifiedItems.add(item);
            this.notifyItemModified(this.list.indexOf(item));
            this.notifyDataSetChanged();
        }
    }

    public void removeItem(Item item)
    {
        if(this.list.indexOf(item) == -1) {
            return;
        }

        this.list.remove(item);
        this.modifiedItems.remove(item);
        this.notifyDataSetChanged();
        this.notifyItemRemoved(item);
    }

    public void addItemSourceChangedEventListener(ItemsSourceChangedEventListener<Item> listener)
    {
        if(this.adapterSourceChangedEventListeners.contains(listener))
        {
            return;
        }
        this.adapterSourceChangedEventListeners.add(listener);
    }

    public void removeItemSourceChangedEventListener(ItemsSourceChangedEventListener listener)
    {
        if(!this.adapterSourceChangedEventListeners.contains(listener))
        {
            return;
        }

        this.adapterSourceChangedEventListeners.remove(listener);
    }

    protected void notifyItemAdded(int position)
    {
        for (ItemsSourceChangedEventListener listener :
                this.adapterSourceChangedEventListeners) {
            listener.onNewItemAdded(position);
        }
    }

    protected void notifyItemModified(int position)
    {
        for (ItemsSourceChangedEventListener listener :
                this.adapterSourceChangedEventListeners) {
            listener.onItemModified(position);
        }
    }

    protected void notifyItemRemoved(Item item)
    {
        for (ItemsSourceChangedEventListener listener :
                this.adapterSourceChangedEventListeners) {
            listener.onItemRemoved(item);
        }
    }

    public LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    public Context getContext() {
        return context;
    }

    protected void updateSourceCollection(Collection<Item> newSourceCollection) {
        this.list.clear();
        this.list.addAll(newSourceCollection);
    }

    public interface ItemsSourceChangedEventListener<ItemType>
    {
        void onNewItemAdded(int itemPosition);

        void onItemModified(int itemPosition);

        void onItemRemoved(ItemType item);
    }
}
