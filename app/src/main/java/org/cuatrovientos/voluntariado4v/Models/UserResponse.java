package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class UserResponse implements Serializable {
    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("correo")
    private String correo;

    @SerializedName("nombre_rol") // Coincide con alias SQL en UsuarioController
    private String nombreRol;

    @SerializedName("estado_cuenta") // Coincide con alias SQL en UsuarioController
    private String estadoCuenta;

    @SerializedName("img_perfil")
    private String imgPerfil;

    // Constructor vacío
    public UserResponse() {}

    public int getId() {
        return idUsuario;
    }

    public String getCorreo() {
        return correo != null ? correo : "";
    }

    public String getNombre() {
        return correo; // Usamos el correo como nombre visual
    }

    public String getRol() {
        return nombreRol != null ? nombreRol : "User";
    }

    public String getEstadoCuenta() {
        return estadoCuenta != null ? estadoCuenta : "Pendiente";
    }

    public String getImgPerfil() {
        return imgPerfil;
    }
}