package com.cgs.vision.dialogos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.cgs.vision.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.Nullable;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private String tipo;

    public BottomSheetDialog() {
    }
    public BottomSheetDialog(String tipo) {
        this.tipo = tipo;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        if(this.tipo.equals("Code")) {
            v = inflater.inflate(R.layout.dialog_select_code, container, false);
            TextView java = v.findViewById(R.id.java);
            TextView python = v.findViewById(R.id.python);
            TextView c = v.findViewById(R.id.c);
            TextView cplus = v.findViewById(R.id.cplus);
            TextView javascript = v.findViewById(R.id.javascript);
            TextView txt = v.findViewById(R.id.txt);
            java.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClickedCode("JAVA");
                    dismiss();
                }
            });
            python.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClickedCode("PYTHON");
                    dismiss();
                }
            });
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClickedCode("C");
                    dismiss();
                }
            });
            cplus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClickedCode("C++");
                    dismiss();
                }
            });
            javascript.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClickedCode("JAVASCRIPT");
                    dismiss();
                }
            });
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClickedCode("OTROS");
                    dismiss();
                }
            });
        }
        else
        {
            v = inflater.inflate(R.layout.dialog_select_tipo, container, false);
            ConstraintLayout button1 = v.findViewById(R.id.t1View);
            ConstraintLayout button2 = v.findViewById(R.id.t2View);
            ConstraintLayout button3 = v.findViewById(R.id.t3View);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClickedTipo("PRIVADO");
                    dismiss();
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClickedTipo("PUBLICO");
                    dismiss();
                }
            });
            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClickedTipo("GRUPO");
                    dismiss();
                }
            });
        }
        return v;
    }
    public interface BottomSheetListener {
        void onButtonClickedTipo(String text);
        void onButtonClickedCode(String text);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}
