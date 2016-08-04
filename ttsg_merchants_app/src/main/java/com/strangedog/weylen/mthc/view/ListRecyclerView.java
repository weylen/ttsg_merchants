package com.strangedog.weylen.mthc.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.util.DimensUtil;

/**
 * Created by weylen on 2016-07-30.
 */
public class ListRecyclerView extends LRecyclerView{
    private OnRefreshListener onRefreshListener;
    public ListRecyclerView(Context context) {
        super(context);
        init();
    }

    public ListRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void addItemDecoration(ItemDecoration itemDecoration){
        if (itemDecoration == null){
            super.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                    .color(getResources().getColor(R.color.divider_dark)).size(DimensUtil.dp2px(getContext(), 0.5f)).build());
        }else {
            super.addItemDecoration(itemDecoration);
        }
    }

    /**
     * 初始化设置
     */
    private void init(){
        // 设置布局容器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(layoutManager);

        setLScrollListener(new LScrollListener() {
            @Override
            public void onRefresh() {
                if (onRefreshListener != null){
                    onRefreshListener.onRefresh();
                }
            }

            @Override
            public void onScrollUp() {

            }

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onBottom() {
                if (onRefreshListener != null){
                    onRefreshListener.onBottom();
                }
            }

            @Override
            public void onScrolled(int distanceX, int distanceY) {

            }
        });
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public interface OnRefreshListener{
        void onRefresh();
        void onBottom();
    }
}
