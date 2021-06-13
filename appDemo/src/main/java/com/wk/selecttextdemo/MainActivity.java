package com.wk.selecttextdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView mTvTest;

    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

//        SelectTextHelper mSelectableTextHelper = new SelectTextHelper.Builder(mTvTest)
//                .setSelectedColor(getResources().getColor(R.color.selected_blue))
//                .setCursorHandleSizeInDp(20)
//                .setCursorHandleColor(getResources().getColor(R.color.cursor_handle_color))
//                .build();
//
//        mSelectableTextHelper.setSelectOptionListener(new DefOnSelectOptionListener(mSelectableTextHelper) {
//            @Override
//            public List<SelectOption> calculateSelectInfo(@NonNull SelectionInfo selectionInfo, String textContent) {
//                List<SelectOption> optionList = super.calculateSelectInfo(selectionInfo, textContent);
//
//                optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "分享"));
//                optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "搜索"));
//                optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "翻译"));
//                optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "注释"));
//                return optionList;
//            }
//
//            @Override
//            public void onSelectOption(@NonNull SelectionInfo selectionInfo, SelectOption option) {
//                super.onSelectOption(selectionInfo, option);
//                if (option.getType() == SelectOption.TYPE_CUSTOM) {
//                    Log.e("自定义选项：", option.toString());
//                    selectTextHelper.clearOperate();
//                }
//            }
//        });
//
//        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
//                if (lastSelect != null) {
//                    lastSelect.clearOperate();
//                    LastSelectManager.getInstance().setLastSelect(null);
//                    return true;
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
//                if (lastSelect != null) {
//                    if (lastSelect.isOnTouchDown()) {
//                        lastSelect.onScroll();
//                    } else {
//                        lastSelect.onScrollFromOther();
//                        LastSelectManager.getInstance().setLastSelect(null);
//                    }
//                }
//                return super.onScroll(e1, e2, distanceX, distanceY);
//            }
//
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
//                if (lastSelect != null) {
//                    if (lastSelect.isOnTouchDown()) {
//                        lastSelect.onFling();
//                    } else {
//                        lastSelect.onScrollFromOther();
//                        LastSelectManager.getInstance().setLastSelect(null);
//                    }
//                }
//
//                return super.onFling(e1, e2, velocityX, velocityY);
//            }
//
//            @Override
//            public boolean onDown(MotionEvent e) {
//                LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
//                if (lastSelect != null) {
//                    lastSelect.onTouchDownOutside(e);
//                }
//                return super.onDown(e);
//            }
//        });
    }

    private void initView() {
        mTvTest = (TextView) findViewById(R.id.tv_test);
        mTvTest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.e("主页面", "文本点击");
            }
        });

        findViewById(R.id.main_jumpToList).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ListActivity.class));
        });
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return gestureDetector.onTouchEvent(event);
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        super.dispatchTouchEvent(event);
//        return gestureDetector.onTouchEvent(event);
//    }
}