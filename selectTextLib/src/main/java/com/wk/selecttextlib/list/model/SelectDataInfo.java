package com.wk.selecttextlib.list.model;

public class SelectDataInfo {

    public static final int TYPE_TEXT = 1;
    public static final int TYPE_OTHER = 0;

    private Object object;
    private int type = TYPE_OTHER;
    private String selectContent;
    private int start = -1;
    private int end = -1;

    public SelectDataInfo(Object object) {
        this(object, TYPE_OTHER);
    }

    public SelectDataInfo(Object object, int type) {
        this.object = object;
        this.type = type;
    }

    public SelectDataInfo(Object object, int start, int end) {
        this.object = object;
        this.start = start;
        this.end = end;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSelectContent() {
        return selectContent;
    }

    public void setSelectContent(String selectContent) {
        this.selectContent = selectContent;
    }
}