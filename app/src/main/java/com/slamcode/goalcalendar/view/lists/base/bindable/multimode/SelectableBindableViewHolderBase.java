package com.slamcode.goalcalendar.view.lists.base.bindable.multimode;

import com.android.databinding.library.baseAdapters.BR;
import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementSelector;
import com.slamcode.goalcalendar.view.lists.base.selection.MultiSelector;
import com.slamcode.goalcalendar.view.lists.base.selection.SelectableViewHolder;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableViewHolderBase;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ViewDataBinding;
import android.view.View;

public class SelectableBindableViewHolderBase<Item extends Observable> extends BindableViewHolderBase<Item> implements SelectableViewHolder, View.OnLongClickListener {

    private final MultiSelector multiSelector;
    private final View selectedModeView;
    private ViewDataBinding selectedModeViewBinding;

    public SelectableBindableViewHolderBase(View view, MultiSelector multiSelector) {
        this(view, null, multiSelector, null);
    }

    public SelectableBindableViewHolderBase(View view, Item modelObject, MultiSelector multiSelector) {
        this(view, modelObject, multiSelector, null);
    }

    public SelectableBindableViewHolderBase(View view, Item modelObject, MultiSelector multiSelector, View selectedModeView) {
        super(view, modelObject);
        this.multiSelector = multiSelector;
        this.selectedModeView = selectedModeView;

        if(this.selectedModeView != null)
            this.selectedModeViewBinding = DataBindingUtil.bind(this.selectedModeView);
    }

    @Override
    public void bindToModel(Item modelObject) {
        super.bindToModel(modelObject);
        if(this.selectedModeViewBinding != null)
            this.selectedModeViewBinding.setVariable(BR.vm, modelObject);
    }

    @Override
    public boolean onLongClick(View v) {

        if(this.multiSelector.toggleSelection(this))
        {

        }
        return false;
    }

    @Override
    public boolean isSelected() {
        return CollectionUtils.any(this.multiSelector.getSelectedItemIds(), new ElementSelector<Integer, Boolean>() {
            @Override
            public Boolean select(Integer parent) {
                return parent != null && parent.equals(SelectableBindableViewHolderBase.this.getItemId());
            }
        });
    }
}
