package com.luiz.mg.magickey.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * Classe que mostra o calendário para escolha da data de filtro
 * Extendida da Classe DialogFragment
 * Implementa métodos da classe DatePickerDialog.OnDateSetListener
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private final TextView tvDate;

    /**
     * Construtor da classe
     * @param tvDate TextView com a data
     */
    public DatePickerFragment(TextView tvDate) {
        this.tvDate = tvDate;
    }

    /**
     * Método sobrescrito para criar o calendário
     * @param savedInstanceState Bundle
     * @return DatePickerDialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     * Método que seta o texto do TextView tvDate ao se escolher uma data
     * @param view DatePicker
     * @param year int ano
     * @param month int mês
     * @param day int dia
     */
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
