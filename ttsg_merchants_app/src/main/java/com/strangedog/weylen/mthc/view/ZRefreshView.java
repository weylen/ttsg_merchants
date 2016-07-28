package com.strangedog.weylen.mthc.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.strangedog.weylen.mtch.R;

/**
 * Created by weylen on 2016-07-08.
 */
public class ZRefreshView extends SwipeRefreshLayout {

    public ZRefreshView(Context context) {
        super(context);
        init();
    }

    public ZRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setColorSchemeResources(R.color.themeColor, R.color.themeColorDark, R.color.colorAccent, R.color.colorPrimaryHalf);
    }
}
