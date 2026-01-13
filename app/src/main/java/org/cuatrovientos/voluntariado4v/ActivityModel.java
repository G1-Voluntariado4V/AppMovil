package org.cuatrovientos.voluntariado4v;

import java.io.Serializable;

// IMPLEMENTAMOS Serializable para poder pasar este objeto en un Intent
public class ActivityModel implements Serializable {
    private String title;
    private String organization;
    private String location;
    private String date;
    private String description;
    private int imageResource;

    public ActivityModel(String title, String organization, String location, String date, String description, int imageResource) {
        this.title = title;
        this.organization = organization;
        this.location = location;
        this.date = date;
        this.description = description;
        this.imageResource = imageResource;
    }

    // Getters
    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public int getImageResource() { return imageResource; }
}