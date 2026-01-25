package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class OdsResponse implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName(value = "imgOds", alternate = { "img_ods" })
    private String imgOds;

    @SerializedName("imgUrl")
    private String imgUrl;

    public OdsResponse() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImgOds() {
        return imgOds;
    }

    public void setImgOds(String imgOds) {
        this.imgOds = imgOds;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    // Método legacy para compatibilidad si algo lo usa
    public String getImagen() {
        return imgOds;
    }

    @Override
    public String toString() {
        return nombre;
    }
}