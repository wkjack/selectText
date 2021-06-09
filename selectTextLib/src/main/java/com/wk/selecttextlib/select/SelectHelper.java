package com.wk.selecttextlib.select;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.wk.selecttextlib.LastSelectListener;
import com.wk.selecttextlib.LastSelectManager;
import com.wk.selecttextlib.util.ClickUtil;

/**
 * 长按选择帮助类，提供指定控件的选择弹框处理
 */
@SuppressLint("ClickableViewAccessibility")
public class SelectHelper implements LastSelectListener {

    private SelectPop mOperateWindow; //操作弹框

    private final Context mContext; //上下文
    private final View mView; //控件
    private OnSelectListener selectListener;

    private View.OnClickListener originalClickListener; //控件原有的点击事件

    private boolean isTriggerLongClick = false; //是否触发长按点击事件
    private boolean isTouchDown = false; //是否触发ACTION_DOWN
    private MotionEvent downEvent; //记录ACTION_DOWN的event


    public SelectHelper(SelectHelper.Builder builder) {
        mView = builder.view;
        mContext = mView.getContext();
        init();
    }

    private void init() {
        originalClickListener = ClickUtil.getViewClickListener(mView);

        mView.setOnLongClickListener(v -> {
            isTriggerLongClick = true;
            showSelectView();
            return true;
        });

        mView.setOnTouchListener((v, event) -> {
            //记录触摸点坐标
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isTriggerLongClick = false;
                    isTouchDown = true;
                    downEvent = event;
                    break;

                case MotionEvent.ACTION_MOVE:
                    //应对按下后触发点击事件后继续滑动，此时隐藏操作
                    if (isTriggerLongClick) {
                        //已触发点击事件
                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                    //应对按下后触发点击事件后继续滑动,松开时已划出控件区域
                    downEvent = null;
                    if (isTriggerLongClick) {
                        //已触发点击事件
                        clearOperate();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    //应对按下后触发点击事件后继续滑动直到松开，此时显示操作
                    isTouchDown = false;
                    downEvent = null;
                    if (isTriggerLongClick) {
                        //已触发点击事件
                        showOperatePopup();
                    }
                    break;
            }
            return false;
        });

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LastSelectListener lastSelectText = LastSelectManager.getInstance().getLastSelect();
                if (lastSelectText != null) {
                    lastSelectText.clearOperate();
                    LastSelectManager.getInstance().setLastSelect(null);
                }
                if (originalClickListener != null) {
                    originalClickListener.onClick(v);
                }
            }
        });

        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                //控件解除绑定做销毁操作
                destroy();
            }
        });
    }

    public void setSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    /**
     * 显示选中控件
     */
    private void showSelectView() {
        showOperatePopup();
        //确保只会有一个处于选择复制中
        LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
        if (lastSelect != null && !lastSelect.equals(this)) {
            lastSelect.clearOperate();
        }
        LastSelectManager.getInstance().setLastSelect(this);
    }

    /**
     * 销毁
     */
    private void destroy() {
        clearOperate();
        mOperateWindow = null;

        //记录的缓存为自身时才清除缓存
        LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
        if (lastSelect != null && lastSelect.equals(SelectHelper.this)) {
            LastSelectManager.getInstance().setLastSelect(null);
        }
    }

    //==========  对外提供的方法  ==========

    public OnSelectListener getSelectListener() {
        return selectListener;
    }

    public View getView() {
        return mView;
    }

    @Override
    public void clearOperate() {
        hideOperatePopup();
    }

    @Override
    public boolean isOnTouchDown() {
        return isTouchDown;
    }

    @Override
    public void onTouchDownOutside(MotionEvent motionEvent) {
        if (!motionEvent.equals(downEvent)) {
            isTouchDown = false;
        }
    }

    @Override
    public void onScrollFromOther() {
        isTouchDown = false;
        clearOperate();
    }

    @Override
    public void onScroll() {
        if (isTouchDown) {
            showOperatePopup();
        }
    }

    @Override
    public void onFling() {
        isTouchDown = false;
    }

    /**
     * 显示操作
     */
    private void showOperatePopup() {
        if (mOperateWindow != null) {
            mOperateWindow.show(mView, mOperateWindow.isShowing());
        } else {
            mOperateWindow = new SelectPop(this);
            mOperateWindow.show(mView, false);
        }
    }

    /**
     * 隐藏操作
     */
    private void hideOperatePopup() {
        hideSelectOptionPup();
    }

    private void hideSelectOptionPup() {
        if (mOperateWindow != null) {
            mOperateWindow.dismiss();
        }
    }


    public static class Builder {

        private View view;

        public Builder(View view) {
            this.view = view;
        }

        public SelectHelper build() {
            return new SelectHelper(this);
        }
    }
}