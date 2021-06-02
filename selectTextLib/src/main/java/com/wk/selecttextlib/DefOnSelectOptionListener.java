package com.wk.selecttextlib;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DefOnSelectOptionListener implements OnSelectOptionListener {

    private SelectTextHelper selectTextHelper;

    public DefOnSelectOptionListener(@NonNull SelectTextHelper selectTextHelper) {
        this.selectTextHelper = selectTextHelper;
    }

    @Override
    public List<SelectOption> calculateSelectInfo(SelectionInfo selectionInfo, String textContent) {
        List<SelectOption> selectOptions = new ArrayList<>();
        selectOptions.add(new SelectOption(SelectOption.TYPE_COPY, "复制"));

        if (selectionInfo.mStart != 0 || selectionInfo.mEnd != textContent.length() - 1) {
            selectOptions.add(new SelectOption(SelectOption.TYPE_SELECT_ALL, "全选"));
        }
        return selectOptions;
    }

    @Override
    public void onSelectOption(@NonNull SelectionInfo selectionInfo, SelectOption option) {
        int type = option.getType();
        if (type == SelectOption.TYPE_COPY) {
            //复制到剪切板
            ClipboardManager clip = (ClipboardManager) selectTextHelper.getTextView().getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clip.setPrimaryClip(ClipData.newPlainText(selectionInfo.mSelectionContent, selectionInfo.mSelectionContent));

            selectTextHelper.clearSelectInfo();
            selectTextHelper.hideOperatePopup();
            return;
        }

        if (type == SelectOption.TYPE_SELECT_ALL) {
            selectTextHelper.selectInfo(0, selectTextHelper.getTextView().getText().length());
            selectTextHelper.showOperatePopup();
        }
    }
}
