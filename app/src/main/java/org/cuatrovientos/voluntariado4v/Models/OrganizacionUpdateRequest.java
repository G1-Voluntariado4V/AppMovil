package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class OrganizacionUpdateRequest {

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("sitioWeb")
    private String sitioWeb;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("telefono")
    private String telefono;

    public OrganizacionUpdateRequest(String nombre, String descripcion, String sitioWeb, String direccion,
            String telefono) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.sitioWeb = sitioWeb;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }
}
