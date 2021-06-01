package com.wk.selecttextdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wk.selecttextlib.OnSelectListener;
import com.wk.selecttextlib.SelectTextHelper;

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

        mSelectableTextHelper.setSelectListener(new OnSelectListener() {
            @Override
            public void onTextSelected(CharSequence content) {
                Log.e("选中数据：", content.toString());
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