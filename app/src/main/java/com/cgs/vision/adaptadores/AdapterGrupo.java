package com.cgs.vision.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cgs.vision.Grupo;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;

import java.util.ArrayList;


public class AdapterGrupo extends RecyclerView.Adapter<AdapterGrupo.ViewHolderGrupo> {
    private ArrayList<Grupo> lista;
    private AdapterGrupo.OnItemClickListener mListener;
    private Usuario usuario;
    private Context context;

    public void setOnItemClickListerner(AdapterGrupo.OnItemClickListener listener){
        this.mListener = listener;
    }


    public AdapterGrupo(ArrayList<Grupo> lista, Context context) {
        this.lista = lista;
        this.context=context;
    }

    @NonNull
    @Override
    public AdapterGrupo.ViewHolderGrupo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,null,false);
        return new AdapterGrupo.ViewHolderGrupo(view,mListener);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterGrupo.ViewHolderGrupo holder, int position) {
        holder.asignarDatos(lista.get(position));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick (int position);
        void onCheckChange(int position, boolean isChecked);
        void onDownloadClick(int position);
    }
    public class ViewHolderGrupo extends RecyclerView.ViewHolder {
        TextView tituloLista,descripcion,fecha,tipo;
        ImageView borrar,descarga;

        public ViewHolderGrupo(@NonNull View itemView, final AdapterGrupo.OnItemClickListener listener) {
            super(itemView);
            tituloLista = itemView.findViewById(R.id.tituloLista);
            fecha = itemView.findViewById(R.id.fecha);
            borrar = itemView.findViewById(R.id.deletePrivado);
            descarga = itemView.findViewById(R.id.descargarPrivado);
            tipo = itemView.findViewById(R.id.tipoPrivado);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            borrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
            descarga.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onDownloadClick(position);
                        }
                    }
                }
            });
        }

        public void asignarDatos(Grupo s) {
            tituloLista.setText(s.getTitulo());
            //descripcion.setText(s.getDescripcion());
            fecha.setText(s.getFecha().substring(0,11));
            tipo.setText(s.getTipo());
            switch (s.getTipo())
            {
                case "JAVA":
                    tipo.setTextColor(ContextCompat.getColor(context, R.color.java));
                    break;
                case "PYTHON":
                    tipo.setTextColor(ContextCompat.getColor(context, R.color.python));
                    break;
                case "C":
                    tipo.setTextColor(ContextCompat.getColor(context, R.color.c));
                    break;
                case "C++":
                    tipo.setTextColor(ContextCompat.getColor(context, R.color.cplus));
                    break;
                case "JAVASCRIPT":
                    tipo.setTextColor(ContextCompat.getColor(context, R.color.javascript));
                    break;
                default:
                    break;
            }
        }

        public void esconderBorrar()
        {
            borrar.setVisibility(View.INVISIBLE);
        }
    }
}

