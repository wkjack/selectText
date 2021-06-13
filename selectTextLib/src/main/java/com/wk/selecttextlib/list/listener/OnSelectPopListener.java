package com.wk.selecttextlib.list.listener;

import android.view.View;

import com.wk.selecttextlib.list.model.SelectDataInfo;

/**
 *
 */
public interface OnSelectPopListener {

    /**
     * 获取依赖的控件
     *
     * @return
     */
    View getDependentView();

    /**
     * 返回
     * @return
     */
    SelectDataInfo getSelectDataInfo();
}