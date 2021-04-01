package com.cgs.vision.dialogos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cgs.vision.Datos;
import com.cgs.vision.Grupo;
import com.cgs.vision.Peticion;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;
import com.cgs.vision.adaptadores.AdapterPeticion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DialogPeticiones extends AppCompatActivity {

    private RecyclerView peticiones;
    private ArrayList<Peticion> listaPeticiones;
    private Usuario actual;
    private Button ok;
    private DatabaseReference referenceUser,referenceGrupo;
    private AdapterPeticion adapterPeticion;
    private ProgressBar cargar;
    private TextView sinPeticiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_peticiones);
        peticiones = findViewById(R.id.peticiones);
        ok = findViewById(R.id.ok);
        cargar = findViewById(R.id.cargandoDialog);
        sinPeticiones = findViewById(R.id.sinPeticiones);
        listaPeticiones = new ArrayList<>();
        Datos d = new Datos(DialogPeticiones.this);
        try {
            actual = d.getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }
        referenceUser = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(actual.getId());

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(listaPeticiones!=null)
        {
            //  cargando.startCargando();
            referenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    actual = dataSnapshot.getValue(Usuario.class);
                    Datos save = new Datos(DialogPeticiones.this);
                    try {
                        save.setUsuario(actual);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(actual.getPeticiones()!=null)
                        listaPeticiones.addAll(actual.getPeticiones());
                    while(listaPeticiones.remove(null));
                    cargar.setVisibility(View.GONE);
                    if(!listaPeticiones.isEmpty()) {
                        peticiones.setLayoutManager(new GridLayoutManager(DialogPeticiones.this, 1));
                        adapterPeticion = new AdapterPeticion(listaPeticiones);
                        peticiones.setAdapter(adapterPeticion);
                        adapterInterfacePeticion(adapterPeticion);
                    }
                    else
                    {
                        sinPeticiones.setVisibility(View.VISIBLE);
                    }
                   // cargando.stopCargando();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    DialogTxt alert = new DialogTxt();
                   // cargando.stopCargando();
                    alert.msg(getString(R.string.error_unexpected), DialogPeticiones.this);
                }
            });
        } else {
            peticiones.setLayoutManager(new GridLayoutManager(DialogPeticiones.this, 1));
            adapterPeticion = new AdapterPeticion(listaPeticiones);
            peticiones.setAdapter(adapterPeticion);
            //cargando.stopCargando();
            adapterInterfacePeticion(adapterPeticion);
        }
    }
    private void adapterInterfacePeticion(AdapterPeticion adapter)
    {
        adapter.setOnItemClickListerner(new AdapterPeticion.OnItemClickListener() {
            @Override
            public void onSi(int position) {
                final String id = listaPeticiones.get(position).getID();
                peticionManager(true,id,position);
            }

            @Override
            public void onNo(int position) {
                final String id = listaPeticiones.get(position).getID();
                peticionManager(false,id,position);
            }
        });
    }
    private void peticionManager (final Boolean respuesta , final String id, final int position)
    {
        referenceGrupo = FirebaseDatabase.getInstance().getReference("Grupos")
                .child(id);
        referenceGrupo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                actual.removePeticion(id);
                if(respuesta)
                    actual.addGrupo(id);

                Datos datos = new Datos(DialogPeticiones.this);
                try {
                    datos.setUsuario(actual);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Grupo grupo = snapshot.getValue(Grupo.class);
                grupo.removeMiembroPendiente(actual.getNombre());
                grupo.addMiembroConfirmado(actual.getNombre());

                referenceGrupo.setValue(grupo);

                referenceUser.setValue(actual);
                listaPeticiones.remove(position);
                peticiones.removeViewAt(position);
                adapterPeticion.notifyItemRemoved(position);
                adapterPeticion.notifyItemRangeChanged(position,listaPeticiones.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}