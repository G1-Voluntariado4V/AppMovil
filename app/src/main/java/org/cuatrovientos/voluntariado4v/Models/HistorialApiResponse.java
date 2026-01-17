package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class HistorialApiResponse implements Serializable {
    @SerializedName("resumen")
    private Resumen resumen;

    @SerializedName("actividades")
    private List<InscripcionItem> actividades;

    public Resumen getResumen() {
        return resumen;
    }

    public List<InscripcionItem> getActividades() {
        return actividades;
    }

    public static class Resumen implements Serializable {
        @SerializedName("total_participaciones")
        public int total;
    }

    public static class InscripcionItem implements Serializable {
        @SerializedName("id_actividad")
        private int idActividad;

        @SerializedName("titulo_actividad")
        private String titulo;

        @SerializedName("descripcion")
        private String descripcion;

        @SerializedName("ubicacion")
        private String ubicacion;

        @SerializedName("fecha_actividad")
        private String fechaActividad;

        @SerializedName("nombre_organizacion")
        private String nombreOrganizacion;

        @SerializedName("id_organizacion")
        private int idOrganizacion;

        @SerializedName("imagen_actividad")
        private String imagenActividad;

        @SerializedName("estado")
        private String estadoInscripcion;

        @SerializedName("tipos")
        private List<String> tipos;

        @SerializedName("cupo_maximo")
        private int cupoMaximo;

        @SerializedName("inscritos_confirmados")
        private int inscritosConfirmados;

        // Convertir a ActividadResponse
        public ActividadResponse toActividadResponse() {
            ActividadResponse a = new ActividadResponse();
            a.setId(idActividad);
            a.setTitulo(titulo);
            a.setDescripcion(descripcion);
            a.setUbicacion(ubicacion);
            a.setFechaInicio(fechaActividad);
            a.setNombreOrganizacion(nombreOrganizacion);
            a.setImagenActividad(imagenActividad);
            a.setTipos(tipos != null ? tipos : new ArrayList<>());

            a.setIdOrganizacion(idOrganizacion);
            a.setEstadoInscripcionUsuario(estadoInscripcion);

            // Map plazas
            a.setCupoMaximo(cupoMaximo);
            a.setInscritosConfirmados(inscritosConfirmados);

            return a;
        }

        public String getEstadoInscripcion() {
            return estadoInscripcion;
        }

        public List<String> getTipos() {
            return tipos;
        }
    }
}
