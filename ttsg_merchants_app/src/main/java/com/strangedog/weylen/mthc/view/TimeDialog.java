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
import com.rey.material.app.TimePickerDialog;
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
public class TimeDialog {

    @Bind(R.id.radio_startTime) TextView startTimeView;
    @Bind(R.id.radio_endTime) TextView endTimeView;

    private String startTime;
    private String endTime;
    private Context context;
    private OnDataListener onDataListener;
    private DialogInterface.OnCancelListener onCancelListener;

    @OnClick(R.id.radio_startTime)
    public void onStartTimeClick(){
        showTimeDialog(startTimeView);
    }

    @OnClick(R.id.radio_endTime)
    public void onEndTimeClick(){
        showTimeDialog(endTimeView);
    }

    private void clearTexts(){
        startTimeView.setText(Constants.EMPTY_STR);
        endTimeView.setText(Constants.EMPTY_STR);
    }

    private AlertDialog alertDialog;
    public void show(){
        if (alertDialog == null){
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_time, null, false);
            ButterKnife.bind(this, view);
            startTimeView.setText(startTime);
            endTimeView.setText(endTime);

            alertDialog = new AlertDialog.Builder(context)
                    .setTitle("请选择时间段")
                    .setView(view)
                    .setNegativeButton("取消", (dialog, which) -> {
                        setShow(dialog, true);
                        dialog.dismiss();
                        if (onCancelListener != null){
                            onCancelListener.onCancel(dialog);
                        }
                    })
                    .setPositiveButton("确定", (dialog, which) -> {
                        startTime = startTimeView.getText().toString();
                        endTime = endTimeView.getText().toString();
                        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)){
                            setShow(dialog, false);
                            Toast.makeText(context, "请选择开始或结束时间", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        setShow(dialog, true);
                        dialog.dismiss();
                        if (onDataListener != null){
                            onDataListener.onDate(startTime, endTime);
                        }
                    }).create();
        }
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
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

    private void showTimeDialog(TextView textView){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(context);
        timePickerDialog.hour(calendar.get(Calendar.HOUR_OF_DAY));
        timePickerDialog.minute(calendar.get(Calendar.MINUTE));
        timePickerDialog.positiveAction("确定");
        timePickerDialog.positiveActionClickListener(v -> {
            String time = CalendarUtil.getStandardTime(timePickerDialog.getMinute(), timePickerDialog.getHour());
            textView.setText(time);
            timePickerDialog.dismiss();
        });
        timePickerDialog.show();
    }

    public void setOnDataListener(OnDataListener onDataListener) {
        this.onDataListener = onDataListener;
    }

    public TimeDialog(Context context, String startTime, String endTime){
        this.context = context;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public interface OnDataListener{
        void onDate(String startTime, String endTime);
    }
}
