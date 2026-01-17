package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class OrganizacionResponse implements Serializable {

    @SerializedName(value = "id_organizacion", alternate = { "id", "idUsuario", "userId" })
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("email")
    private String email;

    @SerializedName(value = "sitio_web", alternate = { "web", "sitioWeb", "url" })
    private String sitioWeb;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName(value = "total_voluntarios_historico", alternate = { "total_voluntarios" })
    private int totalVoluntarios;

    @SerializedName(value = "ranking_global", alternate = { "posicion", "ranking" })
    private int rankingGlobal;

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEmail() {
        return email;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public int getTotalVoluntarios() {
        return totalVoluntarios;
    }

    public int getRankingGlobal() {
        return rankingGlobal;
    }
}