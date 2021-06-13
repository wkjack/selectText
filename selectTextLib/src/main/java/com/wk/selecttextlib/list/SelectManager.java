package com.wk.selecttextlib.list;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class SelectManager {

    private Map<String, ListSelectTextHelp> cache = new HashMap<>();

    private SelectManager() {
    }


    public static SelectManager getInstance() {
        return SelectManager.Inner.INSTANCE;
    }


    private static class Inner {
        private static SelectManager INSTANCE = new SelectManager();
    }

    public void put(@NonNull String key, @NonNull ListSelectTextHelp value) {
        cache.put(key, value);
    }

    public ListSelectTextHelp get(@NonNull String key) {
        return cache.get(key);
    }

    public void clear(){
        cache.clear();
    }
}