package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.goalcalendar.data.model.CategoryModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public abstract class RecyclerViewDataAdapter<TData, TViewHolder extends ViewHolderBase<TData>> extends RecyclerView.Adapter<TViewHolder> {

    private List<TData> list;
    private Context context;
    private LayoutInflater layoutInflater;
    private List<ItemsSourceChangedEventListener> adapterSourceChangedEventListeners;

    private Comparator<TData> itemsComparator;

    private List<TData> modifiedItems;

    protected RecyclerViewDataAdapter(Context context, LayoutInflater layoutInflater)
    {
        this(context, layoutInflater, CollectionUtils.<TData>emptyList());
    }

    protected RecyclerViewDataAdapter(Context context, LayoutInflater layoutInflater, List<TData> sourceList)
    {
        this.list = sourceList;
        this.modifiedItems = new ArrayList<>();
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.adapterSourceChangedEventListeners = new ArrayList<>();
    }

    public Comparator<TData> getItemsComparator() {
        return itemsComparator;
    }

    protected void setItemsComparator(Comparator<TData> itemsComparator) {
        this.itemsComparator = itemsComparator;
    }

    public TData getItem(int position)
    {
        TData result = null;
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
    public abstract TViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(TViewHolder holder, int position);

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.list != null ? this.list.size() : 0;
    }

    public void addOrUpdateItem(TData item)
    {
        if(!this.list.contains(item))
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

    public void removeItem(TData item)
    {
        if(!this.list.contains(item)) {
            return;
        }

        this.list.remove(item);
        this.modifiedItems.remove(item);
        this.notifyDataSetChanged();
        this.notifyItemRemoved(item);
    }

    public void addItemSourceChangedEventListener(ItemsSourceChangedEventListener<TData> listener)
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

    protected List<TData> getList()
    {
        return this.list;
    }

    protected void setList(List<TData> newList)
    {
        this.list = newList;
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

    protected void notifyItemRemoved(TData item)
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

    public interface ItemsSourceChangedEventListener<ItemType>
    {
        void onNewItemAdded(int itemPosition);

        void onItemModified(int itemPosition);

        void onItemRemoved(ItemType item);
    }
}
