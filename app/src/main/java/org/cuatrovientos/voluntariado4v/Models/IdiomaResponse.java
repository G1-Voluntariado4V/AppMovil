package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class IdiomaResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("nombre_idioma")
    private String nombre;

    @SerializedName("codigo_iso")
    private String codigoIso;

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigoIso() {
        return codigoIso;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
