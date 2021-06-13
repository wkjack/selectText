package com.wk.selecttextlib.list;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.wk.selecttextlib.R;
import com.wk.selecttextlib.SelectOption;
import com.wk.selecttextlib.list.bind.BaseSelectBind;
import com.wk.selecttextlib.list.listener.OnFindViewListener;
import com.wk.selecttextlib.list.listener.OnOperateListener;
import com.wk.selecttextlib.list.listener.OnSelectCursorListener;
import com.wk.selecttextlib.list.listener.OnSelectDataListener;
import com.wk.selecttextlib.list.listener.OnSelectPopListener;
import com.wk.selecttextlib.list.model.SelectDataInfo;

import java.util.ArrayList;
import java.util.List;

public class ListSelectTextHelp implements OnOperateListener, OnSelectPopListener, OnSelectCursorListener, OnSelectDataListener {

    private final int CURSOR_SIZE = 50;


    private Context context;
    private SelectCursorView startCursorView; //文本选择的起始游标
    private SelectCursorView endCursorView; //文本选择的结束游标
    private SelectPupop selectPupop; //操作弹框

    private OnFindViewListener findViewListener;

    private SelectDataInfo selectDataInfo; //选中数据信息


    public ListSelectTextHelp(@NonNull Context context, @NonNull OnFindViewListener findViewListener) {
        this.context = context;
        this.findViewListener = findViewListener;
    }

    @Override
    public List<SelectOption> getOperateList() {
        if (selectDataInfo == null) {
            return new ArrayList<>();
        }

        List<SelectOption> optionList = new ArrayList<>();
        if (SelectDataInfo.TYPE_TEXT == selectDataInfo.getType()) {
            //文本选择
            optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "复制"));
            optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "全选"));
            optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "分享"));
            optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "搜索"));
            optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "翻译"));
            optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "注释"));
            return optionList;
        }

        //非文本选择
        optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "复制"));
        optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "全选"));
        optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "分享"));
        return optionList;
    }

    @Override
    public void onOperate(SelectOption operate) {
        //操作

    }

    @Override
    public View getDependentView() {
        return getDependentView(selectDataInfo);
    }

    private View getDependentView(SelectDataInfo selectInfo) {
        View view = selectInfo != null ? findViewListener.getViewByData(selectInfo) : null;
        view = getTagView(view);
        return view;
    }

    @Override
    public SelectDataInfo getSelectDataInfo() {
        return selectDataInfo;
    }


    @Override
    public SelectCursorView getOtherCursorView(SelectCursorView cursorView) {
        if (cursorView.equals(startCursorView)) {
            return endCursorView;
        }
        return startCursorView;
    }

    @Override
    public void hideOperatePop() {
        hideSelectPupop();
    }

    @Override
    public void showOperatePop() {
        //显示内容
        if (SelectDataInfo.TYPE_TEXT == selectDataInfo.getType()) {
            selectBind.update();
        }

        hideSelectPupop();
        selectPupop = new SelectPupop.Builder(context)
                .setCursorHandleSize(CURSOR_SIZE)
                .setSelectPopListener(this)
                .setOperateListener(this)
                .build();
        selectPupop.show();
    }

    @Override
    public void updateSelectInfo() {
        if (selectDataInfo == null || SelectDataInfo.TYPE_TEXT != selectDataInfo.getType() || selectBind == null) {
            return;
        }
        selectDataInfo.setSelectContent(selectBind.getSelectData(selectDataInfo));
    }

    @Override
    public void onSelectData(SelectDataInfo newSelectDataInfo) {
        if (this.selectDataInfo != null) {
            if (newSelectDataInfo != null) {
                if (selectDataInfo.getObject().equals(newSelectDataInfo.getObject())
                        && selectDataInfo.getType() == newSelectDataInfo.getType()) {
                    //表明选择的是同一条数据
                    View view = getDependentView(newSelectDataInfo);
                    if (view == null) {
                        onHideSelect();
                        this.selectDataInfo = null;
                        this.selectBind = null;
                        return;
                    }

                    this.selectDataInfo = newSelectDataInfo;
                    this.selectBind = (BaseSelectBind) view.getTag(R.id.select_bind);
                    onShowSelect();
                    return;
                }
            }

            //清除之前的操作
            onHideSelect();
            this.selectDataInfo = null;
            this.selectBind = null;
        }

        if (newSelectDataInfo != null) {
            //选择新数据
            //TODO 更新新的操作
            View view = getDependentView(newSelectDataInfo);
            if (view == null) {
                onHideSelect();
                this.selectDataInfo = null;
                this.selectBind = null;
                return;
            }

            this.selectDataInfo = newSelectDataInfo;
            this.selectBind = (BaseSelectBind) view.getTag(R.id.select_bind);
            onShowSelect();
        }
    }


    public final void onHideSelect() {
        if (SelectDataInfo.TYPE_TEXT == selectDataInfo.getType()) {
            selectBind.clear();
        }

        hideSelectPupop();
        hideStartCursor();
        hideEndCursor();
    }

    public final void onShowSelect() {
        //显示内容
        if (SelectDataInfo.TYPE_TEXT == selectDataInfo.getType()) {
            selectBind.update();
        }


        //操作弹框
        hideSelectPupop();
        selectPupop = new SelectPupop.Builder(context)
                .setCursorHandleSize(CURSOR_SIZE)
                .setSelectPopListener(this)
                .setOperateListener(this)
                .build();
        selectPupop.show();

        //起始游标
        hideStartCursor();
        startCursorView = new SelectCursorView.Build(context)
                .setLeft(true)
                .setmCursorHandleSize(CURSOR_SIZE)
                .setmCursorHandleColor(Color.parseColor("#4680ff"))
                .setSelectPopListener(this)
                .setSelectCursorListener(this)
                .build();
        startCursorView.show();

        //结束游标
        hideEndCursor();
        endCursorView = new SelectCursorView.Build(context)
                .setLeft(false)
                .setmCursorHandleSize(CURSOR_SIZE)
                .setmCursorHandleColor(Color.parseColor("#4680ff"))
                .setSelectPopListener(this)
                .setSelectCursorListener(this)
                .build();
        endCursorView.show();
    }

    private View getTagView(View view) {
        if (view != null) {
            if (view.getTag(R.id.select_bind) != null) {
                return view;
            }

            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int childCount = viewGroup.getChildCount();

                View v;
                for (int i = 0; i < childCount; i++) {
                    v = getTagView(viewGroup.getChildAt(i));
                    if (v != null) {
                        return v;
                    }
                }
            }
        }
        return null;
    }

    private void hideSelectPupop() {
        if (selectPupop != null) {
            if (selectPupop.isShowing()) {
                selectPupop.dismiss();
            }
            selectPupop = null;
        }
    }

    private void hideStartCursor() {
        if (startCursorView != null) {
            if (startCursorView.isShowing()) {
                startCursorView.dismiss();
            }
            startCursorView = null;
        }
    }

    private void hideEndCursor() {
        if (endCursorView != null) {
            if (endCursorView.isShowing()) {
                endCursorView.dismiss();
            }
            endCursorView = null;
        }
    }


    private BaseSelectBind selectBind;

    public void setSelectBind(BaseSelectBind selectBind) {
        this.selectBind = selectBind;
    }

    public BaseSelectBind getSelectBind() {
        return selectBind;
    }


    public void destory() {
        selectBind = null;
        findViewListener = null;
    }
}