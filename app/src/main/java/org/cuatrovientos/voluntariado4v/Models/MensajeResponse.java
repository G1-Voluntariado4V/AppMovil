package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class MensajeResponse {

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("error")
    private String error;

    public String getMensaje() {
        return mensaje;
    }

    public String getError() {
        return error;
    }

    public boolean hasError() {
        return error != null && !error.isEmpty();
    }
}
