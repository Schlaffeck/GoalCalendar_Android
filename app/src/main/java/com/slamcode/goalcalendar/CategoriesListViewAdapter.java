package com.slamcode.goalcalendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.slamcode.goalcalendar.base.ListViewDataAdapter;
import com.slamcode.goalcalendar.base.ViewHolderBase;
import com.slamcode.goalcalendar.data.model.*;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.*;

/**
 * Created by moriasla on 15.12.2016.
 */

public class CategoriesListViewAdapter extends ListViewDataAdapter<CategoryModel, CategoriesListViewAdapter.CategoryViewHolder> {

        private List<CategoryModel> list;
        private Context context;
        private LayoutInflater layoutInflater;

        public CategoriesListViewAdapter(Context context, LayoutInflater layoutInflater)
        {
            super(context, layoutInflater);
            this.list = new ArrayList<CategoryModel>();
            this.context = context;
            this.layoutInflater = layoutInflater;
        }

    @Override
    protected CategoryViewHolder getNewViewHolder(View convertView) {
        convertView = this.layoutInflater.inflate(R.layout.list_item_monthly_goals,null);

        return new CategoryViewHolder(
                convertView,
                (TextView)convertView.findViewById(R.id.monthly_goals_list_item_category_name),
                (TextView)convertView.findViewById(R.id.monthly_goals_list_item_frequency),
                (GridView)convertView.findViewById(R.id.monthly_goals_list_item_days_list));
    }

    @Override
    protected void fillListElementView(CategoryModel monthlyGoals, final CategoryViewHolder viewHolder)
    {
        // put view holder to array so it can be modified from inner class
        final CategoryViewHolder[] innerViewHolder = new CategoryViewHolder[] { viewHolder };

        innerViewHolder[0].categoryNameTextView.setText(monthlyGoals.getName());
        innerViewHolder[0].frequencyTextView.setText(monthlyGoals.getFrequency().toString());
        //innerViewHolder[0].daysListGridView.setAdapter();
    }

    public class CategoryViewHolder extends ViewHolderBase<CategoryModel>
    {
        private CategoryModel baseObject;
        private TextView categoryNameTextView;
        private TextView frequencyTextView;
        private GridView daysListGridView;

        CategoryViewHolder(
                View view,
                TextView categoryNameTextView,
                   TextView frequencyTextView,
                   GridView daysListGridView)
        {
            super(view);
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
