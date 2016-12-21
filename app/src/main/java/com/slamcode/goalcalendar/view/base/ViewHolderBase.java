package com.slamcode.goalcalendar.view.base;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.TextView;

/**
 * Created by moriasla on 16.12.2016.
 */

public class ViewHolderBase<TData> {

    private final View view;
    private TData baseObject;
    private long id;
    private boolean viewVisible;

    public ViewHolderBase(View view, long id)
    {
        this.id = id;
        this.view = view;
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View v = getView();
                v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if(v.getMeasuredHeight() >0 && v.getMeasuredWidth() > 0)
                {
                    setViewVisible();
                }
            }
        });
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

    public long getId() {
        return id;
    }

    private void setViewVisible()
    {
        this.viewVisible = true;
    }

    public boolean isViewVisible()
    {
        return this.viewVisible;
    }
}
