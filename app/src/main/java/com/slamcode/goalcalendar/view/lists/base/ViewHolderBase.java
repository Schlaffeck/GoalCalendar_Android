package com.slamcode.goalcalendar.view.lists.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import butterknife.ButterKnife;

/**
 * Created by moriasla on 16.12.2016.
 */

public class ViewHolderBase<Model> extends RecyclerView.ViewHolder {

    private final View view;
    private boolean viewRendered;

    private Model modelObject;

    public ViewHolderBase(View view){
        this(view, null);
    }

    public ViewHolderBase(View view, Model modelObject)
    {
        super(view);
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

        this.modelObject = modelObject;
    }

    public View getView() {
        return view;
    }

    private void setViewRendered()
    {
        this.viewRendered = true;
    }

    public boolean isViewRendered()
    {
        return this.viewRendered;
    }

    public Model getModelObject() {
        return modelObject;
    }

    public void bindToModel(Model modelObject)
    {
        this.modelObject = modelObject;
    }
}
