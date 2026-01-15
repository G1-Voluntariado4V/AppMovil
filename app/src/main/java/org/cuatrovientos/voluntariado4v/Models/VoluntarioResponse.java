package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Modelo híbrido para soportar respuesta de LOGIN (nombre, apellidos) y PERFIL
 * (nombre_completo).
 */
public class VoluntarioResponse {

    @SerializedName("id_usuario")
    private int idUsuario;

    // Login
    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;

    // Perfil
    @SerializedName("nombre_completo")
    private String nombreCompleto;

    @SerializedName("correo")
    private String correo;

    @SerializedName("curso")
    private String curso;

    @SerializedName("estado_cuenta")
    private String estadoCuenta;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("preferencias")
    private List<String> preferencias;

    @SerializedName("idiomas")
    private List<IdiomaInfo> idiomas;

    // Métodos híbridos
    public int getId() {
        return idUsuario;
    }

    public String getNombre() {
        if (nombre != null && !nombre.isEmpty())
            return nombre;
        // Fallback desde nombreCompleto
        if (nombreCompleto != null) {
            String[] parts = nombreCompleto.split(" ", 2);
            return parts.length > 0 ? parts[0] : "";
        }
        return "";
    }

    public String getApellidos() {
        if (apellidos != null && !apellidos.isEmpty())
            return apellidos;
        // Fallback desde nombreCompleto
        if (nombreCompleto != null) {
            String[] parts = nombreCompleto.split(" ", 2);
            return parts.length > 1 ? parts[1] : "";
        }
        return "";
    }

    public String getNombreCompleto() {
        if (nombreCompleto != null && !nombreCompleto.isEmpty())
            return nombreCompleto;
        return (nombre != null ? nombre : "") + " " + (apellidos != null ? apellidos : "");
    }

    public String getCorreo() {
        return correo;
    }

    public String getCurso() {
        return curso;
    }

    public String getEstadoCuenta() {
        return estadoCuenta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public List<String> getPreferencias() {
        return preferencias;
    }

    public List<IdiomaInfo> getIdiomas() {
        return idiomas;
    }

    // Compatibilidad
    public String getDni() {
        return "";
    }

    public String getTelefono() {
        return "";
    }

    public String getFechaNac() {
        return "";
    }

    public boolean isCarnetConducir() {
        return false;
    }

    // Subclase Idiomas
    public static class IdiomaInfo {
        @SerializedName("idioma")
        private String idioma;
        @SerializedName("nivel")
        private String nivel;

        public String getIdioma() {
            return idioma;
        }

        public String getNivel() {
            return nivel;
        }
    }
}
