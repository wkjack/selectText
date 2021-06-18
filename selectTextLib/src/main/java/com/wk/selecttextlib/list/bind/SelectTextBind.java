package com.wk.selecttextlib.list.bind;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.wk.selecttextlib.R;
import com.wk.selecttextlib.list.ListSelectTextHelp;
import com.wk.selecttextlib.list.SelectManager;
import com.wk.selecttextlib.list.model.SelectDataInfo;
import com.wk.selecttextlib.util.ClickUtil;

/**
 * 文本选择绑定
 */
public class SelectTextBind extends BaseSelectBind {
    private final int mSelectedColor = 0xFFAFE1F4;

    private Object data;
    private TextView mTextView;

    private View.OnLongClickListener originalLongClickListener;
    private Spannable mSpannable; //文本内容
    private BackgroundColorSpan mSpan; //选中背景Span

    public SelectTextBind(TextView textView, Object data) {
        this.mTextView = textView;
        this.data = data;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void bind() {
        mTextView.setText(mTextView.getText(), TextView.BufferType.SPANNABLE);
        originalLongClickListener = ClickUtil.getViewLongClickListener(mTextView);

        mTextView.setOnLongClickListener(v -> {

            isTriggerLongClick = true;

            //构建选中信息
            SelectDataInfo selectDataInfo = new SelectDataInfo(data, SelectDataInfo.TYPE_TEXT);
            selectDataInfo.setStart(0);
            selectDataInfo.setEnd(mTextView.getText().length());
            selectDataInfo.setSelectContent(getSelectData(selectDataInfo));

            ListSelectTextHelp selectTextHelp = SelectManager.getInstance().get(getSelectKey());
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
//                    if (isTriggerLongClick) {
//                        //已触发点击事件
//                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                    //应对按下后触发点击事件后继续滑动,松开时已划出控件区域
//                    if (isTriggerLongClick) {
//                        //已触发点击事件
//
//                        //移出选中数据
//                        ListSelectTextHelp selectTextHelp = SelectManager.getInstance().get(getSelectKey());
//                        if (selectTextHelp != null) {
//                            selectTextHelp.onSelectData(null);
//                        }
//                    }
                    break;

                case MotionEvent.ACTION_UP:
                    //应对按下后触发点击事件后继续滑动直到松开，此时显示操作
//                    if (isTriggerLongClick) {
//                    }
                    break;
            }
            return false;
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

        isTouchDown = event.getAction() == MotionEvent.ACTION_DOWN
                && event.getX() >= viewRect.left && event.getX() <= viewRect.right
                && event.getY() >= viewRect.top && event.getY() <= viewRect.bottom;
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
        ListSelectTextHelp selectTextHelp = SelectManager.getInstance().get(getSelectKey());
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

    @Override
    public final String getSelectKey() {
        return mTextView.getContext().toString();
    }
}