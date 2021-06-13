package com.wk.selecttextlib.list.listener;

import com.wk.selecttextlib.list.SelectCursorView;

public interface OnSelectCursorListener {

    SelectCursorView getOtherCursorView(SelectCursorView cursorView);

    /**
     * 隐藏操作弹框
     */
    void hideOperatePop();

    void showOperatePop();

    void updateSelectInfo();
}