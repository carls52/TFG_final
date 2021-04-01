package com.cgs.vision.dialogos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.cgs.vision.Datos;
import com.cgs.vision.Grupo;
import com.cgs.vision.Peticion;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;
import com.cgs.vision.adaptadores.AdapterPersonaGrupo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class DialogPersonas extends AppCompatActivity {

    private Usuario actual;
    private RecyclerView personas;
    private AdapterPersonaGrupo adapter;
    private Button ok,add;
    private ImageView addView;
    private Grupo grupo;
    private Boolean addChecked;
    private TextInputEditText nuevoUsuario;
    private TextInputLayout nuevoUsuarioView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_personas);
        addChecked=true;
        try {
            actual = new Datos(DialogPersonas.this).getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ok = findViewById(R.id.ok);

        personas = findViewById(R.id.confirmados);
        grupo = new Gson().fromJson(getIntent().getStringExtra("Grupo"),Grupo.class);

        personas.setLayoutManager(new GridLayoutManager(DialogPersonas.this,1));
        adapter = new AdapterPersonaGrupo(grupo,actual);
        personas.setAdapter(adapter);
        adapterInterface(adapter);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(grupo.getDueño().equals(actual.getNombre())) {
            add = findViewById(R.id.add);
            nuevoUsuario = findViewById(R.id.nuevoUsuario);
            nuevoUsuarioView = findViewById(R.id.nuevoUsuarioView);
            addView = findViewById(R.id.addView);
            addView.setVisibility(View.VISIBLE);
            addView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(addChecked)
                    {
                        addView.setImageDrawable(getDrawable(R.drawable.ic_close_white));
                        addView.setBackground(getDrawable(R.drawable.round_close));
                        nuevoUsuario.setVisibility(View.VISIBLE);
                        nuevoUsuarioView.setVisibility(View.VISIBLE);
                        add.setVisibility(View.VISIBLE);
                        addChecked=false;
                    }
                    else {
                        addView.setImageDrawable(getDrawable(R.drawable.ic_baseline_add));
                        addView.setBackground(getDrawable(R.drawable.round_arrow));
                        nuevoUsuario.setVisibility(View.GONE);
                        nuevoUsuarioView.setVisibility(View.GONE);
                        nuevoUsuario.setText(" ");
                        add.setVisibility(View.GONE);
                        addChecked=true;
                        esconderTeclado();
                    }
                }
            });
            //tiene que actualizar todos los valores.
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(nuevoUsuario.getText().toString().trim().isEmpty())
                    {
                        new DialogTxt().msg(getString(R.string.error_empty_field),DialogPersonas.this);
                    }
                    else {
                        final Query uQuery = FirebaseDatabase.getInstance().getReference()
                                .child("NombreUsuarios").orderByKey()
                                .equalTo(nuevoUsuario.getText().toString().trim());
                        uQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.child("NombreUsuarios").child(nuevoUsuario.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String id = snapshot.child("id").getValue(String.class);
                                        ref.child("Usuarios").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Usuario usuario = snapshot.getValue(Usuario.class);
                                                usuario.addPeticion(new Peticion(grupo.getId(),grupo.getTitulo()));
                                                ref.child("Usuarios").child(usuario.getId()).setValue(usuario);
                                                grupo.addMiembroPendiente(usuario.getNombre());
                                                ref.child("Grupos").child(grupo.getId()).setValue(grupo);
                                                adapter.notifyDataSetChanged();
                                                nuevoUsuario.setText(" ");
                                                nuevoUsuario.setVisibility(View.GONE);
                                                nuevoUsuarioView.setVisibility(View.GONE);
                                                add.setVisibility(View.GONE);
                                                addView.setImageDrawable(getDrawable(R.drawable.ic_baseline_add));
                                                addView.setBackground(getDrawable(R.drawable.round_arrow));
                                                addChecked=true;
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                       /* DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        grupo.addMiembroPendiente(nuevoUsuario.getText().toString().trim());
                        actual.addPeticion(new Peticion(grupo.getId(), grupo.getTitulo()));
                        reference.child("Grupos").child(grupo.getId()).setValue(grupo);
                        reference.child("Usuarios").child(actual.getId()).setValue(actual);*/
                    }
                }
            });
        }
    }
    private void adapterInterface(AdapterPersonaGrupo adapter)
    {
        adapter.setOnItemClickListerner(new AdapterPersonaGrupo.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position, Boolean confirmado) {

            }
        });
    }
    private void esconderTeclado()
    {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(personas.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }
}