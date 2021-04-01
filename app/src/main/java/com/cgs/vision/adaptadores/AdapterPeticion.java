package com.cgs.vision.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cgs.vision.Peticion;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;

import java.util.ArrayList;


public class AdapterPeticion extends RecyclerView.Adapter<AdapterPeticion.ViewHolderPeticion>
{
    private ArrayList<Peticion> lista;
    private AdapterPeticion.OnItemClickListener mListener;
    private Usuario usuario;

    public void setOnItemClickListerner(AdapterPeticion.OnItemClickListener listener){
        this.mListener = listener;
    }


    public AdapterPeticion(ArrayList<Peticion> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public AdapterPeticion.ViewHolderPeticion onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peticion,null,false);
        return new AdapterPeticion.ViewHolderPeticion(view,mListener);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPeticion.ViewHolderPeticion holder, int position) {
        holder.asignarDatos(lista.get(position));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
    public interface OnItemClickListener {
        void onSi (int position);
        void onNo(int position);

    }

    public static class ViewHolderPeticion extends RecyclerView.ViewHolder {
        TextView nombreGrupo;
        Button si,no;
        public ViewHolderPeticion(@NonNull View itemView, final AdapterPeticion.OnItemClickListener listener) {
            super(itemView);
            nombreGrupo = itemView.findViewById(R.id.nombreGrupo);
            si = itemView.findViewById(R.id.si);
            no = itemView.findViewById(R.id.no);

            si.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onSi(position);
                        }
                    }
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onNo(position);
                        }
                    }
                }
            });
        }

        public void asignarDatos(Peticion s) {
            if(s!=null)
                nombreGrupo.setText(s.getNombre().trim());
        }
    }
}
