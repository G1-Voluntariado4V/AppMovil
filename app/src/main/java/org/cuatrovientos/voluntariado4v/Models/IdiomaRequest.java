package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

/**
 * DTO para añadir un idioma al perfil del voluntario.
 * Corresponde al endpoint POST /voluntarios/{id}/idiomas
 */
public class IdiomaRequest {

    @SerializedName("id_idioma")
    private int idIdioma;

    @SerializedName("nivel")
    private String nivel;

    public IdiomaRequest(int idIdioma, String nivel) {
        this.idIdioma = idIdioma;
        this.nivel = nivel;
    }

    public int getIdIdioma() {
        return idIdioma;
    }

    public void setIdIdioma(int idIdioma) {
        this.idIdioma = idIdioma;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }
}
