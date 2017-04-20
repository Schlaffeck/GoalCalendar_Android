package com.slamcode.goalcalendar.view.utils;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.slamcode.goalcalendar.MonthlyGoalsActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by schlaffeck on 20.04.2017.
 */

public class ViewBinder {

    public static void bindViews(Activity activity) {

        if(activity == null)
            throw new IllegalArgumentException("activity is null");

        bindViews(activity, activity.findViewById(android.R.id.content));

//        for(Field fieldData : activity.getClass().getDeclaredFields())
//        {
//            ViewReference ann = fieldData.getAnnotation(ViewReference.class);
//            if(ann != null)
//            {
//                View view = activity.findViewById(ann.value());
//                if(view != null)
//                {
//                    try {
//                        fieldData.set(activity, fieldData.getType().cast(view));
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
    }

    public static void bindViews(Object object, View mainView)
    {
        if(mainView == null)
            throw new IllegalArgumentException("mainView is null");

        bindFieldsToViews(object, mainView);
        bindMethodsToListeners(object, mainView);
    }

    private static void bindFieldsToViews(Object object, View mainView)
    {
        for(Field fieldData : object.getClass().getDeclaredFields())
        {
            ViewReference ann = fieldData.getAnnotation(ViewReference.class);
            if(ann != null)
            {
                View view = mainView.findViewById(ann.value());
                if(view != null)
                {
                    try {
                        fieldData.set(object, fieldData.getType().cast(view));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void bindMethodsToListeners(final Object object, View mainView)
    {
        for(final Method methodData : object.getClass().getMethods())
        {
            ViewOnClick ann = methodData.getAnnotation(ViewOnClick.class);
            if(ann != null)
            {
                View view = mainView.findViewById(ann.value());
                if(view != null && view instanceof Button)
                {
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    methodData.invoke(object);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                }
            }
        }
    }

    public static<ViewType extends View> ViewType findView(MonthlyGoalsActivity activity, int viewId) {
        View view = activity.findViewById(viewId);

        if(view == null) return null;

        return (ViewType) view;
    }
}
