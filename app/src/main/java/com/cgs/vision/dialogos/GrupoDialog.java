package com.cgs.vision.dialogos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.cgs.vision.Datos;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;
import com.cgs.vision.adaptadores.AdapterUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class GrupoDialog extends AppCompatActivity {

    private TextInputEditText name;
    private RecyclerView recycler;
    private AdapterUser adapter;
    private ArrayList<String> list,IdList;
    private Bundle bundle;
    private Usuario usuario;
    private Button cerrar,add,ok;
    private AutoCompleteTextView auto;
    private ArrayList<String> hint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_grupo);
        add = findViewById(R.id.add_group);
        recycler = findViewById(R.id.miembros);
        name = findViewById(R.id.add_name_group);
        cerrar = findViewById(R.id.cerrarGrupo);
        ok = findViewById(R.id.guardarGrupo);
        auto = findViewById(R.id.AutoCompleteTextView);
        list = new ArrayList<>();
        IdList = new ArrayList<>();
        hint = new ArrayList<>();
        String listaId = "";

        Datos ad = new Datos(GrupoDialog.this);
        try {
            Datos d = new Datos(GrupoDialog.this);
            usuario = d.getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if(ad.leerEncriptado("grupo")!=null || !ad.leerEncriptado("grupo").equals(""))
            {
                String[] aux = ad.leerEncriptado("grupo").split("\n");
                list.addAll(Arrays.asList(aux));
                if(list.get(0).equals(""))
                    list.remove(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!list.isEmpty())
        {
            recycler.setLayoutManager(new GridLayoutManager(GrupoDialog.this, 3));
            adapter = new AdapterUser(list,usuario.getNombre());
            recycler.setAdapter(adapter);
            adapterInterface(adapter);
        }
        FirebaseDatabase.getInstance().getReference("NombreUsuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot s : snapshot.getChildren()) {
                    hint.add(s.getKey());
                }
                ArrayAdapter<String> a = new ArrayAdapter<String>(GrupoDialog.this, android.R.layout.simple_list_item_1,hint);
                auto.setAdapter(a);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder lista = new StringBuilder();


                    FirebaseDatabase.getInstance().getReference("NombreUsuarios").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String aux;
                            StringBuilder res = new StringBuilder();
                            //generamos la lista del grupo con los ID de cada usuario
                           for(String s : list)
                           {
                               aux =  dataSnapshot.child(s).getChildren().iterator().next().getValue(String.class);
                               res.append(aux).append("\n");
                           }
                            Datos dat = new Datos(GrupoDialog.this);
                            try {
                                dat.encriptar("grupoId",res.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                for(String s : list) {
                    lista.append(s).append("\n");
                }

                try {
                    Datos da = new Datos(GrupoDialog.this);
                    da.encriptar("grupo", lista.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
              //  Intent i = new Intent(grupo.this,Result.class);
                finish();
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMember = Objects.requireNonNull(auto.getText()).toString().trim();



                if(usuario.getNombre().equals(newMember))
                {
                    DialogTxt a = new DialogTxt();
                    a.msg("Usted ya forma parte del grupo", GrupoDialog.this);
                }
                else {
                    final Query uQuery = FirebaseDatabase.getInstance().getReference()
                            .child("NombreUsuarios").orderByKey()
                            .equalTo(newMember);
                    uQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {

                                String user = FirebaseDatabase.getInstance().getReference().child("NombreUsuarios").child(auto.getText().toString().trim()).getKey();

                                if (!list.contains(user)) {
                                    list.add(user);
                                    recycler.setLayoutManager(new GridLayoutManager(GrupoDialog.this, 3));
                                    adapter = new AdapterUser(list,usuario.getNombre());
                                    recycler.setAdapter(adapter);
                                    adapterInterface(adapter);

                                    auto.setText("");
                                } else {
                                    DialogTxt a = new DialogTxt();
                                    a.msg(getString(R.string.error_already_group_member), GrupoDialog.this);
                                }
                                //IdList.add(usuario.getId());
                            } else {
                                DialogTxt a = new DialogTxt();
                                a.msg(getString(R.string.error_user_not_exist), GrupoDialog.this);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
    private void adapterInterface(final AdapterUser adapter)
    {
        adapter.setOnItemClickListerner(new AdapterUser.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick(final int position) {
                final androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(GrupoDialog.this);
                mensaje.setMessage(getString(R.string.alert_delete_user_from_group));
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
            }
        });
    }


}