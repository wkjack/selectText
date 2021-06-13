package com.wk.selecttextlib;

import android.view.MotionEvent;

public interface LastSelectListener {

    /**
     * 清除操作
     */
    void clearOperate();

    void hideOperate();

    void showOperate();


    boolean isOnTouchDown();

    /**
     * 按下操作：未触摸到控件
     */
    void onTouchDownOutside(MotionEvent event);

    void onScroll();

    void onFling();

    void onScrollFromOther();
}