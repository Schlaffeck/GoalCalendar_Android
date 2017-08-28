package com.slamcode.goalcalendar.view.lists.gestures;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

/**
 * Option inflater for creating simple image button with icon for menu option
 */

public class ImageButtonSwipeMenuViewInflater implements SwipeMenuOption.ViewInflater {

    private int drawableId;
    private final int drawableColorId;

    public ImageButtonSwipeMenuViewInflater(int drawableId, int drawableColorId) {
        this.drawableId = drawableId;
        this.drawableColorId = drawableColorId;
    }

    @Override
    public View inflateView(final SwipeMenuOption option, Context context, AttributeSet viewAttributes) {
        ImageButton button = new ImageButton(context, viewAttributes);
        button.setImageDrawable(ContextCompat.getDrawable(context, this.drawableId));
        button.setColorFilter(ContextCompat.getColor(context, this.drawableColorId), PorterDuff.Mode.MULTIPLY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                option.optionSelectedAction();
            }
        });
        return button;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public int getDrawableColorId() {
        return drawableColorId;
    }
}
