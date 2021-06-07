package com.wk.selecttextlib.select;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;

import com.wk.selecttextlib.LastSelectListener;
import com.wk.selecttextlib.LastSelectManager;

/**
 * 长按选择帮助类，提供指定控件的选择弹框处理
 */
@SuppressLint("ClickableViewAccessibility")
public class SelectHelper implements LastSelectListener {

    //    private final static int DEFAULT_SELECTION_LENGTH = 1;

    private SelectPop mOperateWindow; //操作弹框

    private final Context mContext; //上下文
    private final View mView; //控件
    private OnSelectListener selectListener;

    private int mTouchX; //触点坐标X
    private int mTouchY; //触点坐标Y

    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;
    private boolean isHideOpetate = true;

    public SelectHelper(SelectHelper.Builder builder) {
        mView = builder.view;
        mContext = mView.getContext();
        init();
    }

    private void init() {
        mView.setOnLongClickListener(v -> {
            //长按显示选中布局
            showSelectView(mTouchX, mTouchY);
            return true;
        });

        mView.setOnTouchListener((v, event) -> {
            //记录触摸点坐标
            mTouchX = (int) event.getX();
            mTouchY = (int) event.getY();
            return false;
        });

        mView.setOnClickListener(v -> {
            clearOperate();

            LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
            if (lastSelect != null && !lastSelect.equals(SelectHelper.this)) {
                lastSelect.clearOperate();
            }
            LastSelectManager.getInstance().setLastSelect(null);
        });
        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        //滚动监听处理
                        if (!isHideOpetate) {
                            showOperatePopup();
                        }
                    }
                };
                mView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);
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
     *
     * @param x 文本控件的内部x坐标点
     * @param y 文本控件的内部y坐标点
     */
    private void showSelectView(int x, int y) {
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
    public void destroy() {
        mView.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
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

    /**
     * 显示操作
     */
    public void showOperatePopup() {
        isHideOpetate = false;

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
    public void hideOperatePopup() {
        isHideOpetate = true;
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