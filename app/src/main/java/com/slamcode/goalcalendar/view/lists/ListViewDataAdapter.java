package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.slamcode.goalcalendar.data.model.CategoryModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public abstract class ListViewDataAdapter<TData, TViewHolder extends ViewHolderBase<TData>> extends BaseAdapter {

    private List<TData> list;
    private Context context;
    private LayoutInflater layoutInflater;
    private List<ItemsSourceChangedEventListener> adapterSourceChangedEventListeners;

    private Comparator<TData> itemsComparator;

    private List<TData> modifiedItems;

    protected ListViewDataAdapter(Context context, LayoutInflater layoutInflater)
    {
        this.list = new ArrayList<>();
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

    @Override
    public int getCount()
    {
        return this.list != null ? this.list.size() : 0;
    }

    @Override
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        TViewHolder viewHolder;
        long id = this.getItemId(position);
        if(convertView == null
                || ((TViewHolder)convertView.getTag()).getId() != id)
        {
            // create new holder for view
            viewHolder = this.getNewViewHolder(convertView, id);
            convertView = viewHolder.getView();

            // hook UP the holder to the view for recyclage
            convertView.setTag(viewHolder);
        }
        else
        {
            // skip all the expensive inflation/findViewById
            // and just get the holder you already made
            viewHolder = (TViewHolder) convertView.getTag();
        }

        TData object = this.getItem(position);
        if(object != null
                && (object != viewHolder.getBaseObject()
                    || this.modifiedItems.contains(object)))
        {
            viewHolder.setBaseObject(object);
            try
            {
                this.fillListElementView(object, viewHolder);
                this.modifiedItems.remove(object);
                convertView = viewHolder.getView();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e("Data conversion", e.getMessage());
            }
        }

        return convertView;
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

    protected abstract TViewHolder getNewViewHolder(View convertView, long id);

    protected abstract void fillListElementView(TData data, final TViewHolder viewHolder);

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
