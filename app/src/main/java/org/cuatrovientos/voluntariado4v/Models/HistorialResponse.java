package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HistorialResponse {

    @SerializedName("resumen")
    private Resumen resumen;

    @SerializedName("actividades")
    private List<ActividadHistorial> actividades;

    public Resumen getResumen() {
        return resumen;
    }

    public List<ActividadHistorial> getActividades() {
        return actividades;
    }

    public static class Resumen {
        @SerializedName("total_participaciones")
        private int totalParticipaciones;

        @SerializedName("horas_acumuladas")
        private int horasAcumuladas;

        @SerializedName("nivel_experiencia")
        private String nivelExperiencia;

        public int getTotalParticipaciones() {
            return totalParticipaciones;
        }

        public int getHorasAcumuladas() {
            return horasAcumuladas;
        }

        public String getNivelExperiencia() {
            return nivelExperiencia;
        }
    }

    public static class ActividadHistorial {
        @SerializedName("id_actividad")
        private int idActividad;

        @SerializedName("titulo")
        private String titulo;

        @SerializedName("fecha_inicio")
        private String fechaInicio;

        @SerializedName("estado_solicitud")
        private String estadoSolicitud;

        @SerializedName("horas")
        private int horas;

        public int getIdActividad() {
            return idActividad;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getFechaInicio() {
            return fechaInicio;
        }

        public String getEstadoSolicitud() {
            return estadoSolicitud;
        }

        public int getHoras() {
            return horas;
        }
    }
}
