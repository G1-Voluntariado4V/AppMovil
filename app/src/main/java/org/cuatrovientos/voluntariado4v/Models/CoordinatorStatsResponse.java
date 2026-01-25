package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class CoordinatorStatsResponse {

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("metricas")
    private Metricas metricas;

    public Metricas getMetricas() {
        return metricas != null ? metricas : new Metricas();
    }

    public String getTitulo() {
        return titulo;
    }

    public static class Metricas {

        // VOLUNTARIOS ACTIVOS
        @SerializedName(value = "voluntarios_activos", alternate = { "total_usuarios", "active_volunteers","voluntarios" })
        public int totalVolunteers;

        // ORGANIZACIONES ACTIVAS
        @SerializedName(value = "organizaciones_activas", alternate = { "total_organizaciones", "active_organizations", "organizaciones"})
        public int totalOrganizations;

        // ACTIVIDADES PUBLICADAS
        @SerializedName(value = "actividades_publicadas", alternate = {"total_actividades", "published_activities", "actividades"})
        public int totalActivities;

        // SOLICITUDES DE VOLUNTARIOS (PENDIENTES)
        @SerializedName(value = "voluntarios_pendientes", alternate = {"inscripciones_pendientes", "pending_users", "usuarios_pendientes"})
        public int pendingVolunteerRequests;

        // ACTIVIDADES POR REVISAR (El que te salía a 0)
        @SerializedName(value = "actividades_pendientes", alternate = { "actividades_revision", "en_revision", "pending_activities", "actividades_en_revision" })
        public int pendingActivityRequests;

        // COORDINADORES ACTIVOS
        @SerializedName(value = "coordinadores_activos", alternate = { "total_coordinadores", "active_coordinators", "coordinadores" })
        public int totalCoordinators;

        public Metricas() {
            this.totalVolunteers = 0;
            this.totalOrganizations = 0;
            this.totalActivities = 0;
            this.pendingVolunteerRequests = 0;
            this.pendingActivityRequests = 0;
            this.totalCoordinators = 0;
        }
    }
}