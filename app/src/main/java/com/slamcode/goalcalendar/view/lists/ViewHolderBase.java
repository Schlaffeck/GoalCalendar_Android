package com.slamcode.goalcalendar.view.lists;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import butterknife.ButterKnife;

/**
 * Created by moriasla on 16.12.2016.
 */

public class ViewHolderBase<TData> extends RecyclerView.ViewHolder {

    private final View view;
    private boolean viewRendered;

    public ViewHolderBase(View view)
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
}
