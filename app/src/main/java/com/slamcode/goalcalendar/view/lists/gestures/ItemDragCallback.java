package com.slamcode.goalcalendar.view.lists.gestures;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemDragCallback extends ItemTouchHelper.Callback {

    private List<ItemDragMoveListener> itemDragMoveListeners = new ArrayList<>();

    private boolean draggingEnabled = false;

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = this.draggingEnabled ? ItemTouchHelper.UP | ItemTouchHelper.DOWN : 0;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return this.draggingEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if(fromPosition == toPosition)
            return false;

        for(ItemDragMoveListener listener : this.itemDragMoveListeners)
            listener.onItemDragMoved(fromPosition, toPosition);

        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }

    public void addOnItemGestureListener(ItemDragMoveListener listener)
    {
        this.itemDragMoveListeners.add(listener);
    }

    public void removeOnItemGestureListener(ItemDragMoveListener listener)
    {
        this.itemDragMoveListeners.remove(listener);
    }

    public void clearOnItemGestureListeners()
    {
        this.itemDragMoveListeners.clear();
    }

    public boolean isDraggingEnabled() {
        return draggingEnabled;
    }

    public void setDraggingEnabled(boolean draggingEnabled) {
        this.draggingEnabled = draggingEnabled;
    }

    /**
     * Simple interface providing contract for all listeners wanting to know whether item in recycler view
     * was dragged and moved to another position
     */

    public interface ItemDragMoveListener {

        void onItemDragMoved(int fromPosition, int toPosition);
    }
}
