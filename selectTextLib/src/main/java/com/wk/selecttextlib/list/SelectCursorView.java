package com.wk.selecttextlib.list;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wk.selecttextlib.TextLayoutUtil;
import com.wk.selecttextlib.list.listener.OnSelectCursorListener;
import com.wk.selecttextlib.list.listener.OnSelectPopListener;
import com.wk.selecttextlib.list.model.SelectDataInfo;

/**
 * 游标控件
 */
public class SelectCursorView extends View {


    private Context context;
    private boolean isLeft; //是否为左边
    private int mCursorHandleColor;
    private int mCursorHandleSize;
    private OnSelectCursorListener selectCursorListener;
    private OnSelectPopListener selectPopListener;

    private PopupWindow mPopupWindow;
    private Paint mPaint;

    private int mCircleRadius; //圆半径
    private int mWidth; //宽
    private int mHeight; //高


    private SelectCursorView(Build build) {
        super(build.context);
        this.isLeft = build.isLeft;
        this.mCursorHandleColor = build.mCursorHandleColor;
        this.mCursorHandleSize = build.mCursorHandleSize;
        this.selectCursorListener = build.selectCursorListener;
        this.selectPopListener = build.selectPopListener;

        //初始化数据
        this.mCircleRadius = mCursorHandleSize / 2; //圆半径
        this.mWidth = mCircleRadius * 2;
        this.mHeight = mCircleRadius * 2;

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
                selectCursorListener.hideOperatePop();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //触摸结束处理：显示操作框
                selectCursorListener.showOperatePop();
                break;

            case MotionEvent.ACTION_MOVE:
                //触摸移动：隐藏操作框，记录当前点并更新
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
    public void changeDirection() {
        isLeft = !isLeft;
        invalidate();
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

    public void update(int rawX, int rawY) {
        View dependentView = selectPopListener.getDependentView();
        SelectDataInfo selectDataInfo = selectPopListener.getSelectDataInfo();

        if (selectDataInfo == null || SelectDataInfo.TYPE_TEXT != selectDataInfo.getType()
                || dependentView == null) {
            return;
        }
        if (!(dependentView instanceof TextView)) {
            return;
        }

        int selectStart = selectDataInfo.getStart();
        int selectEnd = selectDataInfo.getEnd();
        TextView mTextView = (TextView) dependentView;

        int[] mTempCoors = new int[2];
        mTextView.getLocationInWindow(mTempCoors);

        //根据当前坐标倒推选中文字索引
        int x = rawX - mTempCoors[0] - mTextView.getPaddingLeft() + (isLeft ? mWidth : 0);
        int y = rawY - mTempCoors[1] - mTextView.getPaddingTop();

        //获取文本控件中最接近坐标的字符的索引位置
        int offset = TextLayoutUtil.getPreciseOffset(mTextView, x, y);
        int oldOffset = isLeft ? selectStart : selectEnd;

        //新旧偏移位不一致：更新信息
        if (offset != oldOffset) {
            if (isLeft) {
                //当前游标为起始游标
                if (offset > selectEnd) {
                    selectDataInfo.setStart(selectEnd);
                    selectDataInfo.setEnd(offset);
                    selectCursorListener.updateSelectInfo();

                    SelectCursorView endCursor = selectCursorListener.getOtherCursorView(this);
                    endCursor.changeDirection();
                    endCursor.show();

                    changeDirection();
                    show();

                } else {
                    if (offset == selectEnd) {
                        offset = selectEnd - 1;
                    }
                    selectDataInfo.setStart(offset);
                    selectCursorListener.updateSelectInfo();

                    show();
                }
            } else {
                if (offset < selectStart) {
                    selectDataInfo.setStart(offset);
                    selectDataInfo.setEnd(selectStart);
                    selectCursorListener.updateSelectInfo();

                    SelectCursorView endCursor = selectCursorListener.getOtherCursorView(this);
                    endCursor.changeDirection();
                    endCursor.show();

                    changeDirection();
                    show();
                } else {
                    if (offset == selectStart) {
                        offset = selectStart + 1;
                    }
                    selectDataInfo.setStart(offset);
                    selectCursorListener.updateSelectInfo();

                    show();
                }
            }
        }
    }

    private void firstShow() {
        try {
            View dependentView = selectPopListener.getDependentView();
            SelectDataInfo selectDataInfo = selectPopListener.getSelectDataInfo();

            if (selectDataInfo == null || SelectDataInfo.TYPE_TEXT != selectDataInfo.getType()
                    || dependentView == null) {
                return;
            }
            if (!(dependentView instanceof TextView)) {
                return;
            }

            int selectStart = selectDataInfo.getStart();
            int selectEnd = selectDataInfo.getEnd();
            TextView mTextView = (TextView) dependentView;

            int[] mTempCoors = new int[2];
            mTextView.getLocationInWindow(mTempCoors);
            Layout layout = mTextView.getLayout();

            int offset = isLeft ? selectStart : selectEnd;

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
    public void show() {
        try {
            View dependentView = selectPopListener.getDependentView();
            SelectDataInfo selectDataInfo = selectPopListener.getSelectDataInfo();

            if (selectDataInfo == null || SelectDataInfo.TYPE_TEXT != selectDataInfo.getType()
                    || dependentView == null) {
                return;
            }
            if (!(dependentView instanceof TextView)) {
                return;
            }

            int selectStart = selectDataInfo.getStart();
            int selectEnd = selectDataInfo.getEnd();
            TextView mTextView = (TextView) dependentView;

            int[] mTempCoors = new int[2];
            mTextView.getLocationInWindow(mTempCoors);
            Layout layout = mTextView.getLayout();

            int offset = isLeft ? selectStart : selectEnd;

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


    public static class Build {
        private Context context;
        private boolean isLeft;
        private int mCursorHandleColor;
        private int mCursorHandleSize;
        private OnSelectCursorListener selectCursorListener;
        private OnSelectPopListener selectPopListener;


        public Build(Context context) {
            this.context = context;
        }

        public Build setmCursorHandleColor(int mCursorHandleColor) {
            this.mCursorHandleColor = mCursorHandleColor;
            return this;
        }

        public Build setLeft(boolean left) {
            isLeft = left;
            return this;
        }

        public Build setmCursorHandleSize(int mCursorHandleSize) {
            this.mCursorHandleSize = mCursorHandleSize;
            return this;
        }

        public Build setSelectCursorListener(OnSelectCursorListener selectCursorListener) {
            this.selectCursorListener = selectCursorListener;
            return this;
        }

        public Build setSelectPopListener(OnSelectPopListener selectPopListener) {
            this.selectPopListener = selectPopListener;
            return this;
        }

        public SelectCursorView build() {
            if (mCursorHandleSize <= 0 || selectCursorListener == null || selectPopListener == null) {
                throw new IllegalArgumentException("参数未设置");
            }
            return new SelectCursorView(this);
        }
    }
}