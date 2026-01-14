package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class VoluntarioResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("correo")
    private String correo;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("dni")
    private String dni;

    @SerializedName("fecha_nac")
    private String fechaNac;

    @SerializedName("img_perfil")
    private String imgPerfil;

    @SerializedName("carnet_conducir")
    private boolean carnetConducir;

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDni() {
        return dni;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public String getImgPerfil() {
        return imgPerfil;
    }

    public boolean isCarnetConducir() {
        return carnetConducir;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }
}
