package com.wk.selecttextdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.wk.selecttextdemo.adapter.SimpleListAdapter;
import com.wk.selecttextdemo.model.DataModel;
import com.wk.selecttextlib.list.ListSelectTextHelp;
import com.wk.selecttextlib.list.bind.SelectTextBind;
import com.wk.selecttextlib.list.SelectManager;
import com.wk.selecttextlib.list.listener.OnFindViewListener;
import com.wk.selecttextlib.list.model.SelectDataInfo;
import com.wk.selecttextlib.selectText.SelectTextHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ListActivity extends AppCompatActivity implements OnFindViewListener {

    private ListView listView;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = findViewById(R.id.listview);


        Random random = new Random();
        int[] ids = new int[]{R.string.long_text1, R.string.long_text2,
                R.string.long_text3, R.string.long_text4,
                R.string.long_text5, R.string.long_text6, R.string.long_text7};
        List<DataModel> datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datas.add(new DataModel(getString(ids[Math.abs(random.nextInt(10) % ids.length)])));
        }

        SimpleListAdapter adapter = new SimpleListAdapter(this, datas);
        listView.setAdapter(adapter);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //轻按选中会执行onLongClick和此方法，重按选中执行onLongClick
                //点击事件执行此方法

                if (selectTextHelp != null) {
                    SelectTextBind selectBind = selectTextHelp.getSelectBind();
                    if (selectBind != null) {
                        Log.e("详情", "onSingleTapUp-->" + selectBind);
                        if (selectBind.isTouchDown()) {
                            //说明点击到控件
                            if (selectBind.isTriggerLongClick()) {
                                //排除轻按选中数据
                                return true;
                            }
                        }
                    }
                    selectTextHelp.onSelectData(null);
                }

//                LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
//                if (lastSelect != null) {
//                    lastSelect.clearOperate();
//                    LastSelectManager.getInstance().setLastSelect(null);
//                    return true;
//                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.e("详情", "onScroll--");
//                LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
//                if (lastSelect != null) {
//                    if (lastSelect.isOnTouchDown()) {
//                        lastSelect.onScroll();
//                    } else {
//                        lastSelect.onScrollFromOther();
//                        LastSelectManager.getInstance().setLastSelect(null);
//                    }
//                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.e("详情", "onFling");
//                LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
//                if (lastSelect != null) {
//                    if (lastSelect.isOnTouchDown()) {
//                        lastSelect.onFling();
//                    } else {
//                        lastSelect.onScrollFromOther();
//                        LastSelectManager.getInstance().setLastSelect(null);
//                    }
//                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                Log.e("详情", "onDown");
//                LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
//                if (lastSelect != null) {
//                    lastSelect.onTouchDownOutside(e);
//                }

                if (selectTextHelp != null) {
                    SelectTextBind selectBind = selectTextHelp.getSelectBind();
                    if (selectBind != null) {
                        selectBind.onGestureDown(e);
                    }
                }
                return super.onDown(e);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("详情", "滚动监听--01-->" + scrollState);
                if (selectTextHelp != null) {
                    SelectTextBind selectBind = selectTextHelp.getSelectBind();
                    if (selectBind != null) {
                        if (!selectBind.isTouchDown()) {
                            selectTextHelp.onSelectData(null);
                            return;
                        }

                        if (scrollState == SCROLL_STATE_TOUCH_SCROLL || scrollState == SCROLL_STATE_FLING) {
                            selectTextHelp.onHideSelect();
                        } else {
                            refreshPopupLocation();
                        }

                        Log.e("详情", "滚动监听--02-->" + selectBind.isTouchDown());
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                Log.e("详情", "滚动监听--》scroll");
            }
        });

        ListSelectTextHelp selectTextHelp = new ListSelectTextHelp(this, this);
        SelectManager.getInstance().put(this.toString(), selectTextHelp);
        this.selectTextHelp = selectTextHelp;
    }

    private ListSelectTextHelp selectTextHelp;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public View getViewByData(SelectDataInfo selectDataInfo) {
        if (listView == null) {
            return null;
        }

        Adapter adapter = listView.getAdapter();
        if (adapter == null || adapter.getCount() == 0) {
            return null;
        }

        int firstVisiblePos = listView.getFirstVisiblePosition();
        int lastVisiblePos = listView.getLastVisiblePosition();

        int selectPos = -1;
        for (int i = firstVisiblePos; i < lastVisiblePos; i++) {
            Object itemData = adapter.getItem(i);
            if (selectDataInfo.getObject().equals(itemData)) {
                selectPos = i - firstVisiblePos;
                break;
            }
        }

        if (selectPos == -1) {
            //表明控件已在显示区域外
            return null;
        }

        View childView = listView.getChildAt(selectPos);
        return childView;
    }

    protected void refreshPopupLocation() {
        getWindow().getDecorView().removeCallbacks(runnable);
        getWindow().getDecorView().postDelayed(runnable, 120);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (selectTextHelp != null) {
                SelectDataInfo selectDataInfo = selectTextHelp.getSelectDataInfo();
                if(selectDataInfo == null) {
                    return;
                }

                View view = selectTextHelp.getDependentView();
                if (view == null) {
                    selectTextHelp.updateSelectInfo();
                    return;
                }

                SelectTextBind selectBind = (SelectTextBind) view.getTag(R.id.select_bind);
                selectTextHelp.setSelectBind(selectBind);
                selectTextHelp.onShowSelect();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (selectTextHelp != null) {
            selectTextHelp.destory();
            selectTextHelp = null;
        }
        SelectManager.getInstance().clear();
    }
}