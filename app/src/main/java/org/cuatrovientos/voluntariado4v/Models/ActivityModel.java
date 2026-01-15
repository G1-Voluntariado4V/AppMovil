package org.cuatrovientos.voluntariado4v.Models;

import java.io.Serializable;

public class ActivityModel implements Serializable {
    private int id;
    private String title;
    private String organization;
    private String location;
    private String date;
    private String description;
    private int imageResource;
    private String category;
    // Nuevo campo: "ACTIVE", "FINISHED", "CANCELLED"
    private String status;

    public ActivityModel(int id, String title, String organization, String location, String date, String description, int imageResource, String category, String status) {
        this.id = id;
        this.title = title;
        this.organization = organization;
        this.location = location;
        this.date = date;
        this.description = description;
        this.imageResource = imageResource;
        this.category = category;
        this.status = status;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public int getImageResource() { return imageResource; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
}