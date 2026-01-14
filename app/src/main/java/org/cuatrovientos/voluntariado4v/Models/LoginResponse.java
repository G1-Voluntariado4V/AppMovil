package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("google_id")
    private String googleId;

    @SerializedName("correo")
    private String correo;

    @SerializedName("rol")
    private String rol;

    @SerializedName("estado")
    private String estado;

    @SerializedName("mensaje")
    private String mensaje;

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }

    public String getEstado() {
        return estado;
    }

    public String getMensaje() {
        return mensaje;
    }
}
