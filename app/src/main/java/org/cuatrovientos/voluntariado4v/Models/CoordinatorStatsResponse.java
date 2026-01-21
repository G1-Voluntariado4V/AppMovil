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

    // Clase interna para mapear el objeto "metricas" del JSON
    public static class Metricas {
        // Asegúrate de que estos nombres coincidan con las columnas de tu SP SQL o vista
        // Usualmente: snake_case en JSON -> camelCase en Java

        @SerializedName("total_usuarios") // Según tu fallback PHP
        public int totalVolunteers;

        @SerializedName("total_organizaciones") // Ajustar según tu SP real
        public int totalOrganizations;

        @SerializedName("actividades_publicadas") // Según tu fallback PHP
        public int totalActivities;

        @SerializedName("inscripciones_pendientes") // Según tu fallback PHP (Voluntarios pendientes)
        public int pendingVolunteerRequests;

        @SerializedName("actividades_revision") // Ajustar según tu SP real
        public int pendingActivityRequests;
    }
}