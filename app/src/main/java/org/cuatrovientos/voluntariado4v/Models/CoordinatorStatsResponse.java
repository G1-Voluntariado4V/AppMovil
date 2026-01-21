package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class CoordinatorStatsResponse {

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("metricas")
    private Metricas metricas;

    public Metricas getMetricas() {
        return metricas;
    }

    public static class Metricas {
        // Estas claves coinciden 1:1 con $safeStats en CoordinadorController.php

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
    }
}