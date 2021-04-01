package com.cgs.vision.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
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

public class ExplorarFragment extends Fragment {

    private RecyclerView recycler;
    private DatabaseReference reference,referenceUser;
    private CodigoPublico codigo;
    private ArrayList<CodigoPublico> listaCodigos;
    private Usuario actual;
    private Bundle bundle;
    private AdapterDatosPublicos adapter;
    private SwipeRefreshLayout swipe;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_explorar, container, false);
        final TextView sinResultados = root.findViewById(R.id.sinResultadosExplorar);
        recycler = root.findViewById(R.id.recyclerExplorar);
        swipe = root.findViewById(R.id.swipeExplorar);

        setHasOptionsMenu(true);
        final DialogCargando cargando = new DialogCargando(getActivity());

        reference = FirebaseDatabase.getInstance().getReference("Publico");
        referenceUser = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        listaCodigos = new ArrayList<>();

        Datos d = new Datos(getContext());
        try {
            actual = d.getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(listaCodigos.isEmpty()) {
            cargando.startCargando();
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot c : dataSnapshot.getChildren()) {
                        codigo = c.getValue(CodigoPublico.class);
                        if (!codigo.getAutor().equals(actual.getNombre())) {
                            if (!listaCodigos.contains(codigo))
                                listaCodigos.add(codigo);
                        }
                    }
                    recycler.setLayoutManager(new GridLayoutManager(getContext(), 1));
                    adapter = new AdapterDatosPublicos(listaCodigos, actual,getContext());
                    recycler.setAdapter(adapter);
                    adapterInterface(adapter);
                    cargando.stopCargando();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    DialogTxt alert = new DialogTxt();
                    cargando.stopCargando();
                    alert.msg(getString(R.string.error_unexpected), getContext());
                }
            });
        }
        else
        {
            recycler.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            adapter = new AdapterDatosPublicos(listaCodigos, actual,getContext());
            recycler.setAdapter(adapter);
           // cargando.stopCargando();
            adapterInterface(adapter);

        }
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listaCodigos.clear();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot c : dataSnapshot.getChildren()) {
                            codigo = c.getValue(CodigoPublico.class);
                            if (!codigo.getAutor().equals(actual.getNombre())) {
                                if (!listaCodigos.contains(codigo))
                                    listaCodigos.add(codigo);
                            }
                        }
                        recycler.setLayoutManager(new GridLayoutManager(getContext(), 1));
                        adapter = new AdapterDatosPublicos(listaCodigos, actual, getContext());
                     //   recycler.setAdapter(adapter);
                        swipe.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        swipe.setRefreshing(false);
                        DialogTxt alert = new DialogTxt();
                        alert.msg(getString(R.string.error_unexpected), getContext());

                    }
                });
            }
        });

        return root;
    }

    private void adapterInterface(AdapterDatosPublicos adapter)
    {
        adapter.setOnItemClickListerner(new AdapterDatosPublicos.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View titulo,View desc) {
                bundle = new Bundle();
                bundle.putString("Id", listaCodigos.get(position).getID());
              //  NavHostFragment.findNavController(ExplorarFragment.this)
               //         .navigate(R.id.action_nav_slideshow_to_codigoPublicoFragment, bundle);
                Intent i = new Intent(getContext(), Publicacion.class);
                i.putExtra("Tipo","PUBLICO");
                i.putExtra("Id", new Gson().toJson(listaCodigos.get(position)));
                Pair<View,String> p1 = Pair.create((View)titulo,"tituloPrivado");
                Pair<View,String> p2 = Pair.create((View)desc,"descripcion");


                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), p1,p2);
                startActivity(i, options.toBundle());
            }

            @Override
            public void onDeleteClick(int position) {

            }

            @Override
            public void onCheckChange(final int position, final boolean isChecked) {
                Boolean actualizar = true;
                if (isChecked) {
                    actualizar = actual.addFavorito(listaCodigos.get(position).getID());
                } else {
                    actualizar = actual.removeFavorito(listaCodigos.get(position).getID());
                }
                if (actualizar) {
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
                Datos datos = new Datos(getContext());
                if (datos.checkPer(Manifest.permission.WRITE_EXTERNAL_STORAGE, getContext())) {
                    File f1;
                    if (actual.getNombre().equals(listaCodigos.get(position).getAutor())) {
                        f1 = Environment.getExternalStoragePublicDirectory("/vision/" + actual.getNombre() + "/Públicos" + "/" + listaCodigos.get(position).getTipo());
                    } else {
                        f1 = Environment.getExternalStoragePublicDirectory("/vision/" + "Públicos" + "/" + listaCodigos.get(position).getTipo());
                    }

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
                        alert.msg(getString(R.string.alert_file_saved), getContext());
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        item.setVisible(true);

        SearchView search = (SearchView) item.getActionView();

        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    NavHostFragment.findNavController(ExplorarFragment.this)
                            .navigate(R.id.action_nav_explorar_to_filtros);
                }
                else
                    recycler.setVisibility(View.VISIBLE);

            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recycler.setVisibility(View.VISIBLE);
                ArrayList<CodigoPublico> nuevaLista = new ArrayList<>();
                for(CodigoPublico c : listaCodigos)
                {
                    if(c.getTitulo().contains(query.toLowerCase()) || c.getTitulo().contains(query.toUpperCase()))
                    {
                        nuevaLista.add(c);
                    }
                }
                adapter = new AdapterDatosPublicos(nuevaLista,actual, getContext());
                recycler.setAdapter(adapter);
                return  false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recycler.setVisibility(View.GONE);
                return false;
            }
        });
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter = new AdapterDatosPublicos(listaCodigos,actual, getActivity());

                recycler.setAdapter(adapter);
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
