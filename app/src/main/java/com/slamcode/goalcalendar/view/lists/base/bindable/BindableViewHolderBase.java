package com.slamcode.goalcalendar.view.lists.base.bindable;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.slamcode.goalcalendar.view.lists.base.ViewHolderBase;

/**
 * Created by moriasla on 03.03.2017.
 */

public class BindableViewHolderBase<Item> extends ViewHolderBase<Item> {

    private final ViewDataBinding binding;

    public BindableViewHolderBase(View view) {
        this(view, null);
    }

    public BindableViewHolderBase(View view, Item modelObject) {
        super(view, modelObject);
        this.binding = DataBindingUtil.bind(view);
    }

    @Override
    public void bindToModel(Item modelObject) {
        super.bindToModel(modelObject);
        binding.setVariable(BR.vm, modelObject);
    }
}
