package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo para respuesta de GET /actividades/{id}/inscripciones
 * Representa una inscripción con datos del voluntario y la actividad.
 */
public class InscripcionResponse {

    @SerializedName("id")
    private String id; // Composite ID: "voluntario_id-actividad_id"

    @SerializedName("estado")
    private String estado; // Pendiente, Aceptada, Rechazada

    @SerializedName("fecha_solicitud")
    private String fechaSolicitud;

    // Datos del Voluntario (lo que necesitamos para mostrar)
    @SerializedName("id_voluntario")
    private int idVoluntario;

    @SerializedName("nombre_voluntario")
    private String nombreVoluntario;

    // Datos de la Actividad (opcional, puede ser útil)
    @SerializedName("id_actividad")
    private int idActividad;

    @SerializedName("titulo_actividad")
    private String tituloActividad;

    // Getters
    public String getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public int getIdVoluntario() {
        return idVoluntario;
    }

    public String getNombreVoluntario() {
        return nombreVoluntario;
    }

    public int getIdActividad() {
        return idActividad;
    }

    public String getTituloActividad() {
        return tituloActividad;
    }

    // Compatibilidad con VolunteersAdapter: Simular VoluntarioResponse
    public String getNombre() {
        return nombreVoluntario;
    }

    public int getId_usuario() {
        return idVoluntario;
    }
}
