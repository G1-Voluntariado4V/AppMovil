package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class OrganizacionResponse implements Serializable {

    @SerializedName("id_organizacion")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("email")
    private String email; // Añadido campo email

    @SerializedName("sitio_web")
    private String sitioWeb;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("direccion")
    private String direccion;

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getEmail() { return email; }
    public String getSitioWeb() { return sitioWeb; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
}