package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class OdsResponse implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    public OdsResponse() {
    }

    public OdsResponse(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
