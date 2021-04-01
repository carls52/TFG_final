package com.cgs.vision.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cgs.vision.Datos;
import com.cgs.vision.R;
import com.cgs.vision.dialogos.DialogTxt;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class Cambios extends Fragment {

    private String tipo;
    private Button guardar;
    private TextView nuevo,antiguo;
    private EditText nuevoTexto,antiguoTexto;
    private FirebaseAuth auth;

    public Cambios() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Cambios newInstance(String param1, String param2) {
        Cambios fragment = new Cambios();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tipo = getArguments().getString("tipo");
            auth = FirebaseAuth.getInstance();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_cambios, container, false);
        antiguo  = root.findViewById(R.id.old);
        antiguoTexto  = root.findViewById(R.id.oldText);
        nuevo = root.findViewById(R.id.newTitulo);
        nuevoTexto = root.findViewById(R.id.newText);
        guardar = root.findViewById(R.id.guardarCambios);

        switch (tipo){
            case "contraseña":
                antiguo.setText("Contraseña antigua");
                nuevo.setText("Contraseña nueva");
                break;
            case "correo":
                antiguo.setText("Correo actual");
                nuevo.setText("Correo nuevo");
        }
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogTxt alert = new DialogTxt();
                //comprobamos campos vacíos

                if(antiguoTexto.getText().toString().length() == 0 || nuevoTexto.getText().toString().length() == 0)
                {

                    alert.msg(getString(R.string.error_empty_fields),getContext());
                }
                else {
                    String guardado = "";
                    Datos datos = new Datos(getContext());
                    switch (tipo) {
                        case "correo":
                            //comprobamos que el valor antiguo coincide con el valor guardado
                            if (auth.getCurrentUser().getEmail().contentEquals(antiguoTexto.getText())) {

                                //comprobamos si el nuevo correo es un correo válido
                                if(android.util.Patterns.EMAIL_ADDRESS
                                        .matcher(nuevo.getText().toString().trim()).matches()) {
                                    auth.getCurrentUser().updateEmail(nuevoTexto.getText().toString().trim());
                                    try {
                                        datos.encriptar("correo","");
                                        datos.encriptar("contraseña","");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Intent i = new Intent(getContext(), Login.class);
                                    startActivity(i);
                                    getActivity().finish();
                                }
                                else
                                {
                                    alert.msg(getString(R.string.error_mail_incorrect),getContext());
                                }
                            }
                            else
                            {
                                alert.msg(getString(R.string.error_mail_not_match),getContext());
                            }

                            break;
                        case "contraseña":
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(auth.getCurrentUser().getEmail(),
                                            antiguoTexto.getText().toString().trim());
                            if(auth.getCurrentUser().reauthenticate(credential).isSuccessful())
                            {
                                if(nuevoTexto.getText().toString().length()>6)
                                {
                                    auth.getCurrentUser().updatePassword(nuevoTexto.getText().toString().trim());
                                    try {
                                        datos.encriptar("correo","");
                                        datos.encriptar("contraseña","");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Intent i = new Intent(getContext(),Login.class);
                                    startActivity(i);
                                    getActivity().finish();

                                }
                                else
                                {
                                    alert.msg(getString(R.string.error_pass_under_six),getContext());
                                }
                            }
                            else
                            {
                                alert.msg(getString(R.string.error_pass_incorrect),getContext());
                            }
                            break;
                    }
                }
            }
        });
        return root; }
}
