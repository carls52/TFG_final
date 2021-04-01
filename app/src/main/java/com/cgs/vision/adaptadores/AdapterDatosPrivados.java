package com.cgs.vision.adaptadores;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cgs.vision.CodigoPrivado;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class AdapterDatosPrivados extends RecyclerView.Adapter<AdapterDatosPrivados.ViewHolderDatos> {

    private ArrayList<CodigoPrivado> lista;
    private OnItemClickListener mListener;
    private Context context;

    private Usuario usuario;

    public void setOnItemClickListerner(OnItemClickListener listener){
        this.mListener = listener;
    }

    public AdapterDatosPrivados(ArrayList<CodigoPrivado> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,null,false);
        return new ViewHolderDatos(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        ViewCompat.setTransitionName(holder.tituloLista,"tituloPrivado");
        holder.asignarDatos(lista.get(position));

    }
    public interface OnItemClickListener {
        void onItemClick(int position, View titulo);
        void onDeleteClick (int position);
        void onDownloadClick(int position) throws FileNotFoundException;
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView tituloLista,fecha,tipo;
        ImageView borrar,descarga;
        public ViewHolderDatos(@NonNull View itemView, final OnItemClickListener listener) {
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
                            listener.onItemClick(position,tituloLista);
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
                            try {
                                listener.onDownloadClick(position);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }

        public void asignarDatos(CodigoPrivado s) {
            tituloLista.setText(s.getTitulo());
            fecha.setText(s.getFechaCreacion().substring(0,11));
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
    }
}
