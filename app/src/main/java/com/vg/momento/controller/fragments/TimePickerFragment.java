package com.vg.momento.controller.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import com.vg.momento.R;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment{

    private static final String ARG_DATE = "date";

    public static final String EXTRA_DATE = "com.vg.android.momento.date";

    private TimePicker mTimePicker;

    private int mDay;

    private int mMonth;

    private int mYear;

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mMonth = calendar.get(Calendar.MONTH);
        mYear = calendar.get(Calendar.YEAR);

        mTimePicker = (TimePicker) view.findViewById(R.id.dialog_time_time_picker);
        if(Build.VERSION.SDK_INT < 23){
            mTimePicker.setCurrentMinute(minute);
            mTimePicker.setCurrentHour(hour);
        } else {
            mTimePicker.setMinute(minute);
            mTimePicker.setHour(hour);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int minute, hour;
                                if(Build.VERSION.SDK_INT < 23){
                                    minute = mTimePicker.getCurrentMinute();
                                    hour = mTimePicker.getCurrentHour();
                                } else {
                                    minute = mTimePicker.getMinute();
                                    hour = mTimePicker.getHour();
                                }

                                Date date = new GregorianCalendar(mYear, mMonth, mDay, hour, minute).getTime();
                                sendResult(Activity.RESULT_OK, date);
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
