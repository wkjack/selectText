package com.wk.selecttextlib;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.ColorInt;

@SuppressLint("ClickableViewAccessibility")
public class SelectTextHelper {

    private final static int DEFAULT_SELECTION_LENGTH = 1;
    private static final int DEFAULT_SHOW_DURATION = 100;

    private CursorHandle mStartHandle; // 选中起始图标
    private CursorHandle mEndHandle; // 选中结束图标
    private OperateWindow mOperateWindow; //操作弹框
    private final SelectionInfo mSelectionInfo = new SelectionInfo(); //选中信息
    private OnSelectListener mSelectListener; //选中回调

    private final Context mContext; //上下文
    private final TextView mTextView; //文本控件
    private final int mSelectedColor; //选中背景
    private final int mCursorHandleColor; //选中图标颜色
    private final int mCursorHandleSize; //选中图标尺寸


    private int mTouchX; //触点坐标X
    private int mTouchY; //触点坐标Y
    private Spannable mSpannable; //文本内容
    private BackgroundColorSpan mSpan; //选中背景Span
    private boolean isHideWhenScroll = false; //滚动式隐藏
    private boolean isHide = true; //选中控件是否隐藏

    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener;
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    private final String TAG = "SelectTag";

    public SelectTextHelper(Builder builder) {
        mTextView = builder.mTextView;
        mContext = mTextView.getContext();
        mSelectedColor = builder.mSelectedColor;
        mCursorHandleColor = builder.mCursorHandleColor;
        mCursorHandleSize = TextLayoutUtil.dp2px(mContext, builder.mCursorHandleSizeInDp);
        init();
    }

    private void init() {
        //设置文本控件可设置样式
        mTextView.setText(mTextView.getText(), TextView.BufferType.SPANNABLE);
        mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e(TAG, "长按：" + mTouchX + ":" + mTouchY);
                //长按显示选中布局
                showSelectView(mTouchX, mTouchY);
                return true;
            }
        });

        mTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //记录触摸点坐标
                mTouchX = (int) event.getX();
                mTouchY = (int) event.getY();

                Log.e(TAG, "触摸：" + mTouchX + ":" + mTouchY);
                return false;
            }
        });

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "点击");
                //重置选中信息，隐藏选中操作
                resetSelectionInfo();
                hideSelectView();

                //文本点击时，清除已记录的缓存
                SelectTextHelper lastSelectText = SelectTextManager.getInstance().getLastSelectText();
                if (lastSelectText != null) {
                    if (!lastSelectText.equals(SelectTextHelper.this)) {
                        lastSelectText.resetSelectionInfo();
                        lastSelectText.hideSelectView();
                    }
                }
                SelectTextManager.getInstance().setLastSelectText(null);
            }
        });
        mTextView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                //控件解除绑定做销毁操作
                Log.e(TAG, "销毁");
                destroy();
            }
        });

        mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //控件绘制前处理：
                if (isHideWhenScroll) {
                    isHideWhenScroll = false;
                    postShowSelectView();
                }
                return true;
            }
        };
        mTextView.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);

        mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                Log.e(TAG, "滚动");
                //滚动监听处理
                isHideWhenScroll = false;
                resetSelectionInfo();
                hideSelectView();

                //销毁当前缓存
                SelectTextHelper lastSelectText = SelectTextManager.getInstance().getLastSelectText();
                if (lastSelectText != null && lastSelectText.equals(SelectTextHelper.this)) {
                    SelectTextManager.getInstance().setLastSelectText(null);
                }
            }
        };
        mTextView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);

        mOperateWindow = new OperateWindow(mContext);
    }

    /**
     * 显示选中操作控件
     */
    private void postShowSelectView() {
        mTextView.removeCallbacks(mShowSelectViewRunnable);
        mTextView.postDelayed(mShowSelectViewRunnable, DEFAULT_SHOW_DURATION);
    }

    private final Runnable mShowSelectViewRunnable = new Runnable() {
        @Override
        public void run() {
            //延迟显示操作框、游标图标
            if (isHide) return;
            if (mOperateWindow != null) {
                mOperateWindow.show();
            }
            if (mStartHandle != null) {
                mStartHandle.show();
            }
            if (mEndHandle != null) {
                mEndHandle.show();
            }
        }
    };

    /**
     * 隐藏选中操作控件
     */
    private void hideSelectView() {
        //隐藏
        isHide = true;
        if (mStartHandle != null) {
            mStartHandle.dismiss();
        }
        if (mEndHandle != null) {
            mEndHandle.dismiss();
        }
        if (mOperateWindow != null) {
            mOperateWindow.dismiss();
        }
    }

    /**
     * 重置选中信息
     */
    private void resetSelectionInfo() {
        //清空选中内容、样式
        mSelectionInfo.mSelectionContent = null;
        if (mSpannable != null && mSpan != null) {
            mSpannable.removeSpan(mSpan);
            mSpan = null;
        }
    }

    /**
     * 显示选中控件
     *
     * @param x 文本控件的内部x坐标点
     * @param y 文本控件的内部y坐标点
     */
    private void showSelectView(int x, int y) {
        hideSelectView();
        resetSelectionInfo();
        isHide = false;
        if (mStartHandle == null) mStartHandle = new CursorHandle(true);
        if (mEndHandle == null) mEndHandle = new CursorHandle(false);

        //获取文本控件中最接近坐标的字符的索引位置，作为选中的起始位置
        int startOffset = TextLayoutUtil.getPreciseOffset(mTextView, x, y);
        //结束位置 = 起始位 + 1
        int endOffset = startOffset + DEFAULT_SELECTION_LENGTH;
        if (mTextView.getText() instanceof Spannable) {
            mSpannable = (Spannable) mTextView.getText();
        }
        //确保文本内容样式可设置且选中索引在文本内容范围内
        if (mSpannable == null || startOffset >= mTextView.getText().length()) {
            return;
        }
        //设置选中内容样式、游标、操作框
        selectText(startOffset, endOffset);

        mStartHandle.show();
        mEndHandle.show();
        mOperateWindow.show();

        //确保只会有一个处于选择复制中
        SelectTextHelper lastSelectText = SelectTextManager.getInstance().getLastSelectText();
        if (lastSelectText != null) {
            if (lastSelectText.equals(this)) {
                return;
            }
            lastSelectText.resetSelectionInfo();
            lastSelectText.hideSelectView();
        }
        SelectTextManager.getInstance().setLastSelectText(this);
    }

    /**
     * 设置选中文本及样式
     *
     * @param startPos 开始位置
     * @param endPos   结束位置
     */
    private void selectText(int startPos, int endPos) {
        //更新选中信息：起始位、结束位
        if (startPos != -1) {
            mSelectionInfo.mStart = startPos;
        }
        if (endPos != -1) {
            mSelectionInfo.mEnd = endPos;
        }
        //校对起始位、结束位
        if (mSelectionInfo.mStart > mSelectionInfo.mEnd) {
            int temp = mSelectionInfo.mStart;
            mSelectionInfo.mStart = mSelectionInfo.mEnd;
            mSelectionInfo.mEnd = temp;
        }

        if (mSpannable != null) {
            if (mSpan == null) {
                mSpan = new BackgroundColorSpan(mSelectedColor);
            }
            //更新选中内容
            mSelectionInfo.mSelectionContent = mSpannable.subSequence(mSelectionInfo.mStart, mSelectionInfo.mEnd).toString();
            //设置选中样式
            mSpannable.setSpan(mSpan, mSelectionInfo.mStart, mSelectionInfo.mEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            //选中回调
            if (mSelectListener != null) {
                mSelectListener.onTextSelected(mSelectionInfo.mSelectionContent);
            }
        }
    }

    /**
     * 设置选中监听器
     *
     * @param selectListener 监听器
     */
    public void setSelectListener(OnSelectListener selectListener) {
        mSelectListener = selectListener;
    }

    /**
     * 销毁
     */
    public void destroy() {
        mTextView.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
        mTextView.getViewTreeObserver().removeOnPreDrawListener(mOnPreDrawListener);
        resetSelectionInfo();
        hideSelectView();
        mStartHandle = null;
        mEndHandle = null;
        mOperateWindow = null;

        //记录的缓存为自身时才清除缓存
        SelectTextHelper lastSelectText = SelectTextManager.getInstance().getLastSelectText();
        if (lastSelectText != null && lastSelectText.equals(SelectTextHelper.this)) {
            SelectTextManager.getInstance().setLastSelectText(null);
        }
    }

    /**
     * 操作弹框：复制、全选
     */
    private class OperateWindow {

        private PopupWindow mWindow; //弹框
        private int[] mTempCoors = new int[2]; //坐标

        private int mWidth; //宽
        private int mHeight; //高

        public OperateWindow(final Context context) {
            View contentView = LayoutInflater.from(context).inflate(R.layout.layout_operate_windows, null);
            contentView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            mWidth = contentView.getMeasuredWidth();
            mHeight = contentView.getMeasuredHeight();
            mWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
            mWindow.setClippingEnabled(false); //弹框在超出屏幕时不剪裁，即显示在正确的位置

            contentView.findViewById(R.id.tv_copy).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //复制到剪切板
                    ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    clip.setPrimaryClip(ClipData.newPlainText(mSelectionInfo.mSelectionContent, mSelectionInfo.mSelectionContent));
                    //选中回调
                    if (mSelectListener != null) {
                        mSelectListener.onTextSelected(mSelectionInfo.mSelectionContent);
                    }
                    SelectTextHelper.this.resetSelectionInfo();
                    SelectTextHelper.this.hideSelectView();

                    //复制后清除缓存
                    SelectTextManager.getInstance().setLastSelectText(null);
                }
            });
            contentView.findViewById(R.id.tv_select_all).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //全选处理
                    hideSelectView();
                    selectText(0, mTextView.getText().length());
                    isHide = false;

                    mStartHandle.show();
                    mEndHandle.show();
                    mOperateWindow.show();
                }
            });
        }

        public void show() {
            //获取文本控件在当前窗口中的位置
            mTextView.getLocationInWindow(mTempCoors);
            Layout layout = mTextView.getLayout();
            //坐标X = 获取该字符左边的x坐标 + 控件所在x坐标
            int posX = mTempCoors[0] + (int) layout.getPrimaryHorizontal(mSelectionInfo.mStart);
            if (posX <= 0) {
                posX = 16;
            }

            int posY = mTempCoors[1] + layout.getLineTop(layout.getLineForOffset(mSelectionInfo.mStart)) - mHeight - 16;

            Rect visibleRect = new Rect();
            mTextView.getGlobalVisibleRect(visibleRect);
            if (visibleRect.top != mTempCoors[1]) {
                //控件部分内容被遮挡
                if (posY < (visibleRect.top - mHeight - 16)) {
                    //计算显示的Y坐标：使用结束位置计算
                    posY = mTempCoors[1] + layout.getLineBottom(layout.getLineForOffset(mSelectionInfo.mEnd)) + 16;

                    if (posY < visibleRect.top) {
                        //超出顶部区域
                        posY = visibleRect.top;
                    } else if (posY + mHeight + 16 > visibleRect.bottom) {
                        posY = visibleRect.top - mHeight - 16;
                    }
                } else {
                    //啥也不做
                }
            } else {
                //控件内容未被遮挡
                if (posY <= 0) {
                    posY = mTempCoors[1] + layout.getLineBottom(layout.getLineForOffset(mSelectionInfo.mEnd)) + 16;
                }
            }

            //如果超过屏幕宽度
            if (posX + mWidth > TextLayoutUtil.getScreenWidth(mContext)) {
                posX = TextLayoutUtil.getScreenWidth(mContext) - mWidth - 16;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //设置高度
                mWindow.setElevation(8f);
            }
            mWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, posX, posY);
        }

        public void dismiss() {
            mWindow.dismiss();
        }

        public boolean isShowing() {
            return mWindow.isShowing();
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

        private int mBeforeDragStart; //开始拖拽时的选中起始坐标
        private int mBeforeDragEnd; //开始拖拽时的选中结束坐标

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //记录当前点、选中文本的坐标
                    mBeforeDragStart = mSelectionInfo.mStart;
                    mBeforeDragEnd = mSelectionInfo.mEnd;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    //触摸结束处理：显示操作框
                    mOperateWindow.show();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //触摸移动：隐藏操作框，记录当前点并更新
                    mOperateWindow.dismiss();
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
                resetSelectionInfo();
                if (isLeft) {
                    //当前游标为起始游标
                    if (offset > mBeforeDragEnd) {

                        //结束游标变更为起始游标并更新位置
                        CursorHandle handle = getCursorHandle(false);
                        handle.changeDirection();
                        handle.show(true);

                        //当前游标变更为结束游标
                        changeDirection();
                        mBeforeDragStart = mBeforeDragEnd;
                        selectText(mBeforeDragEnd, offset);
                    } else {
                        selectText(offset, -1);
                    }
                    //更新游标位置
                    show(true);
                } else {
                    if (offset < mBeforeDragStart) {
                        CursorHandle handle = getCursorHandle(true);
                        handle.changeDirection();
                        changeDirection();
                        mBeforeDragEnd = mBeforeDragStart;
                        selectText(offset, mBeforeDragStart);
                        handle.show(true);
                    } else {
                        selectText(mBeforeDragStart, offset);
                    }
                    show(true);
                }
            }
        }

        public void show() {
            show(false);
        }

        /**
         * 统一计算显示位置并显示游标
         *
         * @param updateLocation true(更新位置) / false(显示)
         */
        private void show(boolean updateLocation) {
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

            if (updateLocation) {
                mPopupWindow.update(realX, realY, -1, -1);
            } else {

                Log.e(TAG, "游标显示位置：" + realX + "/" + realY);
                mPopupWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, realX, realY);
            }
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