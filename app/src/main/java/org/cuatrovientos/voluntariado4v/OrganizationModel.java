package org.cuatrovientos.voluntariado4v;

import java.io.Serializable;

public class OrganizationModel implements Serializable {
    private String name;
    private String type; // Ej: "Organización Humanitaria"
    private String description;
    private String email;
    private int logoResId;
    private int headerResId;
    private int volunteersCount;
    private double rating;

    public OrganizationModel(String name, String type, String description, String email, int logoResId, int headerResId, int volunteersCount, double rating) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.email = email;
        this.logoResId = logoResId;
        this.headerResId = headerResId;
        this.volunteersCount = volunteersCount;
        this.rating = rating;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getEmail() { return email; }
    public int getLogoResId() { return logoResId; }
    public int getHeaderResId() { return headerResId; }
    public int getVolunteersCount() { return volunteersCount; }
    public double getRating() { return rating; }
}