package com.wk.selecttextlib.list.listener;

import com.wk.selecttextlib.list.model.SelectDataInfo;

public interface OnSelectDataListener {

    /**
     * 选中数据
     * @param selectDataInfo 数据信息
     */
    void onSelectData(SelectDataInfo selectDataInfo);
}