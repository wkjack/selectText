package com.wk.selecttextlib.select;

import com.wk.selecttextlib.SelectTextHelper;

public class SelectManager {

    private SelectHelper lastSelect = null;

    private SelectManager(){
    }


    static SelectManager getInstance(){
        return SelectManager.Inner.INSTANCE;
    }


    private static class Inner {
        private static SelectManager INSTANCE = new SelectManager();
    }

    public SelectHelper getLastSelect() {
        return lastSelect;
    }

    public void setLastSelect(SelectHelper lastSelect) {
        this.lastSelect = lastSelect;
    }
}