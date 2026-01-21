package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class CoordinatorStatsResponse {

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("metricas")
    private Metricas metricas;

    // Getter seguro: si viene nulo, devolvemos un objeto vacío para evitar NullPointerException
    public Metricas getMetricas() {
        return metricas != null ? metricas : new Metricas();
    }

    public String getTitulo() {
        return titulo;
    }

    public static class Metricas {
        // COINCIDENCIA EXACTA con las claves del JSON que envía tu API PHP

        @SerializedName("voluntarios_activos")
        public int totalVolunteers;

        @SerializedName("organizaciones_activas")
        public int totalOrganizations;

        @SerializedName("actividades_publicadas")
        public int totalActivities;

        @SerializedName("voluntarios_pendientes")
        public int pendingVolunteerRequests;

        @SerializedName("actividades_pendientes")
        public int pendingActivityRequests;

        // Constructor por defecto para inicializar a 0 si falla Gson
        public Metricas() {
            this.totalVolunteers = 0;
            this.totalOrganizations = 0;
            this.totalActivities = 0;
            this.pendingVolunteerRequests = 0;
            this.pendingActivityRequests = 0;
        }
    }
}