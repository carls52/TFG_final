package com.cgs.vision.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.cgs.vision.CodigoPublico;
import com.cgs.vision.Datos;
import com.cgs.vision.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class Ajustes extends Fragment {
    private Button cambiarPass,cambiarMail,borrarCuenta;
    private DatabaseReference reference,referenceUser;
    private SwitchMaterial sangradoInteligente,mostrarPro;
    private Spinner idiomas;


    public Ajustes() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_ajustes, container, false);
        idiomas = root.findViewById(R.id.idiomas);
        sangradoInteligente = root.findViewById(R.id.sangradoInteligente);
        mostrarPro = root.findViewById(R.id.mostrarPro);
        cambiarPass = root.findViewById(R.id.cambiarPass);
        cambiarMail = root.findViewById(R.id.cambiarMail);
        borrarCuenta = root.findViewById(R.id.borrarCuenta);

        final Datos datos = new Datos(getActivity());
        final String idioma = datos.getAjustes("idioma");
        String sangrado = datos.getAjustes("Sangrado");
        String mostrar = datos.getAjustes("Mostar");

        //inicializamos los valores guardados
        if(mostrar.equals("NO"))
            mostrarPro.setChecked(false);
        else
        {
            mostrarPro.setChecked(true);
                if(mostrar.equals(""))
                    datos.setAjustes("Mostrar", "SI");
        }

        if(sangrado.equals("NO"))
            sangradoInteligente.setChecked(false);
        else
        {
            sangradoInteligente.setChecked(true);
            if(sangrado.equals(""))
                datos.setAjustes("Sangrado", "SI");
        }

        //Inicializamos los listeners
        mostrarPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Datos datos = new Datos(getContext());
                if(mostrarPro.isChecked())
                    datos.setAjustes("Mostrar","SI");
                else
                    datos.setAjustes("Mostrar","NO");
            }
        });
        sangradoInteligente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    datos.setAjustes("Sangrado","SI");
                else
                    datos.setAjustes("Sangrado","NO");
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.idiomas,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idiomas.setAdapter(adapter);

        if(idioma.equals("en"))
            idiomas.setSelection(1);
        else
            idiomas.setSelection(0);

        idiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Configuration configuration = getResources().getConfiguration();
                if(position==0 && !(idioma.equals("es"))) //ESPAÑOL
                {
                    configuration.setLocale( Locale.forLanguageTag("es-ES"));
                    getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
                    Intent i = new Intent(getContext(),Login.class);
                    startActivity(i);
                }
                else if(position==1 && !(idioma.equals("es"))) //INGLES
                {
                    configuration.setLocale(Locale.ENGLISH);
                    getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
                    Intent i = new Intent(getContext(),Login.class);
                    startActivity(i);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cambiarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("tipo","contraseña");
                NavHostFragment.findNavController(Ajustes.this)
                        .navigate(R.id.action_ajustes2_to_cambios,bundle);
            }
        });

        cambiarMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("tipo","correo");
                NavHostFragment.findNavController(Ajustes.this)
                        .navigate(R.id.action_ajustes2_to_cambios,bundle);
            }
        });

        borrarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(getContext());
                mensaje.setMessage(getString(R.string.alert_delete_account));
                mensaje.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        referenceUser = FirebaseDatabase.getInstance().getReference("Usuarios")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        referenceUser.removeValue();

                        reference = FirebaseDatabase.getInstance().getReference("Publico");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot d : dataSnapshot.getChildren())
                                {
                                    CodigoPublico codigo = d.getValue(CodigoPublico.class);
                                    if(codigo.getIdAutor()
                                            .equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                    {
                                        reference.child(codigo.getID()).removeValue();
                                    }
                                }
                                Intent i = new Intent(getContext(), Login.class);
                                getActivity().finish();
                                startActivity(i);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

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



        return root;
    }
}
