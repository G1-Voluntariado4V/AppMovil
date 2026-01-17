package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class ImagenRequest {

    @SerializedName("url_imagen")
    private String urlImagen;

    @SerializedName("descripcion")
    private String descripcion;

    public ImagenRequest(String urlImagen, String descripcion) {
        this.urlImagen = urlImagen;
        this.descripcion = descripcion;
    }

    public ImagenRequest(String urlImagen) {
        this.urlImagen = urlImagen;
        this.descripcion = null;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
