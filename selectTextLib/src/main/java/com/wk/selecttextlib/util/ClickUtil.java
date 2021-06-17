package com.wk.selecttextlib.util;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

public class ClickUtil {

    @SuppressLint("DiscouragedPrivateApi")
    public static View.OnClickListener getViewClickListener(@NonNull View view) {
        try {
            Field field = View.class.getDeclaredField("mListenerInfo");
            field.setAccessible(true);
            Object object = field.get(view);
            field = object.getClass().getDeclaredField("mOnClickListener");
            field.setAccessible(true);
            object = field.get(object);
            if (object != null && object instanceof View.OnClickListener) {
                return ((View.OnClickListener) object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("DiscouragedPrivateApi")
    public static View.OnLongClickListener getViewLongClickListener(@NonNull View view) {
        try {
            Field field = View.class.getDeclaredField("mListenerInfo");
            field.setAccessible(true);
            Object object = field.get(view);
            field = object.getClass().getDeclaredField("mOnLongClickListener");
            field.setAccessible(true);
            object = field.get(object);
            if (object != null && object instanceof View.OnLongClickListener) {
                return ((View.OnLongClickListener) object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}