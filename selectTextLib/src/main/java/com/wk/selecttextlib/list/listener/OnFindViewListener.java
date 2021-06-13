package com.wk.selecttextlib.list.listener;

import android.view.View;

import com.wk.selecttextlib.list.model.SelectDataInfo;

public interface OnFindViewListener {

    /**
     * 根据数据查找获取对应控件
     * @param selectDataInfo
     * @return
     */
    View getViewByData(SelectDataInfo selectDataInfo);
}