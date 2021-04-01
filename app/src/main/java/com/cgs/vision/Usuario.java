package com.cgs.vision;

import java.util.ArrayList;

public class Usuario {
    private String nombre;
    private String id;
    private ArrayList<CodigoPrivado> codigosPrivados;
    private ArrayList<String> codigosPublicos;
    private ArrayList<String> favoritos;
    private ArrayList<String> grupos;
    private ArrayList<Peticion> peticiones;

    public Usuario() {
    }
    public Usuario(String nombre, String id) {
        this.nombre = nombre;
        this.id = id;
        this.codigosPrivados = new ArrayList<>();
        this.favoritos = new ArrayList<>();
        this.grupos = new ArrayList<>();
        this.peticiones = new ArrayList<>();
        this.codigosPublicos = new ArrayList<>();
    }
    public Usuario(String nombre, String id, ArrayList<CodigoPrivado> codigosPrivados,
                   ArrayList<String> favoritos) {
        this.nombre = nombre;
        this.id = id;
        this.codigosPrivados = codigosPrivados;
        this.favoritos = favoritos;
        this.grupos = new ArrayList<>();
        this.peticiones = new ArrayList<>();
        this.codigosPublicos = new ArrayList<>();
    }
    public void addCodigoPublico (String id)
    {
        if(this.codigosPublicos==null)
            this.codigosPublicos = new ArrayList<>();
        codigosPublicos.add(id);
    }
    public void removeCodigoPublico(String id)
    {
        if(this.codigosPublicos==null)
            this.codigosPublicos = new ArrayList<>();
        codigosPublicos.remove(id);
    }

    public ArrayList<String> getCodigosPublicos() {
        if(this.codigosPublicos==null)
            codigosPublicos=new ArrayList<>();
        return codigosPublicos;
    }

    public void addGrupo(String grupo)
    {
        if(grupos==null)
            grupos = new ArrayList<>();

        grupos.add(grupo);
    }
    public void removeGrupo(String grupo)
    {
        if(grupos==null)
            grupos = new ArrayList<>();

        grupos.remove(grupo);
    }
    public void addPeticion(Peticion peticion)
    {
        if(peticiones==null)
            peticiones = new ArrayList<>();

        peticiones.add(peticion);
    }
    public void removePeticion(String peticion)
    {
        if(peticiones==null)
             peticiones = new ArrayList<>();

        Peticion delete = null;
        for(Peticion p : peticiones)
        {
            if(p.getID().equals(peticion)) {
                delete = p;
                break;
            }
        }
        if(delete!=null)
            peticiones.remove(delete);
    }

    public ArrayList<String> getGrupos() {
        if(grupos==null)
            grupos= new ArrayList<>();

        return grupos;
    }

    public void setGrupos(ArrayList<String> grupos) {
        if(this.grupos==null) {
            this.grupos = new ArrayList<>();
        }
        this.grupos.clear();

        this.grupos.addAll(grupos);
    }

    public ArrayList<Peticion> getPeticiones() {
        if(peticiones==null)
            peticiones= new ArrayList<>();

        return peticiones;
    }

    public void setPeticiones(ArrayList<Peticion> peticiones) {
        if(this.peticiones==null)
            this.peticiones= new ArrayList<>();

        this.peticiones.clear();

        this.peticiones.addAll(peticiones);
    }

    public ArrayList<String> getFavoritos() {
        if(favoritos==null)
            favoritos= new ArrayList<>();

        return favoritos;
    }

   /* public void setFavoritos(LinkedList<String> favoritos) {
        if(this.favoritos==null)
            this.favoritos=new LinkedList<>();

        this.favoritos.clear();

        this.favoritos.addAll(favoritos);
    }*/

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<CodigoPrivado> getCodigosPrivados() {
        if(this.codigosPrivados==null)
            this.codigosPrivados=new ArrayList<>();

        return codigosPrivados;
    }

  /*  public void setCodigosPrivados(LinkedList<CodigoPrivado> codigosPrivados) {
        if(this.codigosPrivados==null)
            this.codigosPrivados=new LinkedList<>();

        this.codigosPrivados.clear();
        this.codigosPrivados.addAll(codigosPrivados);
    }*/
    public void addCodigoPrivado(CodigoPrivado codigo)
    {
        if(this.getCodigosPrivados()==null)
        {
            this.codigosPrivados = new ArrayList<>();
            this.codigosPrivados.add(codigo);
        }
        else if(this.getCodigosPrivados().isEmpty())
        {
            this.codigosPrivados.add(codigo);
        }
        else
            this.codigosPrivados.add(0,codigo);
    }
    //true si hace algun cambio false si no cambia nada
    public boolean addFavorito(String Id)
    {
        if(this.favoritos==null)
        {
            this.favoritos = new ArrayList<String>();
            this.favoritos.add(Id);
            return true;
        }
        else if(this.favoritos.isEmpty())
        {
            this.favoritos.add(Id);
            return true;
        }
        else if(this.favoritos.contains(Id))
        {
            return false;
        }
        else
            this.favoritos.add(Id);

        return true;
    }
    //True si borra el favorito false si no lo hace
    public boolean removeFavorito(String Id)
    {
        if(this.favoritos.contains(Id))
        {
            favoritos.remove(Id);
            return true;
        }
        return false;
    }
    public void updateCodigoPrivado(CodigoPrivado c, int position)
    {
        codigosPrivados.set(position,c);
    }
    public void removeCodigoPrivado(int position)
    {
        codigosPrivados.remove(position);
    }

    public ArrayList<CodigoPrivado> getCodigoTipo(String tipo)
    {
        ArrayList<CodigoPrivado> result = new ArrayList<>();
        for(CodigoPrivado c : this.codigosPrivados)
        {
            if(c.getTipo().equals(tipo))
            {
                result.add(c);
            }
        }
        return result;
    }
    public  void removeCodigoPrivado(CodigoPrivado c)
    {
        this.codigosPrivados.remove(c);
    }

}
