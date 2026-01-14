package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ActividadResponse implements Serializable {

    @SerializedName("id_actividad")
    private int idActividad;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("ubicacion")
    private String ubicacion;

    @SerializedName("fecha_inicio")
    private String fechaInicio;

    @SerializedName("duracion_horas")
    private int duracionHoras;

    @SerializedName("cupo_maximo")
    private int cupoMaximo;

    @SerializedName("inscritos_confirmados")
    private int inscritosConfirmados;

    @SerializedName("nombre_organizacion")
    private String nombreOrganizacion;

    @SerializedName("img_organizacion")
    private String imgOrganizacion;

    @SerializedName("estado_publicacion")
    private String estadoPublicacion;

    public int getIdActividad() {
        return idActividad;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public int getDuracionHoras() {
        return duracionHoras;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public int getInscritosConfirmados() {
        return inscritosConfirmados;
    }

    public String getNombreOrganizacion() {
        return nombreOrganizacion;
    }

    public String getImgOrganizacion() {
        return imgOrganizacion;
    }

    public String getEstadoPublicacion() {
        return estadoPublicacion;
    }

    public boolean hayPlazasDisponibles() {
        return inscritosConfirmados < cupoMaximo;
    }

    public int getPlazasRestantes() {
        return cupoMaximo - inscritosConfirmados;
    }

    public String getImageUrl() {
        if (imgOrganizacion != null && !imgOrganizacion.isEmpty()) {
            return imgOrganizacion;
        }
        String text = nombreOrganizacion != null ? nombreOrganizacion.replace(" ", "+") : "Actividad";
        return "https://placehold.co/600x400/4E6AF3/ffffff?text=" + text;
    }
}
