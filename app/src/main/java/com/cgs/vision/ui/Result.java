package com.cgs.vision.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.cgs.vision.adaptadores.AdapterUser;
import com.cgs.vision.dialogos.DialogTxt;
import com.cgs.vision.dialogos.BottomSheetDialog;
import com.cgs.vision.CodigoPrivado;
import com.cgs.vision.CodigoPublico;
import com.cgs.vision.Comentario;
import com.cgs.vision.Datos;
import com.cgs.vision.Grupo;
import com.cgs.vision.dialogos.GrupoDialog;
import com.cgs.vision.Peticion;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Result extends AppCompatActivity implements BottomSheetDialog.BottomSheetListener {

    private EditText mText,titulo, descripcion;
    private ImageView flecha,lugar,code;
    private Button guardar,editar;
    private FirebaseAuth mAuth;
    private TextView descripcionText,miembrosText;
    private String visibilidad,codigoText;
    private Usuario usuario;

    private String codigo;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference("Usuarios");
    private DataSnapshot user;
    private Usuario actual;
    private Datos datos;
    private RecyclerView miembros;
    private AdapterUser adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        descripcion = findViewById(R.id.desc);
        descripcionText = findViewById(R.id.descText);
        miembrosText = findViewById(R.id.miembrosText);

        titulo = findViewById(R.id.titulo);
        guardar = findViewById(R.id.guardarGrupo);
        flecha = findViewById(R.id.flecha2);
        mText = findViewById(R.id.captured_text);
        editar = findViewById(R.id.editGrupo);

        visibilidad = "PRIVADO";
        miembros = findViewById(R.id.miembros);

        mAuth = FirebaseAuth.getInstance();

        code = findViewById(R.id.code);
        lugar = findViewById(R.id.dialog);

        Datos datos = new Datos(this);
        try {
            usuario = datos.getUsuario();
            datos.encriptar("grupo",null);
            datos.encriptar("grupoId",null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //recibimos el codigo de la actividad anterior
        codigo = getIntent().getStringExtra("ocrText");
        mText.setText(codigo);

        flecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(Result.this, MainActivity.class);
                //startActivity(i);
                finish();
            }
        });

        code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(Result.this, MainActivity.class);
                //startActivity(i);
                BottomSheetDialog bottomSheet = new BottomSheetDialog("Code");
                bottomSheet.show(getSupportFragmentManager(), "BottomSheetDialog");
            }
        });


        lugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog("Tipo");
                bottomSheet.show(getSupportFragmentManager(), "BottomSheetDialog");
            }
        });
/*        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog d = new BottomSheetDialog(Result.this,R.style.Dialog);
                View va = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.dialog_select,(ConstraintLayout)findViewById(R.id.dialogView));

                va.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Result.this,"HEllo",Toast.LENGTH_LONG);
                    }
                });
            }
        });*/

        ArrayList<String> miembrosGrupo;

   /*     //inicializamos los valores de los spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.tipos,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(adapter);*/


        // NO BORRAR
        //recibimos el valor del spinner de la actividad anterior
        String aux = getIntent().getStringExtra("tipo");
        codigoText = aux;
        onButtonClickedCode(aux);
        onButtonClickedTipo(visibilidad);

        String auxGrupo = "";
        try {
            Datos datoss = new Datos(Result.this);
            auxGrupo = datoss.leerEncriptado("grupo");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(auxGrupo.equals(""))
        {
            miembrosGrupo = new ArrayList<>();
        }
        else {
            miembrosGrupo = new ArrayList<>(Arrays.asList(auxGrupo.split("\n")));
        }
        if(!miembrosGrupo.contains(usuario.getNombre()))
            miembrosGrupo.add(usuario.getNombre());

        miembros.setLayoutManager(new GridLayoutManager(Result.this, 3));
        adapter = new AdapterUser(miembrosGrupo,usuario.getNombre());
        miembros.setAdapter(adapter);
        adapterInterface(adapter);
        //END NO BORRAR
  /*      tipo.setSelection(adapter.getPosition(aux));
        visible.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(visible.getSelectedItem().toString().equals("PRIVADO"))
                {
                    descripcion.setVisibility(View.GONE);
                    editar.setVisibility(View.GONE);
                }
                else if (visible.getSelectedItem().toString().equals("PÚBLICO"))
                {
                    descripcion.setVisibility(View.VISIBLE);
                    editar.setVisibility(View.GONE);
                }
                else if(visible.getSelectedItem().toString().equals("GRUPO"))
                {
                    descripcion.setVisibility(View.VISIBLE);
                    editar.setVisibility(View.VISIBLE);
                    Intent i = new Intent(Result.this, GrupoDialog.class);
                    startActivity(i);

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Result.this, GrupoDialog.class);
                startActivity(i);
            }
        });

/*        final ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.visibilidad,android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visible.setAdapter(adapter2);
  */      //Toast.makeText(Result.this,tipo.getSelectedItem().toString(),Toast.LENGTH_LONG).show();

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //comprobamos que no haya campos vacíos
                if(!titulo.getText().toString().trim().isEmpty()
                        && !mText.getText().toString().isEmpty())
                {
                    //creamos un nuevo anuncio y lo añadimos a la lista de la base de datos
                    final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    final Date date = new Date();
                    if(visibilidad.equals("PRIVADO"))
                    {
                        CodigoPrivado nuevo = new CodigoPrivado(mText.getText().toString().trim(),
                                titulo.getText().toString().trim(),
                                usuario.getNombre().trim(),
                                formatter.format(date),
                                codigoText);

                        reference.child(mAuth.getCurrentUser().getUid()).removeValue();
                        usuario.addCodigoPrivado(nuevo);
                        reference.child(mAuth.getCurrentUser().getUid()).setValue(usuario);

                        Datos d = new Datos(Result.this);
                        try {
                            d.setUsuario(usuario);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else if(visibilidad.equals("PUBLICO"))
                    {
                        reference = database.getReference("Publico");
                        DatabaseReference newPostRef = reference.push();

                        CodigoPublico nuevo = new CodigoPublico(newPostRef.getKey(),
                                titulo.getText().toString().trim(),
                                mText.getText().toString().trim(),
                                descripcion.getText().toString().trim(),
                                usuario.getNombre(),
                                formatter.format(date),
                                0,
                                0,
                                codigoText,
                                usuario.getId());

                        newPostRef.setValue(nuevo);
                        try {
                            actual = new Datos(Result.this).getUsuario();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        actual.addCodigoPublico(nuevo.getID());
                        FirebaseDatabase.getInstance().getReference("Usuarios").child(actual.getId()).setValue(actual);
                        try {
                            new Datos(Result.this).setUsuario(actual);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    else if(visibilidad.equals("GRUPO"))
                    {
                        String grupo="";
                        String grupoNombres = "";
                        try {
                            Datos datoss = new Datos(Result.this);
                            grupo = datoss.leerEncriptado("grupoId");
                            grupoNombres = datoss.leerEncriptado("grupo");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(grupo.isEmpty())
                        {
                            DialogTxt a = new DialogTxt();
                            a.msg(getString(R.string.error_group_empty),Result.this);
                        }
                        else if(visibilidad.equals("GRUPO"))
                        {
                            reference = database.getReference("Grupos");
                            final DatabaseReference newPostRef = reference.push();
                            ArrayList<String> listaGrupoConfirmado = new ArrayList<>();
                            listaGrupoConfirmado.add(usuario.getNombre());
                            ArrayList<String> listaGrupoPendiente = new ArrayList<>(Arrays.asList(grupoNombres.split("\n")));
                            Grupo nuevoGrupo = new Grupo(titulo.getText().toString(),
                                    mText.getText().toString().trim(),
                                    usuario.getNombre(),
                                    descripcion.getText().toString().trim(),
                                    formatter.format(date),
                                    codigoText,
                                    listaGrupoConfirmado,
                                    listaGrupoPendiente,
                                    new ArrayList<Comentario>(),
                                    newPostRef.getKey());
                                    newPostRef.setValue(nuevoGrupo);
                            //ahora que ya se sube y crea el grupo hay que mandar la invitaciones a los
                            //usuarios y hacer la ventana de los grupos ala tiens curro carlos del futuro
                            //ademas ajustar el estilo del edit text de descripcion

                            final String finalGrupo = grupo;
                            final String idGrupo = newPostRef.getKey().toString();

                            //FirebaseDatabase.getInstance().getReference("Usuarios").child()

                            FirebaseDatabase.getInstance().getReference("Usuarios")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(String s : finalGrupo.split("\n"))
                                    {
                                        DataSnapshot sa = dataSnapshot.child(s);
                                        Usuario u = dataSnapshot.child(s).getValue(Usuario.class);
                                        if (u != null) {
                                            u.addPeticion(new Peticion(idGrupo,titulo.getText().toString().trim()));
                                            FirebaseDatabase.getInstance().getReference("Usuarios")
                                                    .child(s).setValue(u);
                                        }
                                    }
                                    Usuario u = dataSnapshot.child(usuario.getId()).getValue(Usuario.class);
                                    if (u != null) {
                                        u.addGrupo(idGrupo);
                                        FirebaseDatabase.getInstance().getReference("Usuarios")
                                                .child(usuario.getId()).setValue(u);
                                    }
                                    Datos datos = new Datos(Result.this);
                                    try {
                                        datos.setUsuario(u);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(Result.this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.setCancelable(false);
                    builder.setMessage(getString(R.string.success));
                    builder.show();
                }

                else
                {
                    DialogTxt a = new DialogTxt();
                    a.msg(getString(R.string.error_empty_fields),Result.this);
                    if(titulo.getText().toString().trim().isEmpty())
                        titulo.setBackground(getDrawable(R.drawable.text_editable_error));
                    else if(titulo.getBackground()!=getDrawable(R.drawable.text_editable))
                        titulo.setBackground(getDrawable(R.drawable.text_editable));

                    if(mText.getText().toString().trim().isEmpty())
                        mText.setBackground(getDrawable(R.drawable.text_editable_error));
                    else if(mText.getBackground()!=getDrawable(R.drawable.text_editable))
                        mText.setBackground(getDrawable(R.drawable.text_editable));

                    if(descripcion.getText().toString().trim().isEmpty())
                        descripcion.setBackground(getDrawable(R.drawable.text_editable_error));
                    else if(descripcion.getBackground()!=getDrawable(R.drawable.text_editable))
                        descripcion.setBackground(getDrawable(R.drawable.text_editable));
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<String> miembrosGrupo;
        String auxGrupo = "";
        try {
            Datos datoss = new Datos(Result.this);
            auxGrupo = datoss.leerEncriptado("grupo");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(auxGrupo.equals(""))
        {
            miembrosGrupo = new ArrayList<>();
        }
        else {
            miembrosGrupo = new ArrayList<>(Arrays.asList(auxGrupo.split("\n")));
        }
        if(!miembrosGrupo.contains(usuario.getNombre()))
            miembrosGrupo.add(usuario.getNombre());

        miembros.setLayoutManager(new GridLayoutManager(Result.this,3));
        adapter = new AdapterUser(miembrosGrupo,usuario.getNombre());
        miembros.setAdapter(adapter);
        adapterInterface(adapter);
    }

    @Override
    public void onButtonClickedTipo(String text) {
        switch (text)
        {
            case "PRIVADO":
                lugar.setImageDrawable(getDrawable(R.drawable.lock_closed_white));
                visibilidad ="PRIVADO";
                descripcion.setVisibility(View.GONE);
                descripcionText.setVisibility(View.GONE);
                editar.setVisibility(View.GONE);
                miembrosText.setVisibility(View.GONE);
                miembros.setVisibility(View.GONE);
                break;
            case "PUBLICO":
                lugar.setImageDrawable(getDrawable(R.drawable.lock_open_white));
                visibilidad ="PUBLICO";
                descripcion.setVisibility(View.VISIBLE);
                descripcionText.setVisibility(View.VISIBLE);
                editar.setVisibility(View.GONE);
                miembros.setVisibility(View.GONE);
                miembrosText.setVisibility(View.GONE);
                break;
            case "GRUPO":
                lugar.setImageDrawable(getDrawable(R.drawable.ic_people_24));
                visibilidad ="GRUPO";
                descripcion.setVisibility(View.VISIBLE);
                descripcionText.setVisibility(View.VISIBLE);
                editar.setVisibility(View.VISIBLE);
                miembros.setVisibility(View.VISIBLE);
                miembrosText.setVisibility(View.VISIBLE);
                Intent i = new Intent(Result.this, GrupoDialog.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onButtonClickedCode(String text) {
        switch (text)
        {
            case "JAVA":
                code.setImageDrawable(getDrawable(R.drawable.ic_j));
                code.setBackground(getDrawable(R.drawable.circle_java));
                codigoText="JAVA";
                break;
            case "PYTHON":
                code.setBackground(getDrawable(R.drawable.circle_python));
                code.setImageDrawable(getDrawable(R.drawable.ic_py));
                codigoText="PYTHON";
                break;
            case "C":
                code.setBackground(getDrawable(R.drawable.circle_c));
                code.setImageDrawable(getDrawable(R.drawable.ic_c));
                codigoText="C";
                break;
            case "C++":
                code.setBackground(getDrawable(R.drawable.circle_cplus));
                code.setImageDrawable(getDrawable(R.drawable.ic_cplus));
                codigoText="CPLUS";
                break;
            case "JAVASCRIPT":
                code.setBackground(getDrawable(R.drawable.circle_javascript));
                code.setImageDrawable(getDrawable(R.drawable.ic_js));
                codigoText="JAVASCRIPT";
                break;
            case "OTROS":
                code.setBackground(getDrawable(R.drawable.circle_txt));
                code.setImageDrawable(getDrawable(R.drawable.ic_txt));
                codigoText="OTROS";
                break;
        }
    }
    private void adapterInterface(final AdapterUser adapter)
    {
        adapter.setOnItemClickListerner(new AdapterUser.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick(final int position) {
               /* final androidx.appcompat.app.AlertDialog.Builder mensaje = new androidx.appcompat.app.AlertDialog.Builder(GrupoDialog.this);
                mensaje.setMessage("¿De verdad quieres eliminar a este usuario del grupo?");
                mensaje.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String c = list.get(position);
                        list.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                });
                mensaje.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                mensaje.create();
                mensaje.show();
            */}
        });
    }

}
