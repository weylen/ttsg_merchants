package com.strangedog.weylen.mthc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.strangedog.weylen.mtch.R;

/**
 * Created by weylen on 2016-07-30.
 */
public class ZAdapterWrapper<T extends WrapperAdapterData> extends EndlessRecyclerViewAdapter{

    private boolean isFinishing; // 数据是否加载完毕
    private View loadingView;
    private View progressView;
    private TextView finishView;

    public ZAdapterWrapper(Context context, T adapter, LoadmoreListenerWrapper listener){
        super(context, adapter, listener, R.layout.item_loading, true, false);
        Preconditions.checkNotNull(adapter, "adapter can not be null");
    }

    public void setFinishing(boolean finishing) {
        isFinishing = finishing;
        invalidate();
    }

    private void ensureView(){
        if (loadingView == null){
            loadingView = getLoadingView();
            progressView = loadingView.findViewById(R.id.loading_pro);
            finishView = (TextView) loadingView.findViewById(R.id.loading_text);
        }
    }

    private void invalidate(){
        ensureView();
        if (isFinishing){
            loadingView.setVisibility(View.GONE);
            finishView.setVisibility(View.VISIBLE);
        }else {
            loadingView.setVisibility(View.VISIBLE);
            finishView.setVisibility(View.GONE);
        }
    }
}
