package com.strangedog.weylen.mthc.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2016-07-03.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    /**
     * @param space 间距
     */
    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
//        outRect.left = space;
        outRect.bottom = space;
        //由于每行都只有2个，所以第一个都是3的倍数，把左边距设为0
//        if (parent.getChildLayoutPosition(view) % num == 0) {
//            outRect.left = 0;
//        }
    }
}
