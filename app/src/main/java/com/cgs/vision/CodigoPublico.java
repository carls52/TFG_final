package com.cgs.vision;

import java.util.ArrayList;

public class CodigoPublico {

    private float sumRating,rating;
    private String ID;
    private String titulo;
    private String codigo;
    private String descripcion;
    private String autor;
    private String fecha;
    private int descargas, ratingTotal;
    private int visualizaciones;
    private ArrayList<Comentario> comentarios;
    private String tipo;
    private String IdAutor;

    public CodigoPublico(String ID, String titulo, String codigo, String descripcion, String autor,
                         String fecha, int descargas, int visualizaciones, ArrayList<Comentario> comentarios,
                         String tipo, String IdAutor) {
        this.ID = ID;
        this.titulo = titulo;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.autor = autor;
        this.fecha = fecha;
        this.descargas = descargas;
        this.visualizaciones = visualizaciones;
        this.comentarios = comentarios;
        this.tipo = tipo;
        this.IdAutor = IdAutor;
        this.rating = 0;
        this.sumRating = 0;

    }
    public CodigoPublico(String ID, String titulo, String codigo, String descripcion, String autor,
                         String fecha, int descargas, int visualizaciones, String tipo, String IdAutor) {
        this.ID = ID;
        this.titulo = titulo;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.autor = autor;
        this.fecha = fecha;
        this.descargas = descargas;
        this.visualizaciones = visualizaciones;
        this.comentarios = new ArrayList<>();
        this.tipo = tipo;
        this.IdAutor = IdAutor;
        this.rating = 0;
        this.sumRating = 0;

    }
    public CodigoPublico() {
    }
    public void setRating(float r) {this.rating=r;}
    public float getRating() {
        return rating;
    }
    public int getRatingTotal()
    {
        return ratingTotal;
    }
    public void setSumRating(float sumRating)
    {
        this.sumRating=sumRating;
    }

    public float getSumRating() {
        return sumRating;
    }

    public void addRating(float rating)
    {
        ratingTotal++;
        sumRating+=rating;
        this.rating = sumRating/ ratingTotal;
    }
    public void sustituirRating(float old ,float n)
    {
        sumRating = sumRating - old;
        sumRating += n;
        this.rating = sumRating/ ratingTotal;
    }



    public String getIdAutor() {
        return IdAutor;
    }

    public void setIdAutor(String idAutor) {
        IdAutor = idAutor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public int getDescargas() {
        return descargas;
    }

    public void setDescargas(int descargas) {
        this.descargas = descargas;
    }

    public int getVisualizaciones() {
        return visualizaciones;
    }

    public void setVisualizaciones(int visualizaciones) {
        this.visualizaciones = visualizaciones;
    }

    public ArrayList<Comentario> getComentarios() {
        if(comentarios==null)
            comentarios = new ArrayList<>();
        return comentarios;
    }

    public void setComentarios(ArrayList<Comentario> comentarios) {
        this.comentarios = comentarios;
    }
    public void addComentario(Comentario comentario)
    {
        if(comentarios==null)
        {
            comentarios = new ArrayList<>();
        }
            comentarios.add(0,comentario);
    }
    public int addVisualizacion()
    {
        return this.visualizaciones++;
    }
}
