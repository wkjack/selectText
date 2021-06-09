package com.wk.selecttextdemo;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wk.selecttextlib.LastSelectListener;
import com.wk.selecttextlib.LastSelectManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ListActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        recyclerView = findViewById(R.id.recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        Random random = new Random();
        int[] ids = new int[]{R.string.long_text1, R.string.long_text2, R.string.long_text3, R.string.long_text4, R.string.long_text5};
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datas.add(getString(ids[Math.abs(random.nextInt(10) % ids.length)]));
        }

        SimpleAdapter adapter = new SimpleAdapter(this, datas);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                LastSelectListener lastSelect = LastSelectManager.getInstance().getLastSelect();
                if (lastSelect != null) {
                    lastSelect.clearOperate();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }
}