package com.wk.selecttextlib.list.bind;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.wk.selecttextlib.R;
import com.wk.selecttextlib.list.ListSelectTextHelp;
import com.wk.selecttextlib.list.SelectManager;
import com.wk.selecttextlib.list.model.SelectDataInfo;
import com.wk.selecttextlib.util.ClickUtil;

/**
 * 非文本选择绑定
 */
public class SelectBind extends BaseSelectBind {

    private Object data;
    private View mView;

    private View.OnLongClickListener originalLongClickListener;

    public SelectBind(View view, Object data) {
        this.mView = view;
        this.data = data;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void bind() {
        originalLongClickListener = ClickUtil.getViewLongClickListener(mView);

        mView.setOnLongClickListener(v -> {

            if (originalLongClickListener != null) {
                if (originalLongClickListener.onLongClick(v)) {
                    return true;
                }
            }

            isTriggerLongClick = true;

            //构建选中信息
            SelectDataInfo selectDataInfo = new SelectDataInfo(data, SelectDataInfo.TYPE_OTHER);

            ListSelectTextHelp selectTextHelp = SelectManager.getInstance().get(getSelectKey());
            if (selectTextHelp != null) {
                selectTextHelp.onSelectData(selectDataInfo);
                return true;
            }
            return false;
        });


        mView.setOnTouchListener((v, event) -> {
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

        mView.setTag(R.id.select_bind, this);
    }

    /**
     * 供手势onDown
     *
     * @param event 事件
     */
    public void onGestureDown(MotionEvent event) {
        Rect viewRect = new Rect();
        mView.getGlobalVisibleRect(viewRect);

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
        return super.getSelectData(selectDataInfo);
    }

    public View getView() {
        return mView;
    }

    public Object getData() {
        return data;
    }

    @Override
    public final String getSelectKey() {
        return mView.getContext().toString();
    }
}