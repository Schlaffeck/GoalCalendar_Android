package com.slamcode.goalcalendar.view.lists.gestures;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Represents data for swipe menu attached to item
 */

public class ItemSwipeMenu {

    private final ViewGroup menuLayoutView;
    private List<SwipeMenuOption> menuOptions = new ArrayList<>();

    private View itemView;
    private boolean showing;

    private boolean layoutSetUp;

    public ItemSwipeMenu(View itemView, ViewGroup menuLayoutView, SwipeMenuOption... options) {
        this.itemView = itemView;
        this.menuLayoutView = menuLayoutView;
        for(SwipeMenuOption option : options)
            menuOptions.add(option);
    }

    public void addMenuOption(SwipeMenuOption option)
    {
        menuOptions.add(option);
        this.menuLayoutView.addView(option.inflateView(this.itemView.getContext()));
    }

    public void clearMenuOptions()
    {
        this.menuOptions.clear();
        this.layoutSetUp = false;
        this.setUpMenuLayout();
    }

    public void showMenu()
    {
        this.setUpMenuLayout();
        this.menuLayoutView.setVisibility(View.VISIBLE);
        this.showing = true;
    }

    public void hideMenu()
    {
        this.menuLayoutView.setVisibility(View.GONE);
        this.showing = false;
    }

    private void setUpMenuLayout()
    {
        if(!this.layoutSetUp) {
            for (SwipeMenuOption option : menuOptions)
                this.addMenuOption(option);
        }
    }

    public boolean isShowing() {
        return showing;
    }
}
