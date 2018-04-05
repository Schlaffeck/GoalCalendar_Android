package com.slamcode.goalcalendar.view.lists.base.bindable.multimode;

import com.slamcode.goalcalendar.view.lists.base.selection.MultiSelector;
import com.slamcode.goalcalendar.view.lists.base.selection.SelectableViewHolder;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableViewHolderBase;

import android.databinding.Observable;
import android.view.View;

public class SelectableBindableViewHolderBase<Item extends Observable> extends BindableViewHolderBase<Item> implements SelectableViewHolder, View.OnLongClickListener {

    private final MultiSelector multiSelector;

    public SelectableBindableViewHolderBase(View view, MultiSelector multiSelector) {
        super(view);
        this.multiSelector = multiSelector;
    }

    public SelectableBindableViewHolderBase(View view, Item modelObject, MultiSelector multiSelector) {
        super(view, modelObject);
        this.multiSelector = multiSelector;
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public boolean isSelected() {
        return false;
    }
}
