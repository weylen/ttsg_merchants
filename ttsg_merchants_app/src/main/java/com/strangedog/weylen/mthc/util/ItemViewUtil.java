package com.strangedog.weylen.mthc.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by weylen on 2016-08-09.
 */
public class ItemViewUtil {

    public static void show(Context context, RecyclerView recyclerView){
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int first = manager.findFirstVisibleItemPosition();
        int last = manager.findLastVisibleItemPosition();
        for (int i = first; i <= last; i++){
            View view = manager.findViewByPosition(i);
            AnimatorUtil.translate(view, DimensUtil.dp2px(context, 0), null);
//            AnimatorUtil.scaleShow(holder.checkBox, null);
        }
    }

    public static void hide(Context context, RecyclerView recyclerView){
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int first = manager.findFirstVisibleItemPosition();
        int last = manager.findLastVisibleItemPosition();
        for (int i = first; i <= last; i++){
            View view = manager.findViewByPosition(i);
            AnimatorUtil.translate(view, DimensUtil.dp2px(context, -40), null);
            DebugUtil.d("ItemViewUtil hide ");
//            AnimatorUtil.scaleShow(holder.checkBox, null);
        }
    }
}
