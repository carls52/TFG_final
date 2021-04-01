package com.cgs.vision.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cgs.vision.CodigoPublico;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;

import java.util.ArrayList;

public class AdapterDatosPublicos extends RecyclerView.Adapter<AdapterDatosPublicos.ViewHolderPublic>
 {
    private ArrayList<CodigoPublico> lista;
    private OnItemClickListener mListener;
    private Usuario usuario;
    private Context context;

    public void setOnItemClickListerner(OnItemClickListener listener){
        this.mListener = listener;
    }

    public AdapterDatosPublicos(ArrayList<CodigoPublico> lista, Usuario usuario , Context contexto) {
        this.lista = lista;
        this.usuario = usuario;
        this.context = contexto;
    }

    @NonNull
    @Override
    public ViewHolderPublic onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_publico,null,false);
        return new ViewHolderPublic(view,mListener);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPublic holder, int position) {
        ViewCompat.setTransitionName(holder.tituloLista,"tituloPublic");
        ViewCompat.setTransitionName(holder.descripcion,"descPublic");
        holder.asignarDatos(lista.get(position));
        if(usuario.getFavoritos()!=null) {
            if (usuario.getFavoritos().contains(lista.get(position).getID())) {
                holder.cambiarEstrella();
            }
        }
        if (!lista.get(position).getAutor().equals(usuario.getNombre())) {
            holder.esconderBorrar();
        }

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
     public interface OnItemClickListener {
         void onItemClick(int position,View titulo,View descripcion);
         void onDeleteClick (int position);
         void onCheckChange(int position, boolean isChecked);
         void onDownloadClick(int position);
     }

    public class ViewHolderPublic extends RecyclerView.ViewHolder {
        TextView tituloLista,descripcion,fecha,tipo,autor,numRating;
        ImageView borrar,descargar;
        ToggleButton fav;
        RatingBar rating;
        //private Context context;

        public ViewHolderPublic(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tituloLista = itemView.findViewById(R.id.tituloListaPublica);
            descripcion = itemView.findViewById(R.id.descripcionPublica);
            fecha = itemView.findViewById(R.id.fechaPublica);
            tipo = itemView.findViewById(R.id.tipoListaPublica);
            borrar = itemView.findViewById(R.id.deletePublico);
            fav = itemView.findViewById(R.id.fab);
            autor = itemView.findViewById(R.id.autorPublico);
            descargar = itemView.findViewById(R.id.descargarPublico);
            rating = itemView.findViewById(R.id.ratingBar2);
            numRating = itemView.findViewById(R.id.numRating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(position,tituloLista,descripcion);
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
            fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onCheckChange(position,isChecked);
                        }
                    }
                }
            });
            descargar.setOnClickListener(new View.OnClickListener() {
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

        public void asignarDatos(CodigoPublico s) {
            tituloLista.setText(s.getTitulo());
            descripcion.setText(s.getDescripcion());
            fecha.setText(s.getFecha().substring(0,11));
            tipo.setText(s.getTipo());
            autor.setText(s.getAutor());
            rating.setRating(s.getRating());
            numRating.setText(String.valueOf(s.getRatingTotal()));
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
        public void esconderEstrella()
        {
            fav.setVisibility(View.GONE);
        }
        public void cambiarEstrella()
        {
            fav.setChecked(true);
        }
        public void esconderBorrar()
        {
            borrar.setVisibility(View.GONE);
        }
    }
}
