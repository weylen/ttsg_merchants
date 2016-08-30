package com.strangedog.weylen.mthc.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.DatePickerDialog;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.util.CalendarUtil;

import java.lang.reflect.Field;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-08-31.
 */
public class SaleQueryDialog {

    @Bind(R.id.generic_time_group) RadioGroup genericTimeGroup;
    @Bind(R.id.custom_time_group) RadioGroup customTimeGroup;
    @Bind(R.id.radio_startTime) TextView startTimeView;
    @Bind(R.id.radio_endTime) TextView endTimeView;

    private Type status;
    private String startTime;
    private String endTime;
    private Context context;
    private OnDataListener onDataListener;

    private enum Type{
        CUSTOM, GENERIC
    }

    @OnClick(R.id.radio_startTime)
    public void onStartTimeClick(){
        status = Type.CUSTOM;
        genericTimeGroup.clearCheck();
        showDateDialog(startTimeView);
    }

    @OnClick(R.id.radio_endTime)
    public void onEndTimeClick(){
        status = Type.CUSTOM;
        genericTimeGroup.clearCheck();
        showDateDialog(endTimeView);
    }

    @OnClick(R.id.radio_today)
    public void onTodayClick(){
        customTimeGroup.clearCheck();
        status = Type.GENERIC;

        Calendar calendar = Calendar.getInstance();
        endTime = CalendarUtil.getStandardDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        startTime = endTime;
    }

    @OnClick(R.id.radio_month)
    public void onMonthClick(){
        customTimeGroup.clearCheck();
        status = Type.GENERIC;

        Calendar calendar = Calendar.getInstance();
        endTime = CalendarUtil.getStandardDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        startTime = CalendarUtil.getStandardDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
    }

    @OnClick(R.id.radio_year)
    public void onYearClick(){
        customTimeGroup.clearCheck();
        status = Type.GENERIC;

        Calendar calendar = Calendar.getInstance();
        endTime = CalendarUtil.getStandardDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        calendar.set(calendar.get(Calendar.YEAR), 0, 1);
        startTime = CalendarUtil.getStandardDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
    }

    private AlertDialog alertDialog;
    public void show(){
        if (alertDialog == null){
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_sales_query, null, false);
            ButterKnife.bind(this, view);

            alertDialog = new AlertDialog.Builder(context)
                    .setTitle("请选择时间段")
                    .setView(view)
                    .setNegativeButton("取消", (dialog, which) -> {
                        setShow(dialog, true);
                        dialog.dismiss();
                    })
                    .setPositiveButton("确定", (dialog, which) -> {
                        if (status == null){
                            setShow(dialog, false);
                            Toast.makeText(context, "请选择时间段", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (status == Type.CUSTOM){
                            if (TextUtils.isEmpty(startTime)){
                                setShow(dialog, false);
                                Toast.makeText(context, "请选择开始时间", Toast.LENGTH_SHORT).show();
                                return;
                            }else if (TextUtils.isEmpty(endTime)){
                                setShow(dialog, false);
                                Toast.makeText(context, "请选择结束时间", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        setShow(dialog, true);
                        dialog.dismiss();
                        if (onDataListener != null){
                            onDataListener.onDate(startTime, endTime);
                        }
                    }).create();
        }
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void setShow(DialogInterface dialog, boolean mShowing) {
        try {
            Field field = dialog.getClass().getSuperclass().getSuperclass()
                    .getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, mShowing);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void showDateDialog(TextView textView){
        DatePickerDialog pickerDialog = new DatePickerDialog(context);
        pickerDialog.dateRange(1, 0, 1970, 31, 11, 2200);
        pickerDialog.date(System.currentTimeMillis());
        pickerDialog.positiveAction("确定");
        pickerDialog.positiveActionClickListener(v -> {
            pickerDialog.dismiss();
            String standardTime = CalendarUtil.getStandardDate(pickerDialog.getDay(), pickerDialog.getMonth(), pickerDialog.getYear());
            textView.setText(standardTime);
            if (textView == startTimeView){
                startTime = standardTime;
            }else {
                endTime = standardTime;
            }
        });
        pickerDialog.negativeAction("取消");
        pickerDialog.negativeActionClickListener(v -> {
            pickerDialog.dismiss();
        });
        pickerDialog.show();
    }

    public void setOnDataListener(OnDataListener onDataListener) {
        this.onDataListener = onDataListener;
    }

    public SaleQueryDialog(Context context){
        this.context = context;
    }

    public interface OnDataListener{
        void onDate(String startTime, String endTime);
    }
}
