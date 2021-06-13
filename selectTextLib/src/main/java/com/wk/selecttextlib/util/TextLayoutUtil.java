package com.wk.selecttextlib.util;

import android.content.Context;
import android.text.Layout;
import android.widget.TextView;

public class TextLayoutUtil {

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取文本控件中与指定坐标距离最近字符在文本内容中的索引
     *
     * @param textView 文本控件
     * @param x        文本控件的内部x坐标点
     * @param y        文本控件的内部y坐标点
     * @return 索引值
     */
    public static int getPreciseOffset(TextView textView, int x, int y) {
        Layout layout = textView.getLayout();
        if (layout != null) {
            //获取y坐标所在行
            int topVisibleLine = layout.getLineForVertical(y);
            //获取指定行最接近x坐标的字符偏移量
            int offset = layout.getOffsetForHorizontal(topVisibleLine, x);
            //获取该字符左边的x坐标
            int offsetX = (int) layout.getPrimaryHorizontal(offset);

            if (offsetX > x) {
                return layout.getOffsetToLeftOf(offset);
            } else {
                return offset;
            }
        } else {
            return -1;
        }
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}