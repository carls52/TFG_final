package com.cgs.vision;

public class CodigoPrivado {
    String codigo;
    String titulo;
    String autor;
    String fechaCreacion;
    String tipo;

    public CodigoPrivado() {
    }

    public CodigoPrivado(String codigo, String titulo, String autor, String fechaCreacion, String tipo) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.fechaCreacion = fechaCreacion;
        this.tipo = tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
