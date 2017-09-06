package com.slamcode.goalcalendar.view.lists.scrolling;

import com.slamcode.goalcalendar.view.controls.ListenableHorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

public final class HorizontalScrollViewSimultaneousScrollingController implements SimultaneousScrollingController<ListenableHorizontalScrollView> {

    private List<ListenableHorizontalScrollView> scrollViewList = new ArrayList<>();
    private OnScrolledListener scrollListener = new OnScrolledListener();

    public void addForSimultaneousScrolling(ListenableHorizontalScrollView scrollView)
    {
        if(scrollViewList.contains(scrollView))
            return;

        scrollViewList.add(scrollView);
        scrollView.addOnScrollListener(this.scrollListener);
        this.scrollListener.scrollListenerAdded(scrollView);
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

    private class OnScrolledListener implements ListenableHorizontalScrollView.OnScrollListener{

        private int fullScrollX = 0;
        private int fullScrollY = 0;

        @Override
        public void onScrolled(ListenableHorizontalScrollView scrollView, int dx, int dy) {
            for (ListenableHorizontalScrollView scrollViewInList : scrollViewList) {
                if(scrollViewInList != scrollView)
                {
                    scrollViewInList.removeOnScrollListener(this);
                    scrollViewInList.scrollBy(dx, dy);
                    scrollViewInList.addOnScrollListener(this);
                }
                else
                {
                    fullScrollX += dx;
                    fullScrollY += dy;
                }
            }
        }

        private void scrollListenerAdded(ListenableHorizontalScrollView scrollView)
        {
            scrollView.scrollTo(fullScrollX, fullScrollY);
        }
    }
}
