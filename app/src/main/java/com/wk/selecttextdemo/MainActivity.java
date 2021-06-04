package com.wk.selecttextdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.wk.selecttextlib.DefOnSelectOptionListener;
import com.wk.selecttextlib.SelectOption;
import com.wk.selecttextlib.SelectTextHelper;
import com.wk.selecttextlib.SelectionInfo;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTvTest;

    private SelectTextHelper mSelectableTextHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        mSelectableTextHelper = new SelectTextHelper.Builder(mTvTest)
                .setSelectedColor(getResources().getColor(R.color.selected_blue))
                .setCursorHandleSizeInDp(20)
                .setCursorHandleColor(getResources().getColor(R.color.cursor_handle_color))
                .build();

        mSelectableTextHelper.setSelectOptionListener(new DefOnSelectOptionListener(mSelectableTextHelper) {
            @Override
            public List<SelectOption> calculateSelectInfo(@NonNull SelectionInfo selectionInfo, String textContent) {
                List<SelectOption> optionList = super.calculateSelectInfo(selectionInfo, textContent);

                optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "分享"));
                optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "搜索"));
                optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "翻译"));
                optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "注释"));
                return optionList;
            }

            @Override
            public void onSelectOption(@NonNull SelectionInfo selectionInfo, SelectOption option) {
                super.onSelectOption(selectionInfo, option);
                if (option.getType() == SelectOption.TYPE_CUSTOM) {
                    Log.e("自定义选项：", option.toString());
                    selectTextHelper.clearSelectInfo();
                    selectTextHelper.hideOperatePopup();
                }
            }
        });

    }

    private void initView() {
        mTvTest = (TextView) findViewById(R.id.tv_test);
        //mTvTest.setTextIsSelectable(true);

        findViewById(R.id.main_jumpToList).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ListActivity.class));
        });
    }
}