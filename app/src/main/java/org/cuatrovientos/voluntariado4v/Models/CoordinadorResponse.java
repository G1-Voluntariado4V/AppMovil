package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class CoordinadorResponse {
    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("correo")
    private String correo;

    @SerializedName("telefono")
    private String telefono;

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre != null ? nombre : "";
    }

    public String getApellidos() {
        return apellidos != null ? apellidos : "";
    }

    public String getCorreo() {
        return correo != null ? correo : "";
    }

    public String getTelefono() {
        return telefono != null ? telefono : "";
    }
}