package com.cgs.vision.dialogos;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class DialogTxt {

    public DialogTxt() {
    }

    public void msg(String txt, Context contexto)
    {
        final AlertDialog.Builder mensaje = new AlertDialog.Builder(contexto);
        mensaje.setMessage(txt);
        mensaje.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        mensaje.create();
        mensaje.show();
    }
}
