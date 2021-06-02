package com.wk.selecttextlib;

/**
 * 管理记录上一次操作
 */
public class SelectTextManager {

    private SelectTextHelper lastSelectText = null;

    private SelectTextManager(){
    }


    static SelectTextManager getInstance(){
        return Inner.INSTANCE;
    }


    private static class Inner {
        private static SelectTextManager INSTANCE = new SelectTextManager();
    }

    public SelectTextHelper getLastSelectText() {
        return lastSelectText;
    }

    public void setLastSelectText(SelectTextHelper lastSelectText) {
        this.lastSelectText = lastSelectText;
    }
}