package com.cgs.vision.dialogos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;

import com.cgs.vision.Datos;
import com.cgs.vision.R;
import com.cgs.vision.ui.CloudVision;
import com.cgs.vision.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class SelectDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_select);

        Button online = findViewById(R.id.buttonSi);
        Button local = findViewById(R.id.buttonNo);
        final TextView usos = findViewById(R.id.usos);
        final CheckBox box = findViewById(R.id.checkBox);

        Datos datos = new Datos(SelectDialog.this);
        if(datos.getAjustes("Mostrar").equals("NO"))
        {
            Intent i = new Intent(SelectDialog.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        String s = datos.getAjustes("Mostrar");
        if(s==null)
        {
            datos.setAjustes("Mostrar","SI");
        }

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("prueba");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usos.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCaja(box.isChecked());
                restarUso(ref);
                Intent i = new Intent(SelectDialog.this, CloudVision.class);
                startActivity(i);
                finish();
            }
        });

        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCaja(box.isChecked());
                Intent i = new Intent(SelectDialog.this,MainActivity.class);
                startActivity(i);
                finish();

            }
        });
    }
    private void checkCaja(boolean b)
    {
        Datos datos = new Datos(SelectDialog.this);
        if(b)
            datos.setAjustes("Mostrar","NO");
        else
            datos.setAjustes("Mostrar","SI");
    }
    private void restarUso(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Object p = mutableData.getValue();
                if (p == null) {
                    return Transaction.success(mutableData);
                }
                String s = p.toString();
                int v =  Integer.parseInt(s);
                v--;
                // Set value and report transaction success
                mutableData.setValue(v);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                // Log.d(, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
}