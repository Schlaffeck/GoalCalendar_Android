package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public abstract class RecyclerViewDataAdapter<Item, ViewHolder extends ViewHolderBase<Item>> extends RecyclerView.Adapter<ViewHolder> {

    private SortedList<Item> list;
    private Context context;
    private LayoutInflater layoutInflater;
    private List<ItemsSourceChangeRequestListener> adapterSourceRequestsListeners;

    private List<Item> modifiedItems;

    protected RecyclerViewDataAdapter(Context context, LayoutInflater layoutInflater, SortedList<Item> sourceList)
    {
        this.list = sourceList;
        this.modifiedItems = new ArrayList<>();
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.adapterSourceRequestsListeners = new ArrayList<>();
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
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.bindToModel(this.list.get(position));
    }

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
        int itemIndex = this.getItemIndex(item);
        if(itemIndex == -1)
        {
            this.list.add(item);
            this.notifyItemInserted(this.list.indexOf(item));
        }
        else {
            this.modifiedItems.add(item);
            this.list.recalculatePositionOfItemAt(itemIndex);
            int newPosition = this.list.indexOf(item);
            this.notifyItemMoved(itemIndex, newPosition);
            this.notifyItemChanged(newPosition);
        }
    }

    public void removeItem(Item item)
    {
        if(this.list.indexOf(item) == -1) {
            return;
        }

        int position = this.list.indexOf(item);
        this.list.remove(item);
        this.modifiedItems.remove(item);
        this.notifyItemRemoved(position);
    }

    public void addItemSourceRequestListener(ItemsSourceChangeRequestListener listener)
    {
        if(this.adapterSourceRequestsListeners.contains(listener))
        {
            return;
        }
        this.adapterSourceRequestsListeners.add(listener);
    }

    public void removeItemSourceRequestListener(ItemsSourceChangeRequestListener listener)
    {
        if(!this.adapterSourceRequestsListeners.contains(listener))
        {
            return;
        }

        this.adapterSourceRequestsListeners.remove(listener);
    }

    public void clearItemSourceRequestListener()
    {
        this.adapterSourceRequestsListeners.clear();
    }

    protected void notifyItemModificationRequested(int position)
    {
        for(ItemsSourceChangeRequestListener listener :
                this.adapterSourceRequestsListeners)
            listener.onModifyItemRequest(position);
    }

    protected void notifyItemRemovalRequested(int position)
    {
        for(ItemsSourceChangeRequestListener listener :
                this.adapterSourceRequestsListeners)
            listener.onRemoveItemRequest(position);
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

    private int getItemIndex(Item item)
    {
        int i = -1;
        boolean found = false;
        while(!found && ++i < this.list.size())
            found = this.list.get(i) == item;
        return found ? i : -1;
    }

    public interface ItemsSourceChangeRequestListener
    {
        void onModifyItemRequest(int itemPosition);

        void onRemoveItemRequest(int itemPosition);
    }
}
