package com.wk.selecttextdemo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wk.selecttextdemo.R;
import com.wk.selecttextdemo.model.DataModel;
import com.wk.selecttextlib.SelectOption;
import com.wk.selecttextlib.list.ListSelectTextHelp;
import com.wk.selecttextlib.list.SelectManager;
import com.wk.selecttextlib.list.bind.SelectTextBind;
import com.wk.selecttextlib.list.listener.OnOperateListener;
import com.wk.selecttextlib.list.model.SelectDataInfo;

import java.util.ArrayList;
import java.util.List;

public class SimpleListAdapter extends BaseAdapter {
    private Context context;
    private List<DataModel> datas;

    public SimpleListAdapter(Context context, List<DataModel> datas) {
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

        holder.content.setText(datas.get(position).getData());
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("列表信息", "点击操作===");
            }
        });

        SelectTextBind selectBind = new SelectTextBind(holder.content, datas.get(position), position);
        selectBind.setOperateListener(new OnOperateListener() {
            @Override
            public List<SelectOption> getOperateList() {
                List<SelectOption> optionList = new ArrayList<>();

                optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "搜索"));
                optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "复制"));
                optionList.add(new SelectOption(SelectOption.TYPE_CUSTOM, "全选"));
                return optionList;
            }

            @Override
            public void onOperate(SelectOption operate) {
                ListSelectTextHelp selectTextHelp = SelectManager.getInstance().get(selectBind.getSelectKey());
                if (selectTextHelp != null) {
                    SelectDataInfo selectDataInfo = selectTextHelp.getSelectDataInfo();
                    if("全选".equals(operate.getName())) {
                        selectDataInfo.setStart(0);
                        selectDataInfo.setEnd(selectBind.getTextView().getText().toString().length());
                        selectDataInfo.setSelectContent(selectBind.getSelectData(selectDataInfo));

                        selectTextHelp.onSelectData(selectDataInfo);
                    }
                }


                Log.e("详情", "操作：" + operate.getType() + "/" + operate.getName());
            }
        });
        selectBind.bind();
        return convertView;
    }


    public static class Holder {
        TextView content;
    }
}