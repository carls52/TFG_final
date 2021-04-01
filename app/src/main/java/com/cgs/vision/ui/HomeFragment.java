package com.cgs.vision.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cgs.vision.adaptadores.AdapterDatosPrivados;
import com.cgs.vision.dialogos.DialogTxt;
import com.cgs.vision.CodigoPrivado;
import com.cgs.vision.Datos;
import com.cgs.vision.R;
import com.cgs.vision.dialogos.SelectDialog;
import com.cgs.vision.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


public class HomeFragment extends Fragment {

    private Bundle bundle;
    private FloatingActionButton fab;
    private Button java,c,cplus,javaScript,python,otros;
    private RecyclerView listaView;
    private AdapterDatosPrivados adapter;
    private Usuario actual;
    private Datos datos;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        datos = new Datos(getContext());
        try {
            actual = datos.getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }

        View root = inflater.inflate(R.layout.fragment_home_online, container, false);

        java = root.findViewById(R.id.buttonJava);
        javaScript = root.findViewById(R.id.buttonJavaScript);
        c = root.findViewById(R.id.buttonC);
        cplus = root.findViewById(R.id.buttonCplus);
        python = root.findViewById(R.id.buttonPython);
        otros = root.findViewById(R.id.buttonOtros);
        listaView = root.findViewById(R.id.lista);

        fab = root.findViewById(R.id.foto);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(getContext(), SelectDialog.class);
               startActivity(i);
            }
        });
        bundle = new Bundle();
        java.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("tipo","JAVA");
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_nav_home_to_first2Fragment,bundle);
            }
        });
        python.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("tipo","PYTHON");
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_nav_home_to_first2Fragment,bundle);
            }
        });
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("tipo","C");
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_nav_home_to_first2Fragment,bundle);
            }
        });
        cplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("tipo","C++");
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_nav_home_to_first2Fragment,bundle);
            }
        });
        javaScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("tipo","JAVASCRIPT");
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_nav_home_to_first2Fragment,bundle);
            }
        });
        otros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("tipo","TXT");
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_nav_home_to_first2Fragment,bundle);
            }
        });

        if(actual==null) {
            FirebaseDatabase.getInstance().getReference("Usuarios").child(FirebaseAuth.getInstance().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            actual = snapshot.getValue(Usuario.class);

                            try {
                                datos.setUsuario(actual);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            adapter = new AdapterDatosPrivados(actual.getCodigosPrivados(), getContext());
                            listaView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                            listaView.setAdapter(adapter);
                            adapterInterface(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        else
        {
            adapter = new AdapterDatosPrivados(actual.getCodigosPrivados(), getContext());
            listaView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            listaView.setAdapter(adapter);
            adapterInterface(adapter);
        }
        return root;
    }
    private void adapterInterface(final AdapterDatosPrivados adapter)
    {
        adapter.setOnItemClickListerner(new AdapterDatosPrivados.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View titulo) {
                Intent i = new Intent(getContext(), Publicacion.class);
                i.putExtra("Posicion",String.valueOf(position));
                i.putExtra("Tipo","PRIVADO");
                Pair<View,String> p1 = Pair.create((View)titulo,"tituloPrivado");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(),titulo,"tituloPrivado");
                startActivity(i, options.toBundle());
            }

            @Override
            public void onDeleteClick(final int position) {
                final androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(getContext());

                mensaje.setMessage(getString(R.string.alert_delete_ask));
                mensaje.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(actual.getId());
                        CodigoPrivado c = actual.getCodigosPrivados().get(position);
                        Datos datos = new Datos(getContext());
                        actual.removeCodigoPrivado(position);
                        adapter.notifyItemRemoved(position);
                        reference.setValue(actual);
                        try {
                            datos.setUsuario(actual);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
            public void onDownloadClick(int position) throws FileNotFoundException {
                if(datos.checkPer(Manifest.permission.WRITE_EXTERNAL_STORAGE,getContext())) {
                    File f1 = Environment.getExternalStoragePublicDirectory("/vision/"+actual.getNombre()+"/"+actual.getCodigosPrivados().get(position).getTipo());
                    String extension = null;
                    switch (actual.getCodigosPrivados().get(position).getTipo()) {

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

                    File dir = new File(f1 , actual.getCodigosPrivados().get(position).getTitulo()+extension);
                    f1.mkdirs();
                    try {
                        FileWriter filewriter = new FileWriter(dir, false);
                        BufferedWriter out = new BufferedWriter(filewriter);
                        out.write(actual.getCodigosPrivados().get(position).getCodigo());
                        out.close();
                        DialogTxt alert = new DialogTxt();
                        alert.msg(getString(R.string.alert_file_saved),getContext());
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getActivity(), "NOT FOUND", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.app_bar_view);
        item.setIcon(R.drawable.toggle_view);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.isChecked())
                {
                    item.setChecked(false);
                    item.setIcon(R.drawable.ic_baseline_view_list);
                    java.setVisibility(View.VISIBLE);
                    javaScript.setVisibility(View.VISIBLE);
                    python.setVisibility(View.VISIBLE);
                    otros.setVisibility(View.VISIBLE);
                    c.setVisibility(View.VISIBLE);
                    cplus.setVisibility(View.VISIBLE);
                    listaView.setVisibility(View.GONE);
                }
                else
                {
                    item.setChecked(true);
                    item.setIcon(R.drawable.ic_baseline_grid);
                    java.setVisibility(View.GONE);
                    javaScript.setVisibility(View.GONE);
                    python.setVisibility(View.GONE);
                    otros.setVisibility(View.GONE);
                    c.setVisibility(View.GONE);
                    cplus.setVisibility(View.GONE);
                    listaView.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        item.setVisible(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        datos = new Datos(getContext());
        try {
            actual = datos.getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(actual!=null) {
            adapter = new AdapterDatosPrivados(actual.getCodigosPrivados(), getContext());
            listaView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            listaView.setAdapter(adapter);
            adapterInterface(adapter);
        }
    }
}
