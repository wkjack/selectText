package com.wk.selecttextlib.selectText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.wk.selecttextlib.LastSelectListener;
import com.wk.selecttextlib.LastSelectManager;
import com.wk.selecttextlib.R;
import com.wk.selecttextlib.SelectionInfo;
import com.wk.selecttextlib.TextLayoutUtil;
import com.wk.selecttextlib.util.ClickUtil;

@SuppressLint("ClickableViewAccessibility")
public class SelectTextHelper implements LastSelectListener {

    private CursorHandle mStartHandle; // 选中起始图标
    private CursorHandle mEndHandle; // 选中结束图标
    private SelectOptionPop mOperateWindow; //操作弹框
    private final SelectionInfo mSelectionInfo = new SelectionInfo(); //选中信息
    private OnSelectOptionListener selectOptionListener;

    private final Context mContext; //上下文
    private final TextView mTextView; //文本控件
    private final int mSelectedColor; //选中背景
    private final int mCursorHandleColor; //选中图标颜色
    private final int mCursorHandleSize; //选中图标尺寸


    private Spannable mSpannable; //文本内容
    private BackgroundColorSpan mSpan; //选中背景Span

    private View.OnClickListener originalClickListener; //控件原有的点击事件

    private boolean isTriggerLongClick = false; //是否触发长按点击事件
    private boolean isTouchDown = false; //是否触发ACTION_DOWN
    private MotionEvent downEvent; //记录ACTION_DOWN的event


    public SelectTextHelper(Builder builder) {
        mTextView = builder.mTextView;
        mContext = mTextView.getContext();
        mSelectedColor = builder.mSelectedColor;
        mCursorHandleColor = builder.mCursorHandleColor;
        mCursorHandleSize = TextLayoutUtil.dp2px(mContext, builder.mCursorHandleSizeInDp);

        selectOptionListener = new DefOnSelectOptionListener(this);

        init();
    }

    private void init() {
        //设置文本控件可设置样式
        mTextView.setText(mTextView.getText(), TextView.BufferType.SPANNABLE);
        originalClickListener = ClickUtil.getViewClickListener(mTextView);
        mTextView.setOnLongClickListener(v -> {
            //长按显示选中布局
            isTriggerLongClick = true;
            showSelectView(0, mTextView.getText().length());
            return true;
        });

        mTextView.setOnTouchListener((v, event) -> {
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

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LastSelectListener lastSelectText = LastSelectManager.getInstance().getLastSelect();
                if (lastSelectText != null) {
                    lastSelectText.clearOperate();
                    LastSelectManager.getInstance().setLastSelect(null);

                    if (!lastSelectText.equals(SelectTextHelper.this)) {
                        if (originalClickListener != null) {
                            originalClickListener.onClick(v);
                        }
                    }
                } else {
                    if (originalClickListener != null) {
                        originalClickListener.onClick(v);
                    }
                }
            }
        });
        mTextView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
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

    /**
     * 显示选中控件
     *
     * @param start 文本控件的内部x坐标点
     * @param end   文本控件的内部y坐标点
     */
    private void showSelectView(int start, int end) {
        if (mTextView.getText() instanceof Spannable) {
            mSpannable = (Spannable) mTextView.getText();
        }
        //确保文本内容样式可设置且选中索引在文本内容范围内
        if (mSpannable == null || start >= mTextView.getText().length()) {
            hideOperatePopup();
            LastSelectListener lastSelectText = LastSelectManager.getInstance().getLastSelect();
            if (lastSelectText != null && lastSelectText.equals(SelectTextHelper.this)) {
                LastSelectManager.getInstance().setLastSelect(null);
            }
            return;
        }

        selectInfo(start, end);
        showOperatePopup();

        //确保只会有一个处于选择复制中
        LastSelectListener lastSelectText = LastSelectManager.getInstance().getLastSelect();
        if (lastSelectText != null && !lastSelectText.equals(this)) {
            lastSelectText.clearOperate();
        }
        LastSelectManager.getInstance().setLastSelect(this);
    }

    public void setSelectOptionListener(@NonNull OnSelectOptionListener selectOptionListener) {
        this.selectOptionListener = selectOptionListener;
    }

    /**
     * 销毁
     */
    public void destroy() {
        clearOperate();
        mStartHandle = null;
        mEndHandle = null;
        mOperateWindow = null;

        //记录的缓存为自身时才清除缓存
        LastSelectListener lastSelectText = LastSelectManager.getInstance().getLastSelect();
        if (lastSelectText != null && lastSelectText.equals(SelectTextHelper.this)) {
            LastSelectManager.getInstance().setLastSelect(null);
        }
    }

    //==========  对外提供的方法  ==========

    public OnSelectOptionListener getSelectOptionListener() {
        return selectOptionListener;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public SelectionInfo getSelectionInfo() {
        return mSelectionInfo;
    }

    int getmCursorHandleSize() {
        return mCursorHandleSize;
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

    @Override
    public void clearOperate() {
        clearSelectInfo();
        hideOperatePopup();
    }

    private void clearSelectInfo() {
        //清空选中内容、样式
        mSelectionInfo.mSelectionContent = null;
        mSelectionInfo.mStart = -1;
        mSelectionInfo.mEnd = -1;

        if (mSpannable != null && mSpan != null) {
            mSpannable.removeSpan(mSpan);
            mSpan = null;
        }
    }

    /**
     * 设置选中内容
     *
     * @param start 起始位置
     * @param end   结束位置
     */
    public void selectInfo(int start, int end) {
        //更新选中信息：起始位、结束位
        if (start != -1) {
            mSelectionInfo.mStart = start;
        }
        if (end != -1) {
            mSelectionInfo.mEnd = end;
        }

        //校对起始位、结束位
        if (mSelectionInfo.mStart > mSelectionInfo.mEnd) {
            int temp = mSelectionInfo.mStart;
            mSelectionInfo.mStart = mSelectionInfo.mEnd;
            mSelectionInfo.mEnd = temp;
        }

        try {
            if (mSpannable != null) {
                if (mSpan == null) {
                    mSpan = new BackgroundColorSpan(mSelectedColor);
                }
                //更新选中内容
                mSelectionInfo.mSelectionContent = mSpannable.subSequence(mSelectionInfo.mStart, mSelectionInfo.mEnd).toString();
                //设置选中样式
                mSpannable.setSpan(mSpan, mSelectionInfo.mStart, mSelectionInfo.mEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示操作
     */
    public void showOperatePopup() {
        if (mStartHandle == null) {
            mStartHandle = new CursorHandle(true);
            mStartHandle.firstShow();
        } else {
            mStartHandle.show();
        }
        if (mEndHandle == null) {
            mEndHandle = new CursorHandle(false);
            mEndHandle.firstShow();
        } else {
            mEndHandle.show();
        }
        if (mOperateWindow != null) {
            mOperateWindow.show(mTextView, mSelectionInfo, mOperateWindow.isShowing());
        } else {
            mOperateWindow = new SelectOptionPop(this);
            mOperateWindow.show(mTextView, mSelectionInfo, false);
        }
    }

    /**
     * 隐藏操作
     */
    private void hideOperatePopup() {
        if (mStartHandle != null) {
            mStartHandle.dismiss();
        }
        if (mEndHandle != null) {
            mEndHandle.dismiss();
        }
        hideSelectOptionPup();
    }

    private void hideSelectOptionPup() {
        if (mOperateWindow != null) {
            mOperateWindow.dismiss();
        }
    }

    /**
     * 光标图标控件
     */
    private class CursorHandle extends View {

        private PopupWindow mPopupWindow;
        private Paint mPaint;

        private int mCircleRadius = mCursorHandleSize / 2; //圆半径
        private int mWidth = mCircleRadius * 2; //宽
        private int mHeight = mCircleRadius * 2; //高
        private boolean isLeft; //是否为左边

        public CursorHandle(boolean isLeft) {
            super(mContext);
            this.isLeft = isLeft;
            //画笔
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(mCursorHandleColor);

            //光标弹框
            mPopupWindow = new PopupWindow(this);
            mPopupWindow.setClippingEnabled(false);
            mPopupWindow.setWidth(mWidth);
            mPopupWindow.setHeight(mHeight);
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawCircle(mCircleRadius, mCircleRadius, mCircleRadius, mPaint);
            if (isLeft) {
                canvas.drawRect(mCircleRadius, 0, mCircleRadius * 2, mCircleRadius, mPaint);
            } else {
                canvas.drawRect(0, 0, mCircleRadius, mCircleRadius, mPaint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    //触摸结束处理：显示操作框
                    mOperateWindow.show(mTextView, mSelectionInfo, mOperateWindow.isShowing());
                    break;
                case MotionEvent.ACTION_MOVE:
                    //触摸移动：隐藏操作框，记录当前点并更新
                    hideSelectOptionPup();
                    int rawX = (int) event.getRawX();
                    int rawY = (int) event.getRawY();
                    update(rawX, rawY);
                    break;
            }
            return true;
        }

        /**
         * 变更方向：光标是起始位还是结束位
         */
        private void changeDirection() {
            isLeft = !isLeft;
            invalidate();
        }

        public void dismiss() {
            mPopupWindow.dismiss();
        }

        public void update(int rawX, int rawY) {
            int[] mTempCoors = new int[2];
            mTextView.getLocationInWindow(mTempCoors);

            //根据当前坐标倒推选中文字索引
            int x = rawX - mTempCoors[0] - mTextView.getPaddingLeft() + (isLeft ? mWidth : 0);
            int y = rawY - mTempCoors[1] - mTextView.getPaddingTop();

            //获取文本控件中最接近坐标的字符的索引位置
            int offset = TextLayoutUtil.getPreciseOffset(mTextView, x, y);
            int oldOffset = isLeft ? mSelectionInfo.mStart : mSelectionInfo.mEnd;

            //新旧偏移位不一致：更新信息
            if (offset != oldOffset) {
                if (isLeft) {
                    //当前游标为起始游标
                    if (offset > mSelectionInfo.mEnd) {
                        //起始游标滑动到结束游标之后，需要处理：
                        //1.更新选中内容
                        selectInfo(mSelectionInfo.mEnd, offset);

                        //2.结束游标变更为起始坐标
                        CursorHandle handle = getCursorHandle(false);
                        handle.changeDirection();
                        handle.show();

                        //3.起始游标变更为结束游标
                        changeDirection();
                    } else {
                        selectInfo(offset, -1);
                    }
                } else {
                    if (offset < mSelectionInfo.mStart) {
                        //起始游标滑动到结束游标之后，需要处理：
                        //1.更新选中内容
                        selectInfo(offset, mSelectionInfo.mStart);

                        //2.起始游标变更为结束游标
                        CursorHandle handle = getCursorHandle(true);
                        handle.changeDirection();
                        handle.show();

                        //3.结束游标变更为起始坐标
                        changeDirection();
                    } else {
                        selectInfo(-1, offset);
                    }
                }
                show();
            }
        }

        private void firstShow() {
            try {
                int[] mTempCoors = new int[2];
                mTextView.getLocationInWindow(mTempCoors);
                Layout layout = mTextView.getLayout();

                int offset = isLeft ? mSelectionInfo.mStart : mSelectionInfo.mEnd;

                //获取该字符左边的x坐标
                int x = (int) layout.getPrimaryHorizontal(offset);
                //先获取所在行数，再获取此行的底部位置
                int y = layout.getLineBottom(layout.getLineForOffset(offset));

                int realX = mTempCoors[0] + mTextView.getPaddingLeft() + x - (isLeft ? mWidth : 0);
                int realY = mTempCoors[1] + mTextView.getPaddingTop() + y;


                Rect rect = new Rect();
                mTextView.getGlobalVisibleRect(rect);

                mPopupWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, realX, realY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 统一计算显示位置并显示游标
         */
        private void show() {
            try {
                int[] mTempCoors = new int[2];
                mTextView.getLocationInWindow(mTempCoors);
                Layout layout = mTextView.getLayout();

                int offset = isLeft ? mSelectionInfo.mStart : mSelectionInfo.mEnd;

                //获取该字符左边的x坐标
                int x = (int) layout.getPrimaryHorizontal(offset);
                //先获取所在行数，再获取此行的底部位置
                int y = layout.getLineBottom(layout.getLineForOffset(offset));

                int realX = mTempCoors[0] + mTextView.getPaddingLeft() + x - (isLeft ? mWidth : 0);
                int realY = mTempCoors[1] + mTextView.getPaddingTop() + y;


                Rect rect = new Rect();
                mTextView.getGlobalVisibleRect(rect);

                if (realY + mHeight < rect.top || realY > rect.bottom + 5) {
                    //超出控件的显示区域：隐藏游标
                    dismiss();
                } else {
                    if (isShowing()) {
                        mPopupWindow.update(realX, realY, -1, -1);
                    } else {
                        mPopupWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, realX, realY);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean isShowing() {
            return mPopupWindow.isShowing();
        }
    }

    private CursorHandle getCursorHandle(boolean isLeft) {
        if (mStartHandle.isLeft == isLeft) {
            return mStartHandle;
        } else {
            return mEndHandle;
        }
    }


    public static class Builder {

        TextView mTextView;
        int mCursorHandleColor = 0xFF1379D6;
        int mSelectedColor = 0xFFAFE1F4;
        float mCursorHandleSizeInDp = 24;

        public Builder(TextView textView) {
            mTextView = textView;
        }

        public Builder setCursorHandleColor(@ColorInt int cursorHandleColor) {
            mCursorHandleColor = cursorHandleColor;
            return this;
        }

        public Builder setCursorHandleSizeInDp(float cursorHandleSizeInDp) {
            mCursorHandleSizeInDp = cursorHandleSizeInDp;
            return this;
        }

        public Builder setSelectedColor(@ColorInt int selectedBgColor) {
            mSelectedColor = selectedBgColor;
            return this;
        }

        public SelectTextHelper build() {
            return new SelectTextHelper(this);
        }
    }
}