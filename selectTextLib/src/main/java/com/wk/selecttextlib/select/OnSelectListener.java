package com.wk.selecttextlib.select;

import com.wk.selecttextlib.SelectOption;

import java.util.List;

public interface OnSelectListener {

    List<SelectOption> calculateSelectInfo();


    void onSelectOption(SelectOption selectOption);
}