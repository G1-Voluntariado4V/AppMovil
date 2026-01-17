package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class TipoVoluntariadoResponse implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName(value = "nombreTipo", alternate = { "nombre", "nombre_tipo" })
    private String nombre;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
