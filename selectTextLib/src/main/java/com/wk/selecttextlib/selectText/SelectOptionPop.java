package com.wk.selecttextlib.selectText;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wk.selecttextlib.R;
import com.wk.selecttextlib.SelectOption;
import com.wk.selecttextlib.SelectOptionAdapter;
import com.wk.selecttextlib.SelectionInfo;
import com.wk.selecttextlib.TextLayoutUtil;

import java.util.List;

public class SelectOptionPop {
    private PopupWindow mWindow; //弹框
    private int mWidth; //宽
    private int mHeight; //高

    private SelectTextHelper selectTextHelper;

    public SelectOptionPop(final SelectTextHelper selectTextHelper) {
        this.selectTextHelper = selectTextHelper;

        Context context = selectTextHelper.getTextView().getContext();
        List<SelectOption> mSelectOptions = selectTextHelper.getSelectOptionListener()
                .calculateSelectInfo(selectTextHelper.getSelectionInfo(),
                        selectTextHelper.getTextView().getText().toString());


        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_operate_windows, null);
        GridView gridView = contentView.findViewById(R.id.select_option);


        SelectOptionAdapter adapter = new SelectOptionAdapter(context, mSelectOptions);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectOption selectOption = mSelectOptions.get(position);

                selectTextHelper.getSelectOptionListener().onSelectOption(selectTextHelper.getSelectionInfo(), selectOption);
            }
        });

        int optionSize = mSelectOptions.size();

        gridView.setNumColumns(Math.min(optionSize, 5));


        mWidth = contentView.getPaddingLeft() + contentView.getPaddingRight()
                + Math.min(optionSize, 5) * TextLayoutUtil.dp2px(context, 60)
                + (Math.min(optionSize, 5) - 1) * gridView.getHorizontalSpacing();

        int line;
        if (optionSize <= 5) {
            line = 1;
        } else {
            if (optionSize % 5 == 0) {
                line = optionSize / 5;
            } else {
                line = optionSize / 5 + 1;
            }
        }
        mHeight = contentView.getPaddingTop() + contentView.getPaddingBottom()
                + line * TextLayoutUtil.dp2px(context, 40)
                + (line - 1) * gridView.getVerticalSpacing();

        mWindow = new PopupWindow(contentView, mWidth, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mWindow.setClippingEnabled(false); //弹框在超出屏幕时不剪裁，即显示在正确的位置
    }

    public void show(TextView mTextView, SelectionInfo mSelectionInfo, boolean updateLocation) {
        if (mTextView == null || mSelectionInfo == null) {
            return;
        }
        Context mContext = mTextView.getContext();
        if (mContext == null) {
            return;
        }
        int[] mTempCoors = new int[2];
        mTextView.getLocationInWindow(mTempCoors);

        Rect viewRect = new Rect();
        mTextView.getGlobalVisibleRect(viewRect);

        if (mTempCoors[0] != viewRect.left && mTempCoors[1] != viewRect.top) {
            //经过实验，此种情况为控件已无显示区域

            int posX = 0;
            int posY = -mHeight - 16;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //设置高度
                mWindow.setElevation(8f);
            }
            if (updateLocation) {
                mWindow.update(posX, posY, -1, -1);
            } else {
                mWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, posX, posY);
            }
            return;
        }

        int posX;
        if (viewRect.left + mWidth <= viewRect.right) {
            //弹框可现实在控件内部
            posX = (viewRect.left + viewRect.right - mWidth) / 2;

        } else if (viewRect.left + mWidth < TextLayoutUtil.getScreenWidth(mContext)) {
            //弹框超出控件范围，但未超出屏幕
            posX = viewRect.left;
        } else {
            posX = TextLayoutUtil.getScreenWidth(mContext) - mWidth;
        }

        //获取decorView的显示区域
        Rect rectangle = new Rect();
        ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);

        Layout layout = mTextView.getLayout();

        //1. 尝试计算顶部显示
        int topY = layout.getLineTop(layout.getLineForOffset(mSelectionInfo.mStart));
        int realTopY = mTempCoors[1] + mTextView.getPaddingTop() + topY;
        if (realTopY - mHeight - 16 > rectangle.top) {
            //弹框可以显示在getDecorView()区域内

            int posY = realTopY - mHeight - 16;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //设置高度
                mWindow.setElevation(8f);
            }
            if (updateLocation) {
                mWindow.update(posX, posY, -1, -1);
            } else {
                mWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, posX, posY);
            }
            return;
        }

        //2. 顶部不够显示，尝试计算底部显示
        int bottomY = layout.getLineBottom(layout.getLineForOffset(mSelectionInfo.mEnd));
        int realBottomY = mTempCoors[1] + mTextView.getPaddingTop() + bottomY;

        int cursorHeight = selectTextHelper.getmCursorHandleSize();

        if (realBottomY + cursorHeight + mHeight + 16 <= TextLayoutUtil.getScreenHeight(mContext)) {
            //未超出显示区域
            if (realBottomY > 0) {
                //说明当前控件内容完全被遮挡，没有显示内容
                int posY = realBottomY + cursorHeight + 16;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //设置高度
                    mWindow.setElevation(8f);
                }
                if (updateLocation) {
                    mWindow.update(posX, posY, -1, -1);
                } else {
                    mWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, posX, posY);
                }
                return;
            }
        }

        //3. 中间显示
        int posY = (rectangle.top + rectangle.bottom) / 3;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //设置高度
            mWindow.setElevation(8f);
        }
        if (updateLocation) {
            mWindow.update(posX, posY, -1, -1);
        } else {
            mWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, posX, posY);
        }
    }

    public void dismiss() {
        mWindow.dismiss();
    }

    public boolean isShowing() {
        return mWindow.isShowing();
    }
}