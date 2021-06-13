package com.wk.selecttextdemo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wk.selecttextlib.SelectOption;
import com.wk.selecttextlib.SelectionInfo;
import com.wk.selecttextlib.select.OnSelectListener;
import com.wk.selecttextlib.select.SelectHelper;
import com.wk.selecttextlib.selectText.DefOnSelectOptionListener;
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
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("列表信息", "点击操作===");
            }
        });

        SelectTextHelper selectTextHelper = new SelectTextHelper.Builder(holder.content)
                .setSelectedColor(context.getResources().getColor(R.color.selected_blue))
                .setCursorHandleSizeInDp(20)
                .setCursorHandleColor(context.getResources().getColor(R.color.cursor_handle_color))
                .build();
        selectTextHelper.setSelectOptionListener(new DefOnSelectOptionListener(selectTextHelper) {
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
                    selectTextHelper.clearOperate();
                }
            }
        });

//        SelectHelper selectHelper = new SelectHelper.Builder(holder.content)
//                .build();
//        selectHelper.setSelectListener(new OnSelectListener() {
//            @Override
//            public List<SelectOption> calculateSelectInfo() {
//                List<SelectOption> list = new ArrayList<>();
//                list.add(new SelectOption(SelectOption.TYPE_CUSTOM, "分享"));
//                list.add(new SelectOption(SelectOption.TYPE_CUSTOM, "搜索"));
//                return list;
//            }
//
//            @Override
//            public void onSelectOption(SelectOption selectOption) {
//
//            }
//        });
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