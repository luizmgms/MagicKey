package com.luiz.mg.magickey.utils;

import android.view.View;

import androidx.appcompat.app.AlertDialog;

public abstract class DialogButtonClickWrapper implements View.OnClickListener {

    private final AlertDialog dialog;

    public DialogButtonClickWrapper(AlertDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onClick(View v) {

        if(onClicked()){
            dialog.dismiss();
        }
    }

    protected abstract boolean onClicked();
}
