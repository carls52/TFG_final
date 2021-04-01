package com.cgs.vision.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


import com.cgs.vision.dialogos.DialogTxt;
import com.cgs.vision.Datos;
import com.cgs.vision.dialogos.DialogCargando;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText correo,contraseña;
    private CheckBox recuerdame;
    private String correoGuardado,contraseñaGuardada;
    private DialogCargando cargando;
    private static final int requestPermissionID = 101;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        cargando = new DialogCargando(Login.this);
        correo = findViewById(R.id.correo);
        contraseña = findViewById(R.id.contraseña);
        recuerdame = findViewById(R.id.recuerdame);

        Datos datos = new Datos(Login.this);
        String idiomaGuardado = "";
            idiomaGuardado = datos.getAjustes("idioma");

        Configuration configuration = getResources().getConfiguration();
        String idiomaDispositivo = configuration.locale.getLanguage();

        if(idiomaGuardado.equals(""))
        {
            if(idiomaDispositivo.equals("es") || idiomaDispositivo.equals("en")) {
                idiomaGuardado = idiomaDispositivo;
                cambiarIdioma(idiomaDispositivo);
            }
            else
                cambiarIdioma("en");

                datos.setAjustes("idioma",idiomaGuardado);
        }

        if(idiomaGuardado.equals("en") && !idiomaDispositivo.equals("en"))
            cambiarIdioma("en");
        else if(idiomaGuardado.equals("es") && !idiomaDispositivo.equals("es"))
            cambiarIdioma("es");

        //leemos por si hay datos guardados en memoria
        try {
            correoGuardado = datos.leerEncriptado("Correo");
            contraseñaGuardada = datos.leerEncriptado("Contraseña");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //si estos valores no son null iniciamos sesión
        if (!correoGuardado.equals("") && !contraseñaGuardada.equals("")) {
            cargando.startCargando();
            Acceder(correoGuardado,contraseñaGuardada,true);
        }

        //Si pulsamos el boton Registrarse
        Button registrarse = findViewById(R.id.guardarGrupo);
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(Login.this, Registro.class);
                startActivity(t);
                finish();
            }

        });
        //Si pulsamos el boton Entrar
        Button entrar = findViewById(R.id.entrar);
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargando.startCargando();
                if(correo.getText().toString().isEmpty() || contraseña.getText().toString().isEmpty())
                {
                    DialogTxt alert = new DialogTxt();
                    alert.msg(getString(R.string.error_empty_fields),Login.this);
                }
                else
                    Acceder(correo.getText().toString().trim(), contraseña.getText().toString().trim(),recuerdame.isChecked());

                cargando.stopCargando();
            }
        });

        if(ContextCompat.checkSelfPermission(Login.this,CAMERA_SERVICE) != PackageManager.PERMISSION_GRANTED)
        {
            Permisos();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
            finish();
    }
    private void Permisos()
    {
     if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
    } else {
        ActivityCompat.requestPermissions(Login.this,
                new String[] { Manifest.permission.CAMERA },
                101);
    }
    }
    private void Acceder(final String correo, final String contraseña, final boolean isChecked) {
        mAuth.signInWithEmailAndPassword(correo,contraseña)
           .addOnCompleteListener(new OnCompleteListener<AuthResult>()
           {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task)
               {
                   if(task.isSuccessful())
                   {
                       DatabaseReference reference = FirebaseDatabase.getInstance()
                               .getReference("Usuarios")
                               .child(mAuth.getCurrentUser().getUid());
                       reference.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                               //guardamos el usuario en memoria
                               Datos d = new Datos(Login.this);
                               try {
                                   d.setUsuario(usuario);
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }
                               if(isChecked) {
                                   try {
                                       d.encriptar("Contraseña", contraseña);
                                       d.encriptar("Correo", correo);
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }
                               }
                               Intent i = new Intent(Login.this, MenuMain.class);
                               cargando.stopCargando();
                               startActivity(i);
                               finish();
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                               new DialogTxt().msg(getString(R.string.error_unexpected),Login.this);

                           }
                       });
                   }
                   else {
                       new DialogTxt().msg(getString(R.string.error_login), Login.this);
                       cargando.stopCargando();
                   }

               }
           });
    }
    private void cambiarIdioma(String idioma)
    {
        Configuration configuration = getResources().getConfiguration();
        switch (idioma)
        {
            case "en":
                configuration.setLocale(Locale.ENGLISH);
                break;
            case "es":
                configuration.setLocale(Locale.forLanguageTag("es"));
                break;
        }
        getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
        Intent i = new Intent(Login.this,Login.class);
        startActivity(i);
        finish();

    }
}
