package com.cgs.vision.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cgs.vision.adaptadores.AdapterDatosPublicos;
import com.cgs.vision.dialogos.DialogTxt;
import com.cgs.vision.CodigoPublico;
import com.cgs.vision.Datos;
import com.cgs.vision.dialogos.DialogCargando;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class MisCPublicosFragment extends Fragment {


    private FirebaseAuth auth;
    private DatabaseReference reference, referenceUser;
    private CodigoPublico codigo;
    private String ID;
    private RecyclerView recycler;
    private TextView sinResultados;
    private ArrayList<CodigoPublico> listaCodigos;
    private Usuario actual;
    private AdapterDatosPublicos adapter;
    private Bundle bundle;
    private FirebaseAuth Auth;
    private SwipeRefreshLayout swipe;
    private Datos datos;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_repositorio_publico, container, false);

        auth = FirebaseAuth.getInstance();

        listaCodigos = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Publico");
        recycler = root.findViewById(R.id.recyclerPublico);
        sinResultados = root.findViewById(R.id.sinResultadosPublico);
        Auth = FirebaseAuth.getInstance();
        swipe = root.findViewById(R.id.swipePublico);
        try {
            actual = new Datos(getContext()).getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final DialogCargando cargando = new DialogCargando(getActivity());
        cargando.startCargando();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                listaCodigos.clear();
                for (String id : actual.getCodigosPublicos())
                    listaCodigos.add(dataSnapshot.child(id).getValue(CodigoPublico.class));

                if (listaCodigos.size() == 0) {
                    sinResultados.setVisibility(View.VISIBLE);
                    sinResultados.setText("Sin resultados");
                }
                recycler.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                adapter = new AdapterDatosPublicos(listaCodigos, actual, getContext());
                recycler.setAdapter(adapter);
                cargando.stopCargando();
                adapterInterface(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                DialogTxt alert = new DialogTxt();
                cargando.stopCargando();
                alert.msg("Ha ocurrido un error inesperado inténtelo más tarde", getContext());
            }
        });
      /*  else {
            adapter = new AdapterDatosPublic(listaAux, actual);
            recycler.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            cargando.stopCargando();
            recycler.setAdapter(adapter);
            adapterInterface(adapter);
        }*/
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        listaCodigos.clear();
                        listaCodigos.clear();
                        for (String id : actual.getCodigosPublicos())
                            listaCodigos.add(dataSnapshot.child(id).getValue(CodigoPublico.class));

                        if (listaCodigos.size() == 0) {
                            sinResultados.setVisibility(View.VISIBLE);
                            sinResultados.setText("Sin resultados");
                        }
                        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                        adapter = new AdapterDatosPublicos(listaCodigos, actual, getContext());
                        recycler.setAdapter(adapter);
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
        return root;
    }
    private void adapterInterface(final AdapterDatosPublicos adapter) {
        adapter.setOnItemClickListerner(new AdapterDatosPublicos.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View titulo, View desc) {
                // bundle = new Bundle();
                // bundle.putString("Id", listaAux.get(position).getID());
                //  bundle.putString("Id",new Gson().toJson(listaAux.get(position)));

                //     NavHostFragment.findNavController(MisCPublicosFragment.this)
                //            .navigate(R.id.action_nav_gallery_to_codigoPublicoFragment, bundle);
                Intent i = new Intent(getContext(), Publicacion.class);
                i.putExtra("Tipo","PUBLICO");
                i.putExtra("Id", new Gson().toJson(listaCodigos.get(position)));
                Pair<View,String> p1 = Pair.create((View)titulo,"tituloPrivado");
                Pair<View,String> p2 = Pair.create((View)desc,"descripcion");

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), p1, p2);
                startActivity(i, options.toBundle());
                // startActivity(i);
            }

            @Override
            public void onDeleteClick(final int position) {

                final androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(getContext());
                mensaje.setMessage("¿De verdad quieres borrarlo?");
                mensaje.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CodigoPublico c = listaCodigos.get(position);
                        listaCodigos.remove(position);
                        adapter.notifyItemRemoved(position);
                        reference.child(c.getID()).removeValue();
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

            @Override
            public void onCheckChange(int position, boolean isChecked) {
                Boolean actualizar = true;
                if (isChecked) {
                    actualizar = actual.addFavorito(listaCodigos.get(position).getID());
                } else {
                    actualizar = actual.removeFavorito(listaCodigos.get(position).getID());
                    listaCodigos.remove(position);
                    adapter.notifyItemRemoved(position);
                }
                if (actualizar) {
                    referenceUser = FirebaseDatabase.getInstance()
                            .getReference("Usuarios")
                            .child(Auth.getCurrentUser().getUid());
                    referenceUser.removeValue();
                    referenceUser.setValue(actual);
                    Datos d = new Datos(getContext());
                    Gson gson = new Gson();
                    String json = gson.toJson(actual);
                    try {
                        d.encriptar("Usuario", json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onDownloadClick(int position) {
                Datos d = new Datos(getContext());
                if (d.checkPer(Manifest.permission.WRITE_EXTERNAL_STORAGE, getContext())) {
                    File f1 = Environment.getExternalStoragePublicDirectory("/vision/" + actual.getNombre() + "/Públicos/" + listaCodigos.get(position).getTipo());
                    String extension = null;
                    switch (listaCodigos.get(position).getTipo()) {

                        case "JAVA":
                            extension = ".java";
                            break;
                        case "PYTHON":
                            extension = ".py";
                            break;
                        case "C":
                            extension = ".c";
                            break;
                        case "C++":
                            extension = ".cpp";
                            break;
                        case "JAVASCRIPT":
                            extension = ".js";
                            break;
                        default:
                            extension = ".txt";
                            break;
                    }

                    File dir = new File(f1, listaCodigos.get(position).getTitulo() + extension);
                    f1.mkdirs();
                    try {
                        FileWriter filewriter = new FileWriter(dir, false);
                        BufferedWriter out = new BufferedWriter(filewriter);
                        out.write(listaCodigos.get(position).getCodigo());
                        out.close();
                        DialogTxt alert = new DialogTxt();
                        alert.msg("Archivo guardado", getContext());
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getActivity(), "NOT FOUND", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();

        }

    @Override
    public void onResume() {
        super.onResume();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Datos datos = new Datos(getContext());
                try {
                    actual = datos.getUsuario();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listaCodigos.clear();
                for (String s : actual.getCodigosPublicos()) {
                    listaCodigos.add(snapshot.child(s).getValue(CodigoPublico.class));
                }
                if(listaCodigos.isEmpty())
                {
                    sinResultados.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
                else
                {
                recycler.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                adapter = new AdapterDatosPublicos(listaCodigos, actual, getContext());
                recycler.setAdapter(adapter);
                adapterInterface(adapter);
                sinResultados.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

