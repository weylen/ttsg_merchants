package com.strangedog.weylen.mthc.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.rey.material.widget.Slider;
import com.rey.material.widget.Switch;

/**
 * Created by Administrator on 2016-07-05.
 */
public class ZViewPager extends ViewPager {

    public ZViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZViewPager(Context context) {
        super(context);
    }

    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        return super.canScroll(v, checkV, dx, x, y) || (checkV && customCanScroll(v));
    }

    protected boolean customCanScroll(View v) {
        if ( (v instanceof Slider || v instanceof Switch) && v.isEnabled() )
            return true;
        return false;
    }
}
