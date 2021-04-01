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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cgs.vision.CodigoPrivado;
import com.cgs.vision.Datos;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;
import com.cgs.vision.adaptadores.AdapterDatosPrivados;
import com.cgs.vision.dialogos.DialogTxt;
import com.cgs.vision.dialogos.DialogCargando;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
public class Repositorio extends Fragment {

    private String tipo;

    private FirebaseAuth auth ;
    private FirebaseDatabase bbdd = FirebaseDatabase.getInstance();
    private DatabaseReference reference;
    private ArrayList<String> l;
    private RecyclerView recycler;
    private Usuario user;
    private String ID;
    private TextView error;
    private ArrayList<CodigoPrivado> listaAux;
    private Bundle bundle;
    private Usuario actual;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        //SharedElementTransition();

        auth = FirebaseAuth.getInstance();
        ID = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(ID);

        View root = inflater.inflate(R.layout.fragment_first2, container, false);

        tipo = getArguments().getString("tipo");
        recycler = root.findViewById(R.id.recycler);
        error = root.findViewById(R.id.sinResultados);



        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        final DialogCargando cargando = new DialogCargando(getActivity());
        cargando.startCargando();

        listaAux = new ArrayList<>();

        final Datos datos = new Datos(getContext());
        try {
            actual = datos.getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(actual.getCodigosPrivados()!=null) {
            for (CodigoPrivado c : actual.getCodigosPrivados()) {
                if (c.getTipo().equals(tipo) && c.getTipo() != null) {
                    listaAux.add(c);
                }
            }
        }
        recycler.setLayoutManager(new GridLayoutManager(getActivity(),1));
        final AdapterDatosPrivados adapter = new AdapterDatosPrivados(listaAux,getContext());
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListerner(new AdapterDatosPrivados.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View titulo) {
                Intent i = new Intent(getContext(), Publicacion.class);
                int aux = actual.getCodigosPrivados().indexOf(actual.getCodigoTipo(tipo).get(position));
                i.putExtra("Posicion",String.valueOf(aux));
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
                        CodigoPrivado c = listaAux.get(position);
                        Datos datos = new Datos(getContext());
                        actual.getCodigosPrivados().remove(c);
                        listaAux.remove(position);
                        adapter.notifyItemRemoved(position);
                        reference.removeValue();
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
                    File f1 = Environment.getExternalStoragePublicDirectory("/vision/"+actual.getNombre()+"/"+tipo);
                    String extension = null;
                    switch (listaAux.get(position).getTipo()) {

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

                    File dir = new File(f1 , listaAux.get(position).getTitulo()+extension);
                    f1.mkdirs();
                    try {
                            FileWriter filewriter = new FileWriter(dir, false);
                            BufferedWriter out = new BufferedWriter(filewriter);
                            out.write(listaAux.get(position).getCodigo());
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
        if(listaAux.isEmpty())
        {
            error.setVisibility(View.VISIBLE);
        }
        cargando.stopCargando();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}
