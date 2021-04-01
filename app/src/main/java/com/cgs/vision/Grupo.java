package com.cgs.vision;

import java.util.ArrayList;

public class Grupo {
    private String titulo,codigo,dueño,descripcion,fecha,tipo;
    private ArrayList<String> miembrosConfirmados;
    private ArrayList<String> miembrosPendientes;
    private ArrayList<Comentario> comentarios;
    private String id;

    public Grupo() {
    }

    public Grupo(String titulo, String codigo, String dueño, String descripcion, String fecha, String tipo,
                 ArrayList<String> miembrosConfirmados, ArrayList<String> miembrosPendientes, ArrayList<Comentario> comentarios,
                 String id) {
        this.titulo = titulo;
        this.codigo = codigo;
        this.dueño = dueño;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.tipo = tipo;
        this.miembrosConfirmados = miembrosConfirmados;
        this.miembrosPendientes = miembrosPendientes;
        this.id = id;
        this.comentarios = comentarios;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDueño() {
        return dueño;
    }

    public void setDueño(String dueño) {
        this.dueño = dueño;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public ArrayList<String> getMiembrosConfirmados() {
        if(miembrosConfirmados==null)
            miembrosConfirmados = new ArrayList<>();
        return miembrosConfirmados;
    }

    public void setMiembrosConfirmados(ArrayList<String> miembrosConfirmados) {
        this.miembrosConfirmados = miembrosConfirmados;
    }

    public ArrayList<String> getMiembrosPendientes() {
        if(miembrosPendientes==null)
            miembrosPendientes=new ArrayList<>();
        return miembrosPendientes;
    }

    public void setMiembrosPendientes(ArrayList<String> miembrosPendientes) {
        this.miembrosPendientes = miembrosPendientes;
    }
    public void addComentario(Comentario c)
    {
        if(comentarios==null)
            comentarios = new ArrayList<>();
        comentarios.add(0,c);
    }
    public ArrayList<Comentario> getComentarios() {
        if(comentarios==null)
            comentarios=new ArrayList<>();
        return comentarios;
    }

    public void setComentarios(ArrayList<Comentario> comentarios) {
        this.comentarios = comentarios;
    }
    public void removeMiembroPendiente(String nombre)
    {
        if(miembrosPendientes!=null) {
            if (!(miembrosPendientes.isEmpty()))
                miembrosPendientes.remove(nombre);
        }
    }
    public void addMiembroConfirmado(String nombre)
    {
        if(miembrosConfirmados==null)
           miembrosConfirmados = new ArrayList<>();

        miembrosConfirmados.add(nombre);
    }
    public void addMiembroPendiente(String nombre)
    {
        if(miembrosPendientes==null)
            miembrosPendientes = new ArrayList<>();

        miembrosPendientes.add(nombre);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
