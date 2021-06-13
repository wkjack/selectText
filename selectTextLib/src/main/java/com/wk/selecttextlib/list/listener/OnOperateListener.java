package com.wk.selecttextlib.list.listener;

import com.wk.selecttextlib.SelectOption;

import java.util.List;

public interface OnOperateListener {

    /**
     * 返回操作列表
     *
     * @return
     */
    List<SelectOption> getOperateList();

    /**
     * 执行操作
     *
     * @param operate 操作
     */
    void onOperate(SelectOption operate);
}