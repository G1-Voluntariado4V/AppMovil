package org.cuatrovientos.voluntariado4v;

import java.io.Serializable;

public class ActivityModel implements Serializable {
    private String title;
    private String organization;
    private String location;
    private String date;
    private String description;
    private String category;
    private int imageResource;

    // NUEVOS CAMPOS: Plazas
    private int occupiedSeats;
    private int totalSeats;

    // Constructor actualizado (añadimos los dos int al final)
    public ActivityModel(String title, String organization, String location, String date, String description, String category, int imageResource, int occupiedSeats, int totalSeats) {
        this.title = title;
        this.organization = organization;
        this.location = location;
        this.date = date;
        this.description = description;
        this.category = category;
        this.imageResource = imageResource;
        this.occupiedSeats = occupiedSeats;
        this.totalSeats = totalSeats;
    }

    // Getters existentes
    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public int getImageResource() { return imageResource; }

    // NUEVOS GETTERS
    public int getOccupiedSeats() { return occupiedSeats; }
    public int getTotalSeats() { return totalSeats; }
}