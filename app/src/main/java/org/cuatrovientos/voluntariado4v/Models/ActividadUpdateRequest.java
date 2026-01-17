package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActividadUpdateRequest {

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("fecha_inicio")
    private String fechaInicio;

    @SerializedName("duracion_horas")
    private int duracionHoras;

    @SerializedName("cupo_maximo")
    private int cupoMaximo;

    @SerializedName("ubicacion")
    private String ubicacion;

    @SerializedName("odsIds")
    private List<Integer> odsIds;

    @SerializedName("tiposIds")
    private List<Integer> tiposIds;

    public ActividadUpdateRequest(String titulo, String descripcion, String fechaInicio,
            int duracionHoras, int cupoMaximo, String ubicacion,
            List<Integer> odsIds, List<Integer> tiposIds) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.duracionHoras = duracionHoras;
        this.cupoMaximo = cupoMaximo;
        this.ubicacion = ubicacion;
        this.odsIds = odsIds;
        this.tiposIds = tiposIds;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
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

    public String getUbicacion() {
        return ubicacion;
    }

    public List<Integer> getOdsIds() {
        return odsIds;
    }

    public List<Integer> getTiposIds() {
        return tiposIds;
    }
}
