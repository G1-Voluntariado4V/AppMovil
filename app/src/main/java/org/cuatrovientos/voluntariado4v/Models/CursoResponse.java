package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class CursoResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("abreviacion")
    private String abreviacion;

    @SerializedName("grado")
    private String grado;

    @SerializedName("nivel")
    private int nivel;

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAbreviacion() {
        return abreviacion;
    }

    public String getGrado() {
        return grado;
    }

    public int getNivel() {
        return nivel;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
