package com.slamcode.goalcalendar.view.lists.swipe;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemTouchCallback extends ItemTouchHelper.Callback {

    private List<ItemTouchListener> touchListeners = new ArrayList<>();

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.END | ItemTouchHelper.START;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if(fromPosition == toPosition)
            return false;

        for(ItemTouchListener listener : this.touchListeners)
            listener.onItemMoved(fromPosition, toPosition);

        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int itemPosition = viewHolder.getAdapterPosition();
        for(ItemTouchListener listener : this.touchListeners)
            listener.onItemSwiped(itemPosition);
    }

    public void addOnItemTouchListener(ItemTouchListener listener)
    {
        this.touchListeners.add(listener);
    }

    public void removeOnItemTouchListener(ItemTouchListener listener)
    {
        this.touchListeners.remove(listener);
    }

    public void clearOnItemTouchListeners()
    {
        this.touchListeners.clear();
    }

    public interface ItemTouchListener
    {
        void onItemMoved(int fromPosition, int toPosition);

        void onItemSwiped(int itemPosition);
    }
}
