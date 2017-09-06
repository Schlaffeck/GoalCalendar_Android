package com.slamcode.goalcalendar.view.lists.scrolling;

import com.slamcode.goalcalendar.view.controls.ListenableHorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

public final class HorizontalScrollViewSimultaneousScrollingController implements SimultaneousScrollingController<ListenableHorizontalScrollView> {

    private List<ListenableHorizontalScrollView> scrollViewList = new ArrayList<>();
    private OnScrolledListener scrollListener = new OnScrolledListener();

    private int currentScrollX = 0;
    private int currentScrollY = 0;

    public void addForSimultaneousScrolling(ListenableHorizontalScrollView scrollView)
    {
        if(scrollViewList.contains(scrollView))
            return;

        scrollViewList.add(scrollView);
        scrollView.addOnScrollListener(this.scrollListener);
        this.scrollListenerAdded(scrollView);
    }

    public void removeFromSimultaneousScrolling(ListenableHorizontalScrollView scrollView)
    {
        scrollViewList.remove(scrollView);
        scrollView.removeOnScrollListener(this.scrollListener);
    }

    public void clearScrollableControls()
    {
        for (ListenableHorizontalScrollView scrollView : scrollViewList)
                scrollView.removeOnScrollListener(this.scrollListener);

        scrollViewList.clear();
    }

    private void scrollListenerAdded(ListenableHorizontalScrollView scrollView)
    {
        scrollView.scrollTo(currentScrollX, currentScrollY);
    }

    private class OnScrolledListener implements ListenableHorizontalScrollView.OnScrollListener{

        @Override
        public void onScrolled(ListenableHorizontalScrollView scrollView, int dx, int dy) {

            currentScrollX = scrollView.getScrollX();
            currentScrollY = scrollView.getScrollY();

            for (ListenableHorizontalScrollView scrollViewInList : scrollViewList) {
                if(scrollViewInList != scrollView)
                {
                    scrollViewInList.removeOnScrollListener(this);
                    scrollViewInList.scrollTo(currentScrollX, currentScrollY);
                    scrollViewInList.addOnScrollListener(this);
                }
            }
        }
    }
}
