package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("google_id")
    private String googleId;

    @SerializedName("email")
    private String email;

    public LoginRequest(String googleId, String email) {
        this.googleId = googleId;
        this.email = email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getEmail() {
        return email;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
