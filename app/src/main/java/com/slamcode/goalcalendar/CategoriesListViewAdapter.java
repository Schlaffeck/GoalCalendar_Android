package com.slamcode.goalcalendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.slamcode.goalcalendar.data.model.*;

import org.json.JSONException;

import java.util.*;

/**
 * Created by moriasla on 15.12.2016.
 */

public class CategoriesListViewAdapter extends BaseAdapter {

        private List<CategoryModel> list;
        private Context context;
        private LayoutInflater layoutInflater;

        public CategoriesListViewAdapter(Context context, LayoutInflater layoutInflater)
        {
            this.list = new ArrayList<CategoryModel>();
            this.context = context;
            this.layoutInflater = layoutInflater;
        }

        @Override
        public int getCount()
        {
            return this.list != null ? this.list.size() : 0;
        }

        @Override
        public CategoryModel getItem(int position)
        {
            CategoryModel result = null;
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
            ViewHolder viewHolder;
            if(convertView == null)
            {
                // load layout for item
                convertView = this.layoutInflater.inflate(R.layout.list_item_monthly_goals,null);

                // create new holder for view
                viewHolder = new ViewHolder(
                        (TextView) convertView.findViewById(R.id.monthly_goals_list_item_category_name),
                        (TextView) convertView.findViewById(R.id.monthly_goals_list_item_frequency),
                        (GridView) convertView.findViewById(R.id.monthly_goals_list_item_days_list));

                // hook up the holder to the view for recyclage
                convertView.setTag(viewHolder);
            }
            else
            {
                // skip all the expensive inflation/findViewById
                // and just get the holder you already made
                viewHolder = (ViewHolder) convertView.getTag();
            }

            CategoryModel object = this.getItem(position);
            if(object != null && object != viewHolder.getBaseObject())
            {
                viewHolder.setBaseObject(object);
                try
                {
                    // read card data to show on view
                    this.fillListElementView(object, viewHolder);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("Card data conversion", e.getMessage());
                }
            }

            return convertView;
        }

    public void updateList(List<CategoryModel> newList)
    {
        this.list = newList;
        this.notifyDataSetChanged();
    }

    private static void fillListElementView(CategoryModel monthlyGoals, final ViewHolder viewHolder) throws JSONException
    {
        // put view holder to array so it can be modified from inner class
        final ViewHolder[] innerViewHolder = new ViewHolder[] { viewHolder };

        innerViewHolder[0].categoryNameTextView.setText(monthlyGoals.getName());
        innerViewHolder[0].frequencyTextView.setText(monthlyGoals.getFrequency().toString());
    }

    private static class ViewHolder
    {
        private CategoryModel baseObject;
        private TextView categoryNameTextView;
        private TextView frequencyTextView;
        private GridView daysListGridView;

        ViewHolder(TextView categoryNameTextView,
                   TextView frequencyTextView,
                   GridView daysListGridView)
        {
            this.categoryNameTextView = categoryNameTextView;
            this.frequencyTextView = frequencyTextView;
            this.daysListGridView = daysListGridView;
        }

        public CategoryModel getBaseObject() {
            return baseObject;
        }

        public void setBaseObject(CategoryModel baseObject) {
            this.baseObject = baseObject;
        }
    }
}
