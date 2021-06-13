package com.wk.selecttextlib.list;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.wk.selecttextlib.R;
import com.wk.selecttextlib.list.model.SelectDataInfo;
import com.wk.selecttextlib.util.ClickUtil;

public class SelectBind {
    private final int mSelectedColor = 0xFFAFE1F4;

    private int pos;
    private Object data;
    private TextView mTextView;

    private View.OnClickListener originalClickListener;
    private boolean isTriggerLongClick = false; //是否触发长按点击事件
    private boolean isTouchDown = false; //是否触发ACTION_DOWN
    private MotionEvent downEvent; //记录ACTION_DOWN的event


    private Spannable mSpannable; //文本内容
    private BackgroundColorSpan mSpan; //选中背景Span

    public SelectBind(TextView textView, Object data, int pos) {
        this.mTextView = textView;
        this.data = data;
        this.pos = pos;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void bind() {
        mTextView.setText(mTextView.getText(), TextView.BufferType.SPANNABLE);
        originalClickListener = ClickUtil.getViewClickListener(mTextView);

        mTextView.setOnLongClickListener(v -> {

            isTriggerLongClick = true;

            //构建选中信息
            SelectDataInfo selectDataInfo = new SelectDataInfo(data, SelectDataInfo.TYPE_TEXT);
            selectDataInfo.setStart(0);
            selectDataInfo.setEnd(mTextView.getText().length());
            selectDataInfo.setSelectContent(getSelectData(selectDataInfo));

            ListSelectTextHelp selectTextHelp = SelectManager.getInstance().get(mTextView.getContext().toString());
            if (selectTextHelp != null) {
                selectTextHelp.onSelectData(selectDataInfo);
                return true;
            }
            return false;
        });


        mTextView.setOnTouchListener((v, event) -> {
            //记录触摸点坐标
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isTriggerLongClick = false;
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
                    if (isTriggerLongClick) {
                        //已触发点击事件

                        //移出选中数据
                        ListSelectTextHelp selectTextHelp = SelectManager.getInstance().get(mTextView.getContext().toString());
                        if (selectTextHelp != null) {
                            selectTextHelp.onSelectData(null);
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    //应对按下后触发点击事件后继续滑动直到松开，此时显示操作
                    if (isTriggerLongClick) {
                    }
                    break;
            }
            return false;
        });

        mTextView.setOnClickListener(v -> {

            if (isTouchDown) {
                //原有点击处理
                if (originalClickListener != null) {
                    originalClickListener.onClick(v);
                }
            }
        });

        mTextView.setTag(R.id.select_bind, this);
    }

    /**
     * 供手势onDown
     *
     * @param event 事件
     */
    public void onGestureDown(MotionEvent event) {
        Rect viewRect = new Rect();
        mTextView.getGlobalVisibleRect(viewRect);

        if (event.getAction() == MotionEvent.ACTION_DOWN
                && event.getX() >= viewRect.left && event.getX() <= viewRect.right
                && event.getY() >= viewRect.top && event.getY() <= viewRect.bottom) {
            isTouchDown = true;
        } else {
            isTouchDown = false;
        }
        downEvent = null;
    }

    public boolean isTouchDown() {
        return isTouchDown;
    }

    public boolean isTriggerLongClick() {
        return isTriggerLongClick;
    }

    public String getSelectData(SelectDataInfo selectDataInfo) {
        int start = selectDataInfo.getStart();
        int end = selectDataInfo.getEnd();

        if (start < 0 || end < 0) {
            return "";
        }

        CharSequence textContent = mTextView.getText();
        if (end > textContent.length()) {
            return "";
        }

        return textContent.subSequence(start, end).toString();
    }

    /**
     * 更新内容
     */
    public void clear() {
        //清除状态
        if (mSpannable != null && mSpan != null) {
            mSpannable.removeSpan(mSpan);
            mSpan = null;
        }
    }


    public void update() {
        SelectDataInfo selectDataInfo = null;
        ListSelectTextHelp selectTextHelp = SelectManager.getInstance().get(mTextView.getContext().toString());
        if (selectTextHelp != null) {
            selectDataInfo = selectTextHelp.getSelectDataInfo();
        }

        if (selectDataInfo == null || selectDataInfo.getStart() < 0 || selectDataInfo.getEnd() < 0) {
            return;
        }

        int start = selectDataInfo.getStart();
        int end = selectDataInfo.getEnd();

        if (mTextView.getText() instanceof Spannable) {
            mSpannable = (Spannable) mTextView.getText();
        }
        if (mSpannable == null || start > mSpannable.length() || end > mSpannable.length()) {
            return;
        }

        try {
            if (mSpannable != null) {
                if (mSpan == null) {
                    mSpan = new BackgroundColorSpan(mSelectedColor);
                }

                //设置选中样式
                mSpannable.setSpan(mSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TextView getTextView() {
        return mTextView;
    }

    public Object getData() {
        return data;
    }
}