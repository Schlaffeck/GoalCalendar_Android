package com.slamcode.goalcalendar.view.lists.utils;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public final class RecyclerViewSimultaneousScrollingController {

    private List<RecyclerView> recyclerViewList = new ArrayList<>();
    private ScrollSimultaneouslyListener onScrollListener = new ScrollSimultaneouslyListener();

    public void addForSimultaneousScrolling(RecyclerView recyclerView)
    {
        if(recyclerViewList.contains(recyclerView))
            return;

        recyclerViewList.add(recyclerView);
        recyclerView.addOnScrollListener(onScrollListener);
        onScrollListener.scrollListenerAdded(recyclerView);
    }

    public void removeFromSimultaneousScrolling(RecyclerView recyclerView)
    {
        recyclerViewList.remove(recyclerView);
        recyclerView.removeOnScrollListener(onScrollListener);
    }

    public void clearAllRecyclerViews()
    {
        for (RecyclerView recyclerView : recyclerViewList)
            recyclerView.removeOnScrollListener(onScrollListener);


        recyclerViewList.clear();
    }

    private class ScrollSimultaneouslyListener extends RecyclerView.OnScrollListener {

        private int fullScrollX = 0;
        private int fullScrollY = 0;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            for (RecyclerView recyclerViewInList : recyclerViewList) {
                if(recyclerViewInList != recyclerView)
                {
                    recyclerViewInList.removeOnScrollListener(this);
                    recyclerViewInList.scrollBy(dx, dy);
                    recyclerViewInList.addOnScrollListener(this);
                }
                else
                {
                    fullScrollX += dx;
                    fullScrollY += dy;
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        public void scrollListenerAdded(RecyclerView recyclerView)
        {
            recyclerView.scrollTo(fullScrollX, fullScrollY);
        }
    }
}
