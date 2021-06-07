package com.wk.selecttextdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wk.selecttextlib.selectText.SelectTextHelper;

import java.util.ArrayList;
import java.util.List;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.Holder> {

    private Context context;
    private List<String> datas;

    public SimpleAdapter(Context context, List<String> datas) {
        this.context = context;
        this.datas = datas;
        if (this.datas == null) {
            this.datas = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recy, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleAdapter.Holder holder, int position) {
        holder.content.setText(datas.get(position));
        SelectTextHelper mSelectableTextHelper = new SelectTextHelper.Builder(holder.content)
                .setSelectedColor(context.getResources().getColor(R.color.selected_blue))
                .setCursorHandleSizeInDp(20)
                .setCursorHandleColor(context.getResources().getColor(R.color.cursor_handle_color))
                .build();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        TextView content;

        public Holder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.item_txt);
        }
    }
}