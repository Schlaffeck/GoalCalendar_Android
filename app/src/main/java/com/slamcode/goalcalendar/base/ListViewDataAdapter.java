package com.slamcode.goalcalendar.base;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.slamcode.goalcalendar.CategoriesListViewAdapter;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.model.CategoryModel;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public abstract class ListViewDataAdapter<TData, TViewHolder extends ViewHolderBase<TData>> extends BaseAdapter {

    private List<TData> list;
    private Context context;
    private LayoutInflater layoutInflater;

    protected ListViewDataAdapter(Context context, LayoutInflater layoutInflater)
    {
        this.list = new ArrayList<TData>();
        this.context = context;
        this.layoutInflater = layoutInflater;
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
        if(convertView == null)
        {
            // create new holder for view
            viewHolder = this.getNewViewHolder(convertView);
            convertView = viewHolder.getView();

            // hook up the holder to the view for recyclage
            convertView.setTag(viewHolder);
        }
        else
        {
            // skip all the expensive inflation/findViewById
            // and just get the holder you already made
            viewHolder = (TViewHolder) convertView.getTag();
        }

        TData object = this.getItem(position);
        if(object != null && object != viewHolder.getBaseObject())
        {
            viewHolder.setBaseObject(object);
            try
            {
                // read card data to show on view
                this.fillListElementView(object, viewHolder);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e("Data conversion", e.getMessage());
            }
        }

        return convertView;
    }

    public void updateList(List<TData> newList)
    {
        this.list = newList;
        this.notifyDataSetChanged();
    }

    protected abstract TViewHolder getNewViewHolder(View convertView);

    protected abstract void fillListElementView(TData monthlyGoals, final TViewHolder viewHolder);
}
