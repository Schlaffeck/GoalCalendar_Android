package com.slamcode.goalcalendar.view.base;

import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

/**
 * Created by moriasla on 16.12.2016.
 */

public class ViewHolderBase<TData> {

    private final View view;
    private TData baseObject;

    public ViewHolderBase(View view)
    {
        this.view = view;
    }

    public TData getBaseObject() {
        return baseObject;
    }

    public void setBaseObject(TData baseObject) {
        this.baseObject = baseObject;
    }

    public View getView() {
        return view;
    }
}
