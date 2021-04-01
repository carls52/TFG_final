package com.cgs.vision.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cgs.vision.R;

import java.util.ArrayList;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolderPerson> {
    private ArrayList<String> lista;
    private AdapterUser.OnItemClickListener mListener;
    private String admin;

    public AdapterUser(ArrayList<String> l, String admin)
    {
        this.lista = l;
        this.admin = admin;
    }
    public void setOnItemClickListerner(AdapterUser.OnItemClickListener listener){
        this.mListener = listener;
    }
    @NonNull
    @Override
    public ViewHolderPerson onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person,null,false);
        return new ViewHolderPerson(view,mListener);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPerson holder, int position) {
        holder.asignarDatos(lista.get(position),admin);
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick (int position);
    }

    public static class ViewHolderPerson extends RecyclerView.ViewHolder {
        TextView nombre;
        Button borrar;

        public ViewHolderPerson(@NonNull View itemView, final AdapterUser.OnItemClickListener listener) {
            super(itemView);
            borrar = itemView.findViewById(R.id.borrarParticipante);
            nombre = itemView.findViewById(R.id.name);

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
        }
        public void asignarDatos(String s,String admin) {
            nombre.setText(s);
            if(s.equals(admin))
                borrar.setVisibility(View.GONE);
        }
    }
}
