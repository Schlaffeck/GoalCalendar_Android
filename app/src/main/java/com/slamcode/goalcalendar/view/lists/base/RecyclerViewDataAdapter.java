package com.slamcode.goalcalendar.view.lists.base;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.slamcode.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moriasla on 16.12.2016.
 */

public abstract class RecyclerViewDataAdapter<Item, ViewHolder extends ViewHolderBase<Item>> extends RecyclerView.Adapter<ViewHolder> {

    public static final int ITEM_VIEM_TYPE_LAST_ITEM = 202;
    public static final int ITEM_VIEM_TYPE_REGULAR_ITEM = 101;

    private SortedList<Item> list;
    private Context context;
    private LayoutInflater layoutInflater;

    private Map<Item, ViewHolder> modelToViewHolderMap = new HashMap<>();

    protected RecyclerViewDataAdapter(Context context, LayoutInflater layoutInflater, SortedList<Item> sourceList)
    {
        this.list = sourceList;
        this.context = context;
        this.layoutInflater = layoutInflater;
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
    public int getItemViewType(int position) {
        if(position == this.list.size() -1)
            return ITEM_VIEM_TYPE_LAST_ITEM;

        return ITEM_VIEM_TYPE_REGULAR_ITEM;
    }

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Item item = this.list.get(position);
        holder.bindToModel(item);
        this.modelToViewHolderMap.put(item, holder);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.list != null ? this.list.size() : 0;
    }

    protected LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    protected Context getContext() {
        return context;
    }

    public void updateSourceCollection(Collection<Item> newSourceCollection) {
        if(newSourceCollection == null)
            newSourceCollection = CollectionUtils.emptyList();

        if(this.list == null)
            return;

        this.list.clear();
        this.list.addAll(newSourceCollection);
        this.notifyDataSetChanged();
    }

    public void updateSourceCollectionOneByOne(Collection<Item> newSourceCollection) {
        if(newSourceCollection == null)
            newSourceCollection = CollectionUtils.emptyList();

        if(this.list == null)
            return;

        this.list.clear();
        this.notifyDataSetChanged();
        int i = 0;
        for(Item item : newSourceCollection)
        {
            this.list.add(item);
            this.notifyItemInserted(i++);
        }
    }

    private int getItemIndex(Item item)
    {
        int i = -1;
        boolean found = false;
        while(!found && ++i < this.list.size())
            found = this.list.get(i) == item;
        return found ? i : -1;
    }

    public ViewHolder getViewHolderForPosition(int itemPosition)
    {
        Item item = this.getItem(itemPosition);

        if(!modelToViewHolderMap.containsKey(item))
            return null;

        return modelToViewHolderMap.get(item);
    }
}
