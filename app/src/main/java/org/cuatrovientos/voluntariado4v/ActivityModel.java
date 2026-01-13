package org.cuatrovientos.voluntariado4v;

import java.io.Serializable;

public class ActivityModel implements Serializable {
    private String title;
    private String organization;
    private String location;
    private String date;
    private String description;
    private String category; // NUEVO: Categoría (Social, Medioambiente, etc.)
    private int imageResource;

    // Constructor actualizado con 'category'
    public ActivityModel(String title, String organization, String location, String date, String description, String category, int imageResource) {
        this.title = title;
        this.organization = organization;
        this.location = location;
        this.date = date;
        this.description = description;
        this.category = category;
        this.imageResource = imageResource;
    }

    // Getters
    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public String getCategory() { return category; } // Nuevo getter
    public int getImageResource() { return imageResource; }
}