package com.wk.selecttextlib;

public class LastSelectManager {


    private LastSelectListener lastSelectText = null;

    private LastSelectManager() {
    }


    public static LastSelectManager getInstance() {
        return Inner.INSTANCE;
    }


    private static class Inner {
        private static LastSelectManager INSTANCE = new LastSelectManager();
    }

    public LastSelectListener getLastSelect() {
        return lastSelectText;
    }

    public void setLastSelect(LastSelectListener lastSelect) {
        this.lastSelectText = lastSelect;
    }
}