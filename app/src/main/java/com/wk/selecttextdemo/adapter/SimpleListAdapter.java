package com.wk.selecttextdemo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wk.selecttextdemo.R;
import com.wk.selecttextlib.list.SelectBind;

import java.util.ArrayList;
import java.util.List;

public class SimpleListAdapter extends BaseAdapter {
    private Context context;
    private List<String> datas;

    public SimpleListAdapter(Context context, List<String> datas) {
        this.context = context;
        this.datas = datas;
        if (this.datas == null) {
            this.datas = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recy, null);

            holder = new Holder();
            holder.content = convertView.findViewById(R.id.item_txt);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.content.setText(datas.get(position));
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("列表信息", "点击操作===");
            }
        });

        
        SelectBind selectBind = new SelectBind(holder.content, datas.get(position), position);
        selectBind.bind();
        return convertView;
    }


    public static class Holder {
        TextView content;
    }
}