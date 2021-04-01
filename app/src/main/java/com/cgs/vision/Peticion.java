package com.cgs.vision;

public class Peticion {
    private String ID;
    private String nombre;

    public Peticion(String ID, String nombre) {
        this.ID = ID;
        this.nombre = nombre;
    }
    public Peticion(){}

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
