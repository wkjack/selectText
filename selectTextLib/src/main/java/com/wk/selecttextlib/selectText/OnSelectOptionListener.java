package com.wk.selecttextlib.selectText;

import androidx.annotation.NonNull;

import com.wk.selecttextlib.SelectOption;
import com.wk.selecttextlib.SelectionInfo;

import java.util.List;

public interface OnSelectOptionListener {

    /**
     * 根据选中信息获取操作集合
     *
     * @param selectionInfo 选中信息
     * @return 操作集合
     */
    List<SelectOption> calculateSelectInfo(@NonNull SelectionInfo selectionInfo, String textContent);

    void onSelectOption(@NonNull SelectionInfo selectionInfo, SelectOption option);
}