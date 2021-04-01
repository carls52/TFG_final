package com.cgs.vision.adaptadores;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cgs.vision.Grupo;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;

import java.util.ArrayList;

public class AdapterPersonaGrupo extends RecyclerView.Adapter<AdapterPersonaGrupo.ViewHolderPersona>
    {
        private ArrayList<String> listaConfirmados;
        private ArrayList<String> listaPendientes;
        private Grupo grupo;
        private AdapterPersonaGrupo.OnItemClickListener mListener;
        private Usuario usuario;

        public void setOnItemClickListerner(AdapterPersonaGrupo.OnItemClickListener listener){
        this.mListener = listener;
    }


    public AdapterPersonaGrupo(Grupo grupo, Usuario actual) {
        this.listaConfirmados = grupo.getMiembrosConfirmados();
        this.listaPendientes = grupo.getMiembrosPendientes();
        this.grupo = grupo;
        this.usuario = actual;
    }

        @NonNull
        @Override
        public AdapterPersonaGrupo.ViewHolderPersona onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_persona,null,false);
        return new AdapterPersonaGrupo.ViewHolderPersona(view,mListener,grupo);

    }

        @Override
        public void onBindViewHolder(@NonNull AdapterPersonaGrupo.ViewHolderPersona holder, int position) {
            if(position<=listaConfirmados.size()-1)
            {
                holder.asignarDatos(listaConfirmados.get(position));
                holder.hidePendiente();
                if(!grupo.getDueño().equals(usuario.getNombre()))
                    holder.hideBorrar();
                if(listaConfirmados.get(position).equals(grupo.getDueño()))
                    holder.admin();
            }
            else
            {
                holder.asignarDatos(listaPendientes.get(position-listaConfirmados.size()));
            }
    }

        @Override
        public int getItemCount() {
            if(grupo.getDueño().equals(usuario.getNombre()))
                return listaConfirmados.size()+listaPendientes.size();
            else
                return listaConfirmados.size();
    }
        public interface OnItemClickListener {
            void onDeleteClick (int position, Boolean confirmado);

        }

        public static class ViewHolderPersona extends RecyclerView.ViewHolder {
            TextView nombre,estado;
            ImageView borrar;

            public ViewHolderPersona(@NonNull View itemView, final AdapterPersonaGrupo.OnItemClickListener listener, final Grupo grupo) {
                super(itemView);
                nombre = itemView.findViewById(R.id.nombre);
                estado = itemView.findViewById(R.id.estado);
                borrar = itemView.findViewById(R.id.borrar);
                borrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener != null)
                        {
                            int position = getAdapterPosition();
                            if(position != RecyclerView.NO_POSITION)
                            {
                                //true = confirmado
                                //false = pendiente
                                if(position<=grupo.getMiembrosConfirmados().size()-1)
                                    listener.onDeleteClick(position,true);
                                else
                                    listener.onDeleteClick(position,false);
                            }
                        }
                    }
                });

            }

            public void asignarDatos(String s) {
                if(s!=null)
                    nombre.setText(s);
            }
            public void hidePendiente()
            {
                estado.setVisibility(View.GONE);
            }
            public void hideBorrar(){
                borrar.setVisibility(View.GONE);
            }
            public void admin(){
                estado.setVisibility(View.VISIBLE);
                estado.setText("Admin");
                estado.setBackgroundColor(Color.parseColor("#00cc99"));
                estado.setTextColor(Color.parseColor("#FFFFFF"));
                estado.setPadding(10,10,10,10);
                hideBorrar();
            }
        }
    }
