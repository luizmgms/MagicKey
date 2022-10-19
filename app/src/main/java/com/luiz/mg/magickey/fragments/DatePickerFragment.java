package com.luiz.mg.magickey.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private final TextView tvDate;

    public DatePickerFragment(TextView tvDate) {
        this.tvDate = tvDate;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        month = month + 1;
        String sMonth = String.valueOf(month);
        String sDay = String.valueOf(day);

        if (month < 10)
            sMonth = "0"+month;

        if (day < 10)
            sDay = "0"+day;

        String date = sDay+"/"+sMonth+"/"+year;

        tvDate.setText(date);

    }
}
