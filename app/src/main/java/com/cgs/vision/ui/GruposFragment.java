package com.cgs.vision.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cgs.vision.adaptadores.AdapterGrupo;
import com.cgs.vision.adaptadores.AdapterPeticion;
import com.cgs.vision.dialogos.DialogTxt;
import com.cgs.vision.Datos;
import com.cgs.vision.dialogos.DialogCargando;
import com.cgs.vision.dialogos.DialogPeticiones;
import com.cgs.vision.Grupo;
//import com.example.pruebavision.GrupoMain;
import com.cgs.vision.Peticion;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;


import java.util.ArrayList;



public class GruposFragment extends Fragment {

    private SwipeRefreshLayout swipe;
    private RecyclerView grupos,peticiones;
    private DatabaseReference reference,referenceUser;
    private ArrayList<Grupo> listaGrupos;
    private ArrayList<Peticion> listaPeticiones;
    private Usuario actual;
    private Grupo grupoActual;
    private AdapterGrupo adapter;
    private AdapterPeticion adapterPeticion;
    private Grupo grupo;
    private TextView sinResultados;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_grupos, container, false);

        swipe = root.findViewById(R.id.grupos);
        sinResultados = root.findViewById(R.id.sin_resultados);
        grupos = root.findViewById(R.id.gruposView);
        //peticiones = root.findViewById(R.id.peticionesView);
        final DialogCargando cargando = new DialogCargando(getActivity());

        listaGrupos = new ArrayList<>();
        //listaPeticiones = new ArrayList<>();
        final Datos d = new Datos(getContext());
        try {
            actual = d.getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }

        reference = FirebaseDatabase.getInstance().getReference("Grupos");
        //referenceUser = FirebaseDatabase.getInstance().getReference("Usuarios")
          //      .child(actual.getId());

        if(listaGrupos !=null) {
            if (listaGrupos.isEmpty()) {
                cargando.startCargando();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaGrupos.clear();
                        try {
                            actual = d.getUsuario();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(actual.getGrupos()!=null)
                        {
                            for (String grupo : actual.getGrupos())
                                listaGrupos.add(dataSnapshot.child(grupo).getValue(Grupo.class));
                        }
                        grupos.setLayoutManager(new GridLayoutManager(getContext(), 1));
                        adapter = new AdapterGrupo(listaGrupos,getContext());
                        grupos.setAdapter(adapter);
                        adapterInterface(adapter);
                        if(listaGrupos.isEmpty())
                            sinResultados.setVisibility(View.VISIBLE);
                        cargando.stopCargando();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        DialogTxt alert = new DialogTxt();
                        cargando.stopCargando();
                        alert.msg(getString(R.string.error_unexpected), getContext());
                    }
                });
            } else {
                grupos.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                adapter = new AdapterGrupo(listaGrupos,getContext());
                grupos.setAdapter(adapter);
                // cargando.stopCargando();
                adapterInterface(adapter);
            }
        }
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        listaGrupos.clear();
                        for (String id : actual.getGrupos())
                            listaGrupos.add(dataSnapshot.child(id).getValue(Grupo.class));

                        if (listaGrupos.size() == 0) {
                            sinResultados.setVisibility(View.VISIBLE);
                            sinResultados.setText("Sin resultados");
                        }
                        grupos.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                        adapter = new AdapterGrupo(listaGrupos,getContext());
                        grupos.setAdapter(adapter);
                        cargando.stopCargando();
                        adapterInterface(adapter);
                        swipe.setRefreshing(false);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        });
     /*   if(listaPeticiones!=null)
        {
          //  cargando.startCargando();
            referenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    actual = dataSnapshot.getValue(Usuario.class);
                    Datos save = new Datos(getActivity());
                    try {
                        save.setUsuario(actual);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(actual.getPeticiones()!=null)
                        listaPeticiones.addAll(actual.getPeticiones());
                    while(listaPeticiones.remove(null));
                    peticiones.setLayoutManager(new GridLayoutManager(getContext(), 1));
                    adapterPeticion = new AdapterPeticion(listaPeticiones);
                    peticiones.setAdapter(adapterPeticion);
                    adapterInterfacePeticion(adapterPeticion);
                    cargando.stopCargando();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    AlertTxt alert = new AlertTxt();
                    cargando.stopCargando();
                    alert.msg("Ha ocurrido un error inesperado inténtelo más tarde", getContext());
                }
            });
        } else {
            peticiones.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            adapterPeticion = new AdapterPeticion(listaPeticiones);
            peticiones.setAdapter(adapterPeticion);
            cargando.stopCargando();
            adapterInterfacePeticion(adapterPeticion);
        }*/

        return root;
    }
   /* private void adapterInterfacePeticion(AdapterPeticion adapter)
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
    }*/
    private void adapterInterface(AdapterGrupo adapter)
    {
        adapter.setOnItemClickListerner(new AdapterGrupo.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            /*    Intent i = new Intent (getContext(), GrupoMain.class);
                i.putExtra("Grupo",new Gson().toJson(listaGrupos.get(position)));
                startActivity(i);*/

                Intent i = new Intent(getContext(), Publicacion.class);
                i.putExtra("Tipo","GRUPO");
                i.putExtra("Id", new Gson().toJson(listaGrupos.get(position)));
              //  Pair<View,String> p1 = Pair.create((View)titulo,"tituloPrivado");
              //  Pair<View,String> p2 = Pair.create((View)desc,"descripcion");


               /* ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), p1,p2);
                startActivity(i, options.toBundle());
                */
                startActivity(i);
            }

            @Override
            public void onDeleteClick(int position) {

            }

            @Override
            public void onCheckChange(int position, boolean isChecked) {

            }


            @Override
            public void onDownloadClick(int position) {
            }
        });
    }

    private void peticionManager (final Boolean respuesta , final String id, int position)
    {
        actual.removePeticion(id);
        listaPeticiones.remove(position);

        if(respuesta)
            actual.addGrupo(id);

        Datos datos = new Datos(getContext());
        try {
            datos.setUsuario(actual);
        } catch (Exception e) {
            e.printStackTrace();
        }
        referenceUser.setValue(actual);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                peticiones.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                adapterPeticion = new AdapterPeticion(listaPeticiones);
                peticiones.setAdapter(adapterPeticion);

                grupo = snapshot.child(id).getValue(Grupo.class);
                grupo.removeMiembroPendiente(actual.getNombre());

                if(respuesta) {
                    grupo.addMiembroConfirmado(actual.getNombre());
                    listaGrupos.add(grupo);
                }

                reference.child(id).setValue(grupo);

                grupos.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                adapter = new AdapterGrupo(listaGrupos,getContext());
                grupos.setAdapter(adapter);
                adapterInterface(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.app_bar_bell);

        if(actual.getPeticiones().isEmpty())
            item.setIcon(R.drawable.ic_baseline_notification);
        else
            item.setIcon(R.drawable.ic_baseline_notification_important_24);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(getContext(),DialogPeticiones.class);
                startActivity(i);

                return true;
            }
        });
        item.setVisible(true);

    }

    @Override
    public void onStop() {
        super.onStop();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaGrupos.clear();
                if (actual.getGrupos() != null) {
                    for (String grupo : actual.getGrupos())
                        listaGrupos.add(dataSnapshot.child(grupo).getValue(Grupo.class));
                }
                grupos.setLayoutManager(new GridLayoutManager(getContext(), 1));
                adapter = new AdapterGrupo(listaGrupos,getContext());
                grupos.setAdapter(adapter);
                adapterInterface(adapter);
                if (listaGrupos.isEmpty())
                    sinResultados.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                DialogTxt alert = new DialogTxt();
                alert.msg(getString(R.string.error_unexpected), getContext());
            }
        });
    }

}