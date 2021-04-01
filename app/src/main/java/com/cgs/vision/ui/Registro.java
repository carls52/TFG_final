package com.cgs.vision.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cgs.vision.dialogos.DialogTxt;
import com.cgs.vision.Datos;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Registro extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText correo,contraseña,contraseña2,nombreUsuario;
    private DialogTxt alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        alert = new DialogTxt();
        mAuth = FirebaseAuth.getInstance();

        nombreUsuario = findViewById(R.id.nombreUsuario);
        contraseña2 = findViewById(R.id.contraseña2);
        contraseña = findViewById(R.id.contraseña);
        correo = findViewById(R.id.correo);
        Button guardar = findViewById(R.id.guardarGrupo);
        ImageView flecha = findViewById(R.id.flecha2);

        flecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Registro.this , Login.class);
                startActivity(i);
                finish();
            }
        });
        contraseña2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                checkContraseña();
            }
        });
        correo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                checkContraseña();
            }
        });
        nombreUsuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                checkContraseña();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String error = null;
                //Comprobamos que no haya campos vacíos
                if (!checkFormularioVacio()) {
                    alert.msg(getString(R.string.error_empty_fields),Registro.this);
                }
                //comprobamos que la contraseña escrita cumpla las condiciones
                else if (!checkContraseña()) {
                    alert.msg(getString(R.string.error_pass_incorrect),Registro.this);
                }
                //comprobamos que las contraseñas coincidan
                else if (!(contraseña.getText().toString()).equals(contraseña2.getText().toString()))
                {
                    alert.msg(getString(R.string.error_pass_not_match),Registro.this);
                }
                //comprobamos  si el correo tiene el formato adecuado
                else if (!android.util.Patterns.EMAIL_ADDRESS
                        .matcher(correo.getText().toString().trim()).matches())
                {
                    alert.msg(getString(R.string.error_mail_incorrect),Registro.this);
                }
                else
                {
                    //comprobamos la base de datos para que el nombre de ususario no se repita
                    Query uQuery = FirebaseDatabase.getInstance().getReference()
                            .child("Usuarios").orderByChild("nombre")
                            .equalTo(nombreUsuario.getText().toString().trim());
                    uQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                alert.msg(getString(R.string.error_user_exist),Registro.this);
                            }
                            else
                            {
                                //comprobamos que el correo no este registrado.
                                mAuth.fetchSignInMethodsForEmail(correo.getText().toString().trim())
                                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task)
                                        {
                                            if (!task.getResult().getSignInMethods().isEmpty()
                                                 && task.getResult().getSignInMethods() != null)
                                            {
                                                alert.msg(getString(R.string.error_mail_exist),Registro.this);
                                            }
                                            else
                                            {
                                                createAccount(correo.getText().toString(),
                                                   contraseña.getText().toString(),
                                                   nombreUsuario.getText().toString());
                                            }
                                        }
                                    });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            alert.msg(getString(R.string.error_unexpected),Registro.this);
                        }
                    });
                }
            }
        });
    }

    private boolean checkContraseña()
    {
        contraseña = findViewById(R.id.contraseña);
        if(contraseña.getText().toString().length() > 0 && contraseña.getText().toString().length() < 6 )
        {
            contraseña.setError(getString(R.string.error_pass_under_six));
            contraseña.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
            return false;
        }
        else if(contraseña.getText().toString().length() == 0)
        {
            return false;
        }
        else
        {
            contraseña.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#008000")));
            return true;
        }
    }

    private boolean checkFormularioVacio()
    {
        return (!(contraseña.getText().toString().isEmpty()) &&
                !(contraseña2.getText().toString().isEmpty()) &&
                !(correo.getText().toString().isEmpty()) &&
                !(nombreUsuario.getText().toString().isEmpty()));
    }

    //se crea el usuario con contraseña y correo. Posteriormente se añade el nombre de usuario
    //si al añadir el nombre de usuario sucede algun problema se borrara el usuario y fallara
    void createAccount(final String email, final String password, final String nombreUsuario)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                FirebaseUser user = mAuth.getCurrentUser();
                if (task.isSuccessful())
                {
                   if(user!=null)
                   {
                       //creamos dos referencias al usuario en 2 tablas: Usuarios y NombresUsuarios
                       String userId = user.getUid();
                       DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference()
                               .child("Usuarios").child(userId);
                       SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                       Date fecha = new java.util.Date();


                      // Usuario nuevo = new Usuario(nombreUsuario,userId,cPrivado,favoritos);
                       Usuario nuevo = new Usuario(nombreUsuario,userId);
                       userInfo.setValue(nuevo);
                       FirebaseDatabase.getInstance().getReference()
                               .child("NombreUsuarios").child(nombreUsuario).child("id").setValue(userId);

                       //Guardamos el usuario en memoria para el log in sin internet
                       Datos datos = new Datos(Registro.this);
                       try {
                           datos.setUsuario(nuevo);
                           datos.encriptar("correo",email);
                           datos.encriptar("contraseña",password);
                       } catch (Exception e) {
                           e.printStackTrace();
                       }

                        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    alert.msg(getString(R.string.alert_register_correct),Registro.this);

                                    Intent i = new Intent(Registro.this, MenuMain.class);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        });
                   }
                   else
                   {
                       user.delete();
                       alert.msg(getString(R.string.error_register),Registro.this);
                   }
                }
                else {
                    alert.msg(getString(R.string.error_register),Registro.this);
                }
            }
         })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                alert.msg(getString(R.string.error_register),Registro.this);
            }
        });
    }

}
