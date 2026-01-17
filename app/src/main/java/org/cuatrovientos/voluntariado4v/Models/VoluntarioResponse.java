package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Modelo para respuesta de GET /voluntarios/{id}
 * Contiene todos los datos del perfil del voluntario.
 */
public class VoluntarioResponse {

    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("nombre_completo")
    private String nombreCompleto;

    @SerializedName("correo")
    private String correo;

    @SerializedName("dni")
    private String dni;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("fecha_nac")
    private String fechaNac;

    @SerializedName("carnet_conducir")
    private boolean carnetConducir;

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

    public int getId() {
        return idUsuario;
    }

    public String getNombre() {
        if (nombre != null && !nombre.isEmpty())
            return nombre;
        if (nombreCompleto != null) {
            String[] parts = nombreCompleto.split(" ", 2);
            return parts.length > 0 ? parts[0] : "";
        }
        return "";
    }

    public String getApellidos() {
        if (apellidos != null && !apellidos.isEmpty())
            return apellidos;
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

    public String getDni() {
        return dni != null ? dni : "";
    }

    public String getTelefono() {
        return telefono != null ? telefono : "";
    }

    public String getFechaNac() {
        return fechaNac != null ? fechaNac : "";
    }

    public boolean isCarnetConducir() {
        return carnetConducir;
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

    public static class IdiomaInfo {
        @SerializedName("id_idioma")
        private int idIdioma;
        @SerializedName("idioma")
        private String idioma;
        @SerializedName("nivel")
        private String nivel;

        public int getIdIdioma() {
            return idIdioma;
        }

        public String getIdioma() {
            return idioma;
        }

        public String getNivel() {
            return nivel;
        }
    }
}
