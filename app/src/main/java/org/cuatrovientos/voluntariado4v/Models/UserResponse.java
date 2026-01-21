package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("correo")
    private String correo;

    @SerializedName("nombre_rol")
    private String nombreRol; // Ej: "Voluntario", "Organizacion"

    @SerializedName("estado_cuenta")
    private String estadoCuenta; // Ej: "Pendiente", "Activa", "Bloqueada"

    // Constructor vacío necesario para Gson
    public UserResponse() {}

    public int getId() {
        return idUsuario;
    }

    public String getCorreo() {
        return correo != null ? correo : "";
    }

    // Como el endpoint /usuarios no devuelve nombre, usamos el correo como identificador visual
    public String getNombre() {
        return correo;
    }

    public String getRol() {
        return nombreRol != null ? nombreRol : "User";
    }

    public String getEstadoCuenta() {
        return estadoCuenta != null ? estadoCuenta : "Pendiente";
    }
}