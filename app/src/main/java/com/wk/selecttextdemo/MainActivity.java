package com.wk.selecttextdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wk.selecttextlib.OnSelectListener;
import com.wk.selecttextlib.OnSelectOptionListener;
import com.wk.selecttextlib.SelectOption;
import com.wk.selecttextlib.SelectTextHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTvTest;

    private SelectTextHelper mSelectableTextHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        List<SelectOption> optionList = new ArrayList<>();
        optionList.add(new SelectOption(SelectOption.TYPE_COPY, "复制"));
        optionList.add(new SelectOption(SelectOption.TYPE_SELECT_ALL, "全选"));
        optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "分享"));
        optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "搜索"));
        optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "翻译"));
        optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "注释"));

        mSelectableTextHelper = new SelectTextHelper.Builder(mTvTest)
                .setSelectedColor(getResources().getColor(R.color.selected_blue))
                .setCursorHandleSizeInDp(20)
                .setCursorHandleColor(getResources().getColor(R.color.cursor_handle_color))
                .setSelectOptions(optionList)
                .build();

        mSelectableTextHelper.setSelectListener(new OnSelectListener() {
            @Override
            public void onTextSelected(CharSequence content) {
                Log.e("选中数据：", content.toString());
            }
        });
        mSelectableTextHelper.setSelectOptionListener(new OnSelectOptionListener() {
            @Override
            public void onSelectOption(SelectOption option) {
                Log.e("选中数据：", option.toString());
            }
        });

    }

    private void initView() {
        mTvTest = (TextView) findViewById(R.id.tv_test);
        //mTvTest.setTextIsSelectable(true);

        findViewById(R.id.main_jumpToList).setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, ListActivity.class));
        });
    }
}