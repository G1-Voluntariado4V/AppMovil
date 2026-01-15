package org.cuatrovientos.voluntariado4v.Models;

import java.io.Serializable;

public class OrganizationModel implements Serializable {
    private String name;
    private String type;
    private String description;
    private String email;
    private String address; // Nuevo
    private String website; // Nuevo
    private int logoResId;
    private int headerResId;
    private int volunteersCount;
    private double rating;

    public OrganizationModel(String name, String type, String description, String email, String address, String website, int logoResId, int headerResId, int volunteersCount, double rating) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.email = email;
        this.address = address;
        this.website = website;
        this.logoResId = logoResId;
        this.headerResId = headerResId;
        this.volunteersCount = volunteersCount;
        this.rating = rating;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public int getLogoResId() { return logoResId; }
    public int getHeaderResId() { return headerResId; }
    public int getVolunteersCount() { return volunteersCount; }
    public double getRating() { return rating; }
}