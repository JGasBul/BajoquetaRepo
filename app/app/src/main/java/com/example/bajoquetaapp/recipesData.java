package com.example.bajoquetaapp;

import android.os.Parcel;
import android.os.Parcelable;

public class recipesData {
    private String descripcion;
    private String nombre;
    private String foto;
    private String calorias;
    private String grasas;
    private String hidratos;
    private String personas;
    private String proteinas;
    private String tiempo;
    private String ingredientes;

    public recipesData(String descripcion, String nombre, String foto, String calorias, String grasas, String hidratos, String personas, String proteinas, String tiempo, String ingredientes) {
        this.descripcion = descripcion;
        this.nombre = nombre;
        this.foto = foto;
        this.calorias = calorias;
        this.grasas = grasas;
        this.hidratos = hidratos;
        this.personas = personas;
        this.proteinas = proteinas;
        this.tiempo = tiempo;
        this.ingredientes = ingredientes;
    }

    public recipesData() {
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCalorias() {
        return calorias;
    }

    public void setCalorias(String calorias) {
        this.calorias = calorias;
    }

    public String getGrasas() {
        return grasas;
    }

    public void setGrasas(String grasas) {
        this.grasas = grasas;
    }

    public String getHidratos() {
        return hidratos;
    }

    public void setHidratos(String hidratos) {
        this.hidratos = hidratos;
    }

    public String getPersonas() {
        return personas;
    }

    public void setPersonas(String personas) {
        this.personas = personas;
    }

    public String getProteinas() {
        return proteinas;
    }

    public void setProteinas(String proteinas) {
        this.proteinas = proteinas;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    @Override
    public String toString() {
        return "recipesData{" +
                "descripcion='" + descripcion + '\'' +
                ", nombre='" + nombre + '\'' +
                ", foto='" + foto + '\'' +
                ", calorias='" + calorias + '\'' +
                ", grasas='" + grasas + '\'' +
                ", hidratos='" + hidratos + '\'' +
                ", personas='" + personas + '\'' +
                ", proteinas='" + proteinas + '\'' +
                ", tiempo='" + tiempo + '\'' +
                ", ingredientes='" + ingredientes + '\'' +
                '}';
    }
}