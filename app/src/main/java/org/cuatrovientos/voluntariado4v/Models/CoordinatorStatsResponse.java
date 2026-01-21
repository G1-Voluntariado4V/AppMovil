package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class CoordinatorStatsResponse {

    // Usamos @SerializedName para coincidir con el JSON habitual de APIS REST (snake_case)
    @SerializedName("total_volunteers")
    private int totalVolunteers;

    @SerializedName("total_organizations")
    private int totalOrganizations;

    @SerializedName("total_activities")
    private int totalActivities;

    @SerializedName("pending_volunteer_requests")
    private int pendingVolunteerRequests;

    @SerializedName("pending_activity_requests")
    private int pendingActivityRequests;

    // Constructor vacío necesario para Gson
    public CoordinatorStatsResponse() {}

    public CoordinatorStatsResponse(int totalVolunteers, int totalOrganizations, int totalActivities, int pendingVolunteerRequests, int pendingActivityRequests) {
        this.totalVolunteers = totalVolunteers;
        this.totalOrganizations = totalOrganizations;
        this.totalActivities = totalActivities;
        this.pendingVolunteerRequests = pendingVolunteerRequests;
        this.pendingActivityRequests = pendingActivityRequests;
    }

    public int getTotalVolunteers() { return totalVolunteers; }
    public int getTotalOrganizations() { return totalOrganizations; }
    public int getTotalActivities() { return totalActivities; }
    public int getPendingVolunteerRequests() { return pendingVolunteerRequests; }
    public int getPendingActivityRequests() { return pendingActivityRequests; }
}