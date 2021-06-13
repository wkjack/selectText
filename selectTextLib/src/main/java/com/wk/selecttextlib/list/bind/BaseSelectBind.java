package com.wk.selecttextlib.list.bind;

import android.view.MotionEvent;

import com.wk.selecttextlib.list.model.SelectDataInfo;

public class BaseSelectBind {


    protected boolean isTriggerLongClick = false; //是否触发长按点击事件
    protected boolean isTouchDown = false; //是否触发ACTION_DOWN
    protected MotionEvent downEvent; //记录ACTION_DOWN的event


    public boolean isTriggerLongClick() {
        return isTriggerLongClick;
    }

    public boolean isTouchDown() {
        return isTouchDown;
    }




    /**
     * 供手势onDown
     *
     * @param event 事件
     */
    public void onGestureDown(MotionEvent event) {

    }

    public void clear() {}

    public void update() {}

    public String getSelectData(SelectDataInfo selectDataInfo) {
        return null;
    }
}
