package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PendingEnrollmentResponse implements Serializable {

    @SerializedName("id_actividad")
    private int idActividad;

    @SerializedName("id_voluntario")
    private int idVoluntario;

    @SerializedName("fecha_solicitud")
    private String fechaSolicitud;

    @SerializedName("estado_solicitud")
    private String estadoSolicitud;

    @SerializedName(value = "nombre_voluntario", alternate = { "nombre" })
    private String nombreVoluntario;

    @SerializedName(value = "apellidos_voluntario", alternate = { "apellidos" })
    private String apellidosVoluntario;

    @SerializedName(value = "email_voluntario", alternate = { "correo", "email" })
    private String emailVoluntario;

    @SerializedName("titulo_actividad")
    private String tituloActividad;

    @SerializedName("imagen_actividad")
    private String imagenActividad;

    public PendingEnrollmentResponse() {
    }

    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public int getIdVoluntario() {
        return idVoluntario;
    }

    public void setIdVoluntario(int idVoluntario) {
        this.idVoluntario = idVoluntario;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(String estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }

    public String getNombreVoluntario() {
        return nombreVoluntario;
    }

    public void setNombreVoluntario(String nombreVoluntario) {
        this.nombreVoluntario = nombreVoluntario;
    }

    public String getApellidosVoluntario() {
        return apellidosVoluntario;
    }

    public void setApellidosVoluntario(String apellidosVoluntario) {
        this.apellidosVoluntario = apellidosVoluntario;
    }

    public String getEmailVoluntario() {
        return emailVoluntario;
    }

    public void setEmailVoluntario(String emailVoluntario) {
        this.emailVoluntario = emailVoluntario;
    }

    public String getTituloActividad() {
        return tituloActividad;
    }

    public void setTituloActividad(String tituloActividad) {
        this.tituloActividad = tituloActividad;
    }

    public String getImagenActividad() {
        // Ensure proper URL format
        if (imagenActividad != null && !imagenActividad.isEmpty()) {
            if (imagenActividad.startsWith("http")) {
                return imagenActividad;
            } else {
                return org.cuatrovientos.voluntariado4v.API.ApiClient.BASE_URL + "uploads/actividades/"
                        + imagenActividad;
            }
        }
        return "https://placehold.co/100x100/png?text=Activity";
    }

    public void setImagenActividad(String imagenActividad) {
        this.imagenActividad = imagenActividad;
    }
}
