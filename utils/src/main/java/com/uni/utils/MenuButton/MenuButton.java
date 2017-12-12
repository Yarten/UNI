package com.uni.utils.MenuButton;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jiang.android.indicatordialog.IndicatorBuilder;
import com.jiang.android.indicatordialog.IndicatorDialog;
import com.uni.utils.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D105-01 on 2017/12/11.
 */

public class MenuButton extends LinearLayout {

    public interface OnItemClickListener{
        void onNextButtonClick();
        void onMenuItemClick(int position);
        void onPrevButtonClick();
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    private Button btn_prev;
    private Button btn_next;
    private Button btn_menu;

    private List<String> mLists = new ArrayList<>();
    private List<Integer> mIcons = new ArrayList<>();

    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public MenuButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        LayoutInflater.from(context).inflate(R.layout.indicaton_view, this);

        mContext = context;

        btn_prev = findViewById(R.id.prev_indication_view);
        btn_next = findViewById(R.id.next_indication_view);
        btn_menu = findViewById(R.id.menu_indication_view);

        btn_menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuItem(v, 0.25f, IndicatorBuilder.GRAVITY_LEFT);
            }
        });

        btn_prev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onNextButtonClick();
            }
        });

        btn_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onPrevButtonClick();
            }
        });
    }

    public void setLists(List<String> lists){
        mLists.clear();
        this.mLists = lists;
    }
    public void setIcons(List<Integer> icons){
        mIcons.clear();
        this.mIcons = icons;
    }

    private void showMenuItem(View v, float v1, int gravityLeft){
//        mLists.clear();
//        mIcons.clear();
//
//        mLists.add("面对面快传");
//        mIcons.add(R.mipmap.back);
//        mLists.add("付款");
//        mIcons.add(R.mipmap.back);
//        mLists.add("拍摄");
//        mIcons.add(R.mipmap.back);
        IndicatorDialog dialog = new IndicatorBuilder((Activity) mContext)
                .width(260)
               // .animator(R.style.dialog_exit)
                .height((int) (30 * 0.4))
                .height(-1)
                .ArrowDirection(IndicatorBuilder.LEFT)
                .bgColor(Color.WHITE)
                .gravity(gravityLeft)
                .ArrowRectage(0.4f)
                .radius(20)
                .layoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false))
                .adapter(new BaseAdapter() {
                    @Override
                    public void onBindView(BaseViewHolder holder, int position) {
                        TextView tv = holder.getView(R.id.item_add);
                        tv.setText(mLists.get(position));
//                        tv.setCompoundDrawablesWithIntrinsicBounds(mIcons.get(position), 0, 0, 0);

                        if (position == mLists.size() - 1) {
                            holder.setVisibility(R.id.item_line, BaseViewHolder.GONE);
                        } else {
                            holder.setVisibility(R.id.item_line, BaseViewHolder.VISIBLE);

                        }
                    }

                    @Override
                    public int getLayoutID(int position) {
                        return R.layout.menu_item_view;
                    }

                    @Override
                    public boolean clickable() {
                        return true;
                    }

                    @Override
                    public void onItemClick(View v, int position) {
                        //Toast.makeText(MainActivity.this, "你点击了:" + position, Toast.LENGTH_SHORT).show();
                        onItemClickListener.onMenuItemClick(position);
                    }

                    @Override
                    public int getItemCount() {
                        return mLists.size();
                    }
                }).create();

        dialog.setCanceledOnTouchOutside(true);
        dialog.show(v);
    }





}
