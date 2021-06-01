package com.wk.selecttextlib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SelectOptionAdapter extends BaseAdapter {

    private Context context;
    private List<SelectOption> options;

    public SelectOptionAdapter(Context context, List<SelectOption> options) {
        this.context = context;
        this.options = options;
    }

    @Override
    public int getCount() {
        return options != null ? options.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return options != null ? options.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_select_option, parent, false);

            holder = new Holder();
            holder.nameTv = convertView.findViewById(R.id.itemSelectOption_name);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.nameTv.setText(options.get(position).getName());

        return convertView;
    }

    public static class Holder {
        public TextView nameTv;
    }
}