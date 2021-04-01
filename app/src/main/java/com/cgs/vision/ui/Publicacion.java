package com.cgs.vision.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cgs.vision.CodigoPrivado;
import com.cgs.vision.CodigoPublico;
import com.cgs.vision.Comentario;
import com.cgs.vision.Datos;
import com.cgs.vision.Grupo;
import com.cgs.vision.Peticion;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;
import com.cgs.vision.adaptadores.AdapterComentarios;
import com.cgs.vision.adaptadores.AdapterUser;
import com.cgs.vision.dialogos.DialogTxt;
import com.cgs.vision.dialogos.BottomSheetDialog;
import com.cgs.vision.dialogos.DialogPersonas;
import com.cgs.vision.dialogos.GrupoDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class Publicacion extends AppCompatActivity implements BottomSheetDialog.BottomSheetListener{
    private TextView titulo,descripcion,codigo,sinComentarios, comentariosText,descripcionText, comentarioNuevo;
    private String posicion, visibilidadOriginal;
    private RecyclerView comentariosView,miembros;
    private Button guardar,comentarioNuevoBoton,valorar;
    private Usuario actual;
    private CodigoPrivado codigoP;
    private CodigoPublico codigoPu;
    private Grupo grupo;
    private ImageView atras,lugar,code,editar,stats,gente,editarGrupo;
    private String codigoText, visibilidadActual;
    private Boolean editarChecked;
    private DatabaseReference reference;
    private AdapterComentarios adapterComentarios;
    private ConstraintLayout miembrosLayout;
    private ArrayList<String> miembrosGrupo;
    private RatingBar ratingBar;
/*
/
/
probar hacer cambios en los 4 casos posibles (Pr,PU,G,GrupoDueño)
probar hacer los cambios de clase y guarda r y POR FIN ya estaria
/
/
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacion);

        miembrosGrupo = new ArrayList<>();
        editarChecked = true;
        atras = findViewById(R.id.atras);
        stats = findViewById(R.id.stats);
        gente = findViewById(R.id.gente);
        code = findViewById(R.id.code);
        lugar = findViewById(R.id.dialog);
        editar = findViewById(R.id.editar);
        titulo = findViewById(R.id.tituloPrivado);
        descripcionText = findViewById(R.id.descripcionText);
        descripcion = findViewById(R.id.descPrivado);
        codigo = findViewById(R.id.codigoPrivado);
        guardar = findViewById(R.id.guardarPrivado);
        comentarioNuevo = findViewById(R.id.comentarioNuevo);
        comentarioNuevoBoton = findViewById(R.id.comentarioNuevoBoton);
        comentariosText = findViewById(R.id.comentariosText);
        sinComentarios = findViewById(R.id.sinComentarios);
        comentariosView = findViewById(R.id.comentarios);
        editarGrupo = findViewById(R.id.editarGrupo);
        miembros = findViewById(R.id.miembros);
        miembrosLayout = findViewById(R.id.miembrosView);
        ratingBar = findViewById(R.id.ratingBar);
        valorar = findViewById(R.id.valorar);
        reference = FirebaseDatabase.getInstance().getReference();

        //iniciamos las transiciones
        titulo.setTransitionName("tituloPrivado");
        final Datos datos = new Datos(Publicacion.this);
        try {
            actual = datos.getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }
        visibilidadOriginal = getIntent().getStringExtra("Tipo");

        switch(visibilidadOriginal)
        {
            case "PRIVADO":
                posicion = getIntent().getStringExtra("Posicion");
                codigoP = actual.getCodigosPrivados().get(parseInt(posicion));
                setDatos(codigoP.getTitulo(),codigoP.getCodigo(),"",codigoP.getTipo());

                break;
            case "PUBLICO":
                descripcion.setVisibility(View.VISIBLE);
                codigoPu = new Gson().fromJson(getIntent().getStringExtra("Id"),CodigoPublico.class);
                setDatos(codigoPu.getTitulo(),codigoPu.getCodigo(),codigoPu.getDescripcion(),codigoPu.getTipo());
                setComentarios(codigoPu.getComentarios());
                descripcion.setTransitionName("descripcion");
                if(!codigoPu.getAutor().equals(actual.getNombre()))
                {
                    editar.setVisibility(View.INVISIBLE);
                    stats.setVisibility(View.INVISIBLE);
                }
                else
                {
                    stats.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Estadisticas(Publicacion.this,codigoPu);
                        }
                    });
                }
                gente.setVisibility(View.GONE);
                if(!actual.getNombre().equals(codigoPu.getAutor())) {
                    FirebaseDatabase.getInstance().getReference("Publico").child(codigoPu.getID())
                            .child("visualizaciones").runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            Object p = mutableData.getValue();
                            if (p == null) {
                                return Transaction.success(mutableData);
                            }
                            String s = p.toString();
                            int v = Integer.parseInt(s);
                            v++;
                            codigoPu.setVisualizaciones(v);
                            // Set value and report transaction success
                            mutableData.setValue(v);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                        }
                    });
                }
                break;
            case "GRUPO":
                grupo = new Gson().fromJson(getIntent().getStringExtra("Id"),Grupo.class);
                setDatos(grupo.getTitulo(),grupo.getCodigo(),grupo.getDescripcion(),grupo.getTipo());
                setComentarios(grupo.getComentarios());
                descripcion.setTransitionName("descripcion");
                stats.setVisibility(View.GONE);
                gente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Publicacion.this, DialogPersonas.class);
                        i.putExtra("Grupo",new Gson().toJson(grupo));
                        startActivity(i);
                    }
                });
                break;
            default:
                finish();
        }
        viewManager(visibilidadOriginal);
        valorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("Ratings").child(codigoPu.getID());
                    ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                            if(map == null)
                            {
                                HashMap<String,Float> mapNew = new HashMap<>();
                                mapNew.put(actual.getNombre(),ratingBar.getRating());
                                ratingRef.setValue(mapNew);
                                codigoPu.addRating(ratingBar.getRating());
                            }
                            else
                            {
                                if (map.containsKey(actual.getNombre())) {
                                    String old = map.get(actual.getNombre()).toString();
                                    float casting = Float.parseFloat(old);
                                    codigoPu.sustituirRating(casting,ratingBar.getRating());
                                    map.put(actual.getNombre(),ratingBar.getRating());
                                    ratingRef.setValue(map);
                                }
                                else {
                                    codigoPu.addRating(ratingBar.getRating());
                                }
                            }
                            FirebaseDatabase.getInstance().getReference("Publico").child(codigoPu.getID()).setValue(codigoPu);
                            valorar.setText("VALORADO");
                            valorar.setFocusable(false);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                //codigoPu.addRating(actual.getNombre(),ratingBar.getRating());
                //FirebaseDatabase.getInstance().getReference("Publico").child(codigoPu.getID()).setValue(codigoPu);
                FirebaseDatabase.getInstance().getReference("Ratings").child(codigoPu.getID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String,Float> map = (HashMap<String, Float>) snapshot.getValue();
                        String a = "asd";
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
              //  finish();
            }
        });
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editarChecked) {
                    editar.setImageDrawable(getDrawable(R.drawable.ic_close_white));
                    editar.setBackground(getDrawable(R.drawable.round_close));
                    editarView(editarChecked, visibilidadOriginal);
                    editarChecked = false;
                }
                else {
                    editar.setImageDrawable(getDrawable(R.drawable.ic_baseline_edit_24));
                    editar.setBackground(getDrawable(R.drawable.round_arrow));
                    editarView(false, visibilidadOriginal);
                    switch(visibilidadOriginal) {
                        case "PRIVADO":
                            setDatos(codigoP.getTitulo(), codigoP.getCodigo(), "", codigoP.getTipo());
                            break;
                        case "PUBLICO":
                            setDatos(codigoPu.getTitulo(), codigoPu.getCodigo(), codigoPu.getDescripcion(), codigoPu.getTipo());
                            break;
                        case "GRUPO":
                            setDatos(grupo.getTitulo(), grupo.getCodigo(), grupo.getDescripcion(), grupo.getTipo());
                            break;
                    }
                    editarChecked = true;
                    esconderTeclado();
                }
            }
            });
        code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visualizaciones=0,descargas=0;
                Boolean cambiar = true;
                ArrayList<Comentario> coments = new ArrayList<>();
                if(visibilidadActual.equals(visibilidadOriginal))
                {
                    if(visibilidadOriginal.equals("PRIVADO"))
                        cambiar = HayCambios(codigoP,visibilidadOriginal);
                    else if (visibilidadOriginal.equals("PUBLICO"))
                        cambiar = HayCambios(codigoPu,visibilidadOriginal);
                    else if (visibilidadOriginal.equals("GRUPO"))
                        cambiar = HayCambios(grupo,visibilidadOriginal);
                }
                else
                {
                    if(visibilidadOriginal.equals("GRUPO") && visibilidadActual.equals("PUBLICO"))
                        coments.addAll(grupo.getComentarios());

                    if(visibilidadOriginal.equals("PUBLICO") && visibilidadActual.equals("GRUPO"))
                        coments.addAll(codigoPu.getComentarios());
                }
                if(cambiar)
                {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    switch (visibilidadActual) {
                        case "PRIVADO":
                            CodigoPrivado nuevo = new CodigoPrivado(codigo.getText().toString().trim(),
                                    titulo.getText().toString().trim(),
                                    actual.getNombre(),
                                    formatter.format(date),
                                    codigoText);
                            if(visibilidadOriginal.equals("PRIVADO")) {
                                actual.updateCodigoPrivado(nuevo, parseInt(posicion));
                                reference.child("Usuarios").child(actual.getId()).setValue(actual);
                                try {
                                    datos.setUsuario(actual);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                actual.addCodigoPrivado(nuevo);
                                if(visibilidadOriginal.equals("PUBLICO"))
                                    BorrarDatos(codigoPu,visibilidadOriginal);
                                else
                                    BorrarDatos(grupo,visibilidadOriginal);
                            }

                                finish();
                            break;
                        case "PUBLICO":
                            if(descripcion.getText().toString().trim().isEmpty() || titulo.getText().toString().trim().isEmpty()
                            || codigo.getText().toString().trim().isEmpty())
                            {
                                DialogTxt alert = new DialogTxt();
                                alert.msg(getString(R.string.error_empty_fields), Publicacion.this);
                            }
                            else {
                                reference = FirebaseDatabase.getInstance().getReference("Publico");
                                String id;
                                if (visibilidadOriginal.equals("PUBLICO")) {
                                    id = codigoPu.getID();
                                    visualizaciones = codigoPu.getVisualizaciones();
                                    descargas = codigoPu.getDescargas();
                                } else
                                    id = reference.push().getKey();

                                CodigoPublico nuevoPublico = new CodigoPublico(id,
                                        titulo.getText().toString().trim(),
                                        codigo.getText().toString().trim(),
                                        descripcion.getText().toString().trim(),
                                        actual.getNombre(),
                                        formatter.format(date),
                                        visualizaciones,
                                        descargas,
                                        coments,
                                        codigoText,
                                        actual.getId());
                                reference.child(id).setValue(nuevoPublico);
                                if (visibilidadOriginal.equals("PRIVADO")) {
                                    actual.addCodigoPublico(nuevoPublico.getID());
                                    BorrarDatos(codigoP, visibilidadOriginal);
                                }
                                else if (visibilidadOriginal.equals("GRUPO")){
                                    actual.addCodigoPublico(nuevoPublico.getID());
                                    BorrarDatos(grupo, visibilidadOriginal);
                                }
                                finish();
                            }
                            break;
                        case "GRUPO":
                            ArrayList<String> miembrosPendientes=new ArrayList<>();
                            ArrayList<String> miembrosConfirmados=new ArrayList<>();
                            String id;
                            reference.child("Grupos");
                            //FALTA ESTE POR HACER Y YA ESTARIA
                            if(visibilidadOriginal.equals("GRUPO"))
                            {
                                miembrosConfirmados=grupo.getMiembrosConfirmados();
                                miembrosPendientes= grupo.getMiembrosPendientes();
                                id = grupo.getId();
                            }
                            else
                            {
                                id = FirebaseDatabase.getInstance().getReference("Grupos").push().getKey();
                                miembrosPendientes=miembrosGrupo;
                                if(miembrosPendientes.contains(actual.getNombre()))
                                    miembrosPendientes.remove(actual.getNombre());
                                miembrosConfirmados.add(actual.getNombre());
                            }
                            final Grupo nuevoGrupo = new Grupo(titulo.getText().toString(),
                                    codigo.getText().toString().trim(),
                                    actual.getNombre(),
                                    descripcion.getText().toString().trim(),
                                    formatter.format(date),
                                    codigoText,
                                    miembrosConfirmados,
                                    miembrosPendientes,
                                    coments,
                                    id);
                            reference.child("Grupos").child(id).setValue(nuevoGrupo);
                            FirebaseDatabase.getInstance().getReference("NombreUsuarios")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    final ArrayList<String> idUsers = new ArrayList<>();
                                    for(String s :nuevoGrupo.getMiembrosPendientes())
                                    {
                                        idUsers.add(snapshot.child(s).getChildren().iterator().next().getValue(String.class));
                                    }
                                    FirebaseDatabase.getInstance().getReference("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(String s :idUsers)
                                            {
                                                Usuario u = snapshot.child(s).getValue(Usuario.class);
                                                u.addPeticion(new Peticion(nuevoGrupo.getId(),nuevoGrupo.getTitulo()));
                                                FirebaseDatabase.getInstance().getReference("Usuarios").child(s).setValue(u);
                                            }
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
                            if(visibilidadOriginal.equals("PUBLICO"))
                            {
                                actual.addGrupo(nuevoGrupo.getId());
                                BorrarDatos(codigoPu,visibilidadOriginal);
                            }
                            else if(visibilidadOriginal.equals("PRIVADO"))
                            {
                                actual.addGrupo(nuevoGrupo.getId());
                                BorrarDatos(codigoP,visibilidadOriginal);
                            }
                            finish();
                            break;
                    }

                }
            }
        });
        comentarioNuevoBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comentarioNuevo.getText().toString().length() != 0) {
                  /*  Datos datos = new Datos(getApplicationContext());
                    Usuario u = null;
                    try {
                        u = datos.getUsuario();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    if(visibilidadOriginal.equals("PUBLICO"))
                    {
                        FirebaseDatabase.getInstance().getReference("Publico").child(codigoPu.getID())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                CodigoPublico c = snapshot.getValue(CodigoPublico.class);
                                c.addComentario(new Comentario(actual.getNombre(),
                                        actual.getId(),
                                        comentarioNuevo.getText().toString().trim()));
                                codigoPu.setComentarios(c.getComentarios());
                                reference.child("Publico").child(codigoPu.getID()).setValue(codigoPu);
                                setComentarios(codigoPu.getComentarios());
                                sinComentarios.setVisibility(View.GONE);
                                comentarioNuevo.setText("");
                                comentarioNuevo.clearFocus();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                       /* codigoPu.addComentario(new Comentario(actual.getNombre(),
                                actual.getId(),
                                comentarioNuevo.getText().toString().trim()));
                        reference.child("Publico").child(codigoPu.getID()).setValue(codigoPu);
                        setComentarios(codigoPu.getComentarios());*/
                    }

                    else if(visibilidadOriginal.equals("GRUPO"))
                    {
                        FirebaseDatabase.getInstance().getReference("Grupo").child(grupo.getId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Grupo g = snapshot.getValue(Grupo.class);
                                g.addComentario(new Comentario(actual.getNombre(),
                                        actual.getId(),
                                        comentarioNuevo.getText().toString().trim()));
                                grupo.setComentarios(g.getComentarios());
                                reference.child("Grupos").child(grupo.getId()).setValue(grupo);
                                setComentarios(grupo.getComentarios());
                                sinComentarios.setVisibility(View.GONE);
                                comentarioNuevo.setText("");
                                comentarioNuevo.clearFocus();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }


                    esconderTeclado();
                }
            }
        });
        editarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Publicacion.this, GrupoDialog.class);
                startActivity(i);
            }
        });
    }
    //true si hay cambios
    public boolean HayCambios(Object o, String text)
    {
        switch (text)
        {
            case "PRIVADO":
                CodigoPrivado c = (CodigoPrivado) o;
                return !(c.getTitulo().equals(titulo.getText().toString())
                        && c.getCodigo().equals(codigo.getText().toString()));
            case "PUBLICO":
                CodigoPublico p = (CodigoPublico) o;
                return !(p.getTitulo().equals(titulo.getText().toString())
                        && p.getCodigo().equals(codigo.getText().toString())
                        && p.getDescripcion().equals(descripcion.getText().toString()));
            case "GRUPO":
                Grupo g = (Grupo) o;
                return !(g.getTitulo().equals(titulo.getText().toString())
                        && g.getCodigo().equals(codigo.getText().toString())
                        && g.getDescripcion().equals(descripcion.getText().toString()));
        }
        return false;
    }


    @Override
    public void onButtonClickedTipo(String text) {
        if(visibilidadActual==null)
            viewManager(text);

        if(!visibilidadActual.equals(text))
        {
            viewManager(text);
            guardar.setVisibility(View.VISIBLE);
            if (!text.equals("PRIVADO")) {
                descripcion.setBackground(getDrawable(R.drawable.text_editable));
                descripcion.setFocusable(true);
                descripcion.setFocusableInTouchMode(true);
            }
            else {
                descripcion.setFocusable(false);
                descripcion.setFocusableInTouchMode(false);
                code.setVisibility(View.VISIBLE);
                lugar.setVisibility(View.VISIBLE);

            }
            //si cambiamos la visibilidad para crear un grupo nuevo
            if(text.equals("GRUPO") && !visibilidadOriginal.equals("GRUPO"))
            {
                try {
                    Datos da = new Datos(Publicacion.this);
                    da.encriptar("grupo", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                miembrosLayout.setVisibility(View.VISIBLE);
                ArrayList<String> l = new ArrayList<>();
                l.add(actual.getNombre());
                setMiembros(l);
            }
        }

    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private void viewManager(String text)
    {
        if(text.equals("PRIVADO"))
        {
            descripcion.setVisibility(View.GONE);
            descripcionText.setVisibility(View.GONE);
            comentarioNuevoBoton.setVisibility(View.GONE);
            comentarioNuevo.setVisibility(View.GONE);
            comentariosText.setVisibility(View.GONE);
            comentariosView.setVisibility(View.GONE);
            gente.setVisibility(View.GONE);
            stats.setVisibility(View.GONE);
            guardar.setVisibility(View.GONE);
            sinComentarios.setVisibility(View.GONE);
            miembrosLayout.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            valorar.setVisibility(View.GONE);
            visibilidadActual ="PRIVADO";
            lugar.setImageDrawable(getDrawable(R.drawable.lock_closed_white));
        }
        else
        {
            descripcion.setVisibility(View.VISIBLE);
            descripcionText.setVisibility(View.VISIBLE);
            miembrosLayout.setVisibility(View.GONE);
            if(!this.visibilidadOriginal.equals("PRIVADO")) {
                comentarioNuevoBoton.setVisibility(View.VISIBLE);
                comentarioNuevo.setVisibility(View.VISIBLE);
                comentariosText.setVisibility(View.VISIBLE);
                comentariosView.setVisibility(View.VISIBLE);
            }
            if(text.equals("PUBLICO")) {
                visibilidadActual = "PUBLICO";
                lugar.setImageDrawable(getDrawable(R.drawable.lock_open_white));
                gente.setVisibility(View.GONE);
                if(visibilidadOriginal.equals("PUBLICO")) {
                    valorar.setVisibility(View.VISIBLE);
                    ratingBar.setVisibility(View.VISIBLE);
                    //el boton stats solo se muestra si eres el autor
                    if (codigoPu.getAutor().equals(actual.getNombre()))
                        stats.setVisibility(View.VISIBLE);
                }
            }

            if(text.equals("GRUPO")) {
                visibilidadActual ="GRUPO";
                lugar.setImageDrawable(getDrawable(R.drawable.ic_people_24));
                stats.setVisibility(View.GONE);
                ratingBar.setVisibility(View.GONE);
                valorar.setVisibility(View.GONE);
                if(visibilidadOriginal.equals("GRUPO")) {
                    gente.setVisibility(View.VISIBLE);
                    miembrosLayout.setVisibility(View.GONE);
                }
            }
            miembrosLayout.setVisibility(View.GONE);
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
    private void editarView (Boolean checked, String visibilidad)
    {
        if(checked)
        {
            if(visibilidadOriginal.equals("GRUPO"))
            {
                guardar.setVisibility(View.VISIBLE);
                codigo.setBackground(getDrawable(R.drawable.text_editable));
                if(grupo.getDueño().equals(actual.getNombre())) {
                    code.setVisibility(View.VISIBLE);
                    lugar.setVisibility(View.VISIBLE);
                    titulo.setBackground(getDrawable(R.drawable.text_editable));
                    descripcion.setBackground(getDrawable(R.drawable.text_editable));
                }
            }
            else {
                code.setVisibility(View.VISIBLE);
                lugar.setVisibility(View.VISIBLE);
                guardar.setVisibility(View.VISIBLE);
                titulo.setBackground(getDrawable(R.drawable.text_editable));
                codigo.setBackground(getDrawable(R.drawable.text_editable));
                if (visibilidad.equals("PUBLICO"))
                {
                    descripcion.setBackground(getDrawable(R.drawable.text_editable));
                }

            }
        }
        else
        {
            viewManager(visibilidad);
            code.setVisibility(View.GONE);
            lugar.setVisibility(View.GONE);
            guardar.setVisibility(View.GONE);
            titulo.setBackground(getDrawable(R.drawable.white));
            codigo.setBackground(getDrawable(R.drawable.white));
            if (visibilidad.equals("PUBLICO") || visibilidad.equals("GRUPO"))
                descripcion.setBackground(getDrawable(R.drawable.white));
            else {
                descripcion.setVisibility(View.GONE);
            }
        }

        if(visibilidadOriginal.equals("GRUPO"))
        {
            codigo.setFocusableInTouchMode(checked);
            codigo.setFocusable(checked);
            if(grupo.getDueño().equals(actual.getNombre())) {
                titulo.setFocusableInTouchMode(checked);
                titulo.setFocusable(checked);
                descripcion.setFocusableInTouchMode(checked);
                descripcion.setFocusable(checked);
            }
        }
        else {
            titulo.setFocusableInTouchMode(checked);
            titulo.setFocusable(checked);
            codigo.setFocusableInTouchMode(checked);
            codigo.setFocusable(checked);
            if(visibilidad.equals("PUBLICO")) {
                descripcion.setFocusableInTouchMode(checked);
                descripcion.setFocusable(checked);
            }
        }
    }
    private void esconderTeclado()
    {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(editar.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }
    private void setDatos(String titulo,String codigo,String des,String lenguaje)
    {
        this.descripcion.setText(des);
        this.titulo.setText(titulo);
        this.codigo.setText(codigo);
        onButtonClickedCode(lenguaje);
    }
    private void setComentarios(ArrayList<Comentario> c)
    {
        if(c.isEmpty())
            sinComentarios.setVisibility(View.VISIBLE);
        else {
            comentariosView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
            adapterComentarios = new AdapterComentarios(c);
            comentariosView.setAdapter(adapterComentarios);
        }
    }
    private void setMiembros(ArrayList<String> list)
    {
        miembros.setLayoutManager(new GridLayoutManager(Publicacion.this, 3));
        AdapterUser adapter = new AdapterUser(list,actual.getNombre());
        miembros.setAdapter(adapter);
        adapterInterface(adapter);

    }
    public void Estadisticas(Context contexto, CodigoPublico codigo) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.estadisticas, null);
        TextView visualizaciones,descargas;
        visualizaciones = customLayout.findViewById(R.id.visualizaciones);
        descargas = customLayout.findViewById(R.id.descargas);
        visualizaciones.setText(String.valueOf(codigo.getVisualizaciones()));
        descargas.setText(String.valueOf(codigo.getDescargas()));
        builder.setView(customLayout);
        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void BorrarDatos(Object o , String tipo)
    {
        switch (tipo)
        {
            case "PRIVADO":
                CodigoPrivado p = (CodigoPrivado) o;
                actual.removeCodigoPrivado(p);
                break;
            case "PUBLICO":
                CodigoPublico cp = (CodigoPublico) o;
                actual.removeCodigoPublico(cp.getID());
                FirebaseDatabase.getInstance().getReference("Publico").child(cp.getID()).removeValue();
                break;
            case "GRUPO":
                final Grupo g = (Grupo) o;
                actual.removeGrupo(g.getId());
                FirebaseDatabase.getInstance().getReference("Grupos").child(g.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference("NombreUsuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final ArrayList<String> miembrosConfirmados = new ArrayList<>();
                        final ArrayList<String> miembrosPendientes = new ArrayList<>();
                        for(String s : g.getMiembrosConfirmados())
                            miembrosConfirmados.add(snapshot.child(s).getChildren().iterator().next().getValue(String.class));

                        for(String s : g.getMiembrosPendientes())
                            miembrosPendientes.add(snapshot.child(s).getChildren().iterator().next().getValue(String.class));
                        FirebaseDatabase.getInstance().getReference("Usuarios")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(String s : miembrosConfirmados) {
                                            Usuario u = snapshot.child(s).getValue(Usuario.class);
                                            u.removeGrupo(g.getId());
                                            FirebaseDatabase.getInstance().getReference("Usuarios").child(u.getId()).setValue(u);
                                        }
                                        for(String s : miembrosPendientes) {
                                            Usuario u = snapshot.child(s).getValue(Usuario.class);
                                            u.removePeticion(g.getId());
                                            FirebaseDatabase.getInstance().getReference("Usuarios").child(u.getId()).setValue(u);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                        {

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
        }
        FirebaseDatabase.getInstance().getReference("Usuarios").child(actual.getId()).setValue(actual);
        try {
            Datos datos = new Datos(Publicacion.this);
            datos.setUsuario(actual);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void adapterInterface(final AdapterUser adapter)
    {
        adapter.setOnItemClickListerner(new AdapterUser.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick(int position) {
                miembrosGrupo.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.onResume();

        String auxGrupo = "";
        try {
            Datos datoss = new Datos(Publicacion.this);
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
        if(!miembrosGrupo.contains(actual.getNombre()))
            miembrosGrupo.add(actual.getNombre());

        miembros.setLayoutManager(new GridLayoutManager(Publicacion.this,3));
        AdapterUser adapter = new AdapterUser(miembrosGrupo,actual.getNombre());
        miembros.setAdapter(adapter);
        adapterInterface(adapter);
    }
}