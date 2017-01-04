package com.slamcode.goalcalendar.view.lists;

import android.view.View;
import android.view.ViewTreeObserver;

import butterknife.ButterKnife;

/**
 * Created by moriasla on 16.12.2016.
 */

public class ViewHolderBase<TData> {

    private final View view;
    private TData baseObject;
    private long id;
    private boolean viewRendered;

    public ViewHolderBase(View view, long id)
    {
        this.id = id;
        this.view = view;
        ButterKnife.bind(this, view);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View v = getView();
                v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if(v.getMeasuredHeight() >0 && v.getMeasuredWidth() > 0)
                {
                    setViewRendered();
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

    private void setViewRendered()
    {
        this.viewRendered = true;
    }

    public boolean isViewRendered()
    {
        return this.viewRendered;
    }
}
