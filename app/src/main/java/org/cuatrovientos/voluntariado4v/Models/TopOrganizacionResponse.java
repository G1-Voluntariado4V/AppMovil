package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class TopOrganizacionResponse implements Serializable {
    @SerializedName("posicion")
    private int posicion;
    @SerializedName("id_organizacion")
    private int idOrganizacion;
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("cif")
    private String cif;
    @SerializedName("total_voluntarios")
    private int totalVoluntarios;
    @SerializedName("total_actividades")
    private int totalActividades;
    @SerializedName("descripcion")
    private String descripcion;
    @SerializedName("telefono")
    private String telefono;
    @SerializedName("sitio_web")
    private String sitioWeb;

    public int getPosicion() {
        return posicion;
    }

    public int getIdOrganizacion() {
        return idOrganizacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCif() {
        return cif;
    }

    public int getTotalVoluntarios() {
        return totalVoluntarios;
    }

    public int getTotalActividades() {
        return totalActividades;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }
}
