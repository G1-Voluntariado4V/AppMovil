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

    // --- Campo auxiliar para UI (no viene de API Actividades, se llena manual) ---
    private String estadoInscripcionUsuario;

    public String getEstadoInscripcionUsuario() {
        return estadoInscripcionUsuario;
    }

    public void setEstadoInscripcionUsuario(String estadoInscripcionUsuario) {
        this.estadoInscripcionUsuario = estadoInscripcionUsuario;
    }

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

    @SerializedName(value = "tipo", alternate = { "nombre_tipo", "tipo_nombre", "tipo_voluntariado", "categoria",
            "nombreTipo" })
    private String tipo;

    @SerializedName(value = "imagen_actividad", alternate = { "url_imagen", "imagen", "foto", "img", "image_url",
            "imageUrl" })
    private String imagenActividad;

    @SerializedName("id_organizacion")
    private int idOrganizacion;

    // --- GETTERS ---
    public int getIdOrganizacion() {
        return idOrganizacion;
    }

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

    public String getImagenActividad() {
        return imagenActividad;
    }

    public String getEstadoPublicacion() {
        return estadoPublicacion;
    }

    // --- SETTERS (Añadidos para poder mapear desde ActivityModel) ---
    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setDuracionHoras(int duracionHoras) {
        this.duracionHoras = duracionHoras;
    }

    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public void setInscritosConfirmados(int inscritosConfirmados) {
        this.inscritosConfirmados = inscritosConfirmados;
    }

    public void setNombreOrganizacion(String nombreOrganizacion) {
        this.nombreOrganizacion = nombreOrganizacion;
    }

    public void setImgOrganizacion(String imgOrganizacion) {
        this.imgOrganizacion = imgOrganizacion;
    }

    public void setImagenActividad(String imagenActividad) {
        this.imagenActividad = imagenActividad;
    }

    public void setEstadoPublicacion(String estadoPublicacion) {
        this.estadoPublicacion = estadoPublicacion;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setIdOrganizacion(int idOrganizacion) {
        this.idOrganizacion = idOrganizacion;
    }

    // --- Lógica de Negocio ---
    public boolean hayPlazasDisponibles() {
        return inscritosConfirmados < cupoMaximo;
    }

    public int getPlazasRestantes() {
        return cupoMaximo - inscritosConfirmados;
    }

    // --- Alias para compatibilidad ---
    public void setId(int id) {
        this.idActividad = id;
    }

    public int getId() {
        return idActividad;
    }

    // --- Soporte Lista de Tipos ---
    @SerializedName("tipos")
    private java.util.List<TipoVoluntariadoResponse> tiposList;

    // --- Soporte Lista de ODS ---
    @SerializedName("ods")
    private java.util.List<OdsResponse> odsList;

    public void setTipos(java.util.List<TipoVoluntariadoResponse> tipos) {
        this.tiposList = tipos;
        if (tipos != null && !tipos.isEmpty()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                java.util.StringJoiner joiner = new java.util.StringJoiner(", ");
                for (TipoVoluntariadoResponse t : tipos) {
                    if (t.getNombre() != null)
                        joiner.add(t.getNombre());
                }
                this.tipo = joiner.toString();
            } else {
                this.tipo = tipos.get(0).getNombre();
            }
        }
    }

    public java.util.List<TipoVoluntariadoResponse> getTipos() {
        return tiposList;
    }

    public void setOds(java.util.List<OdsResponse> ods) {
        this.odsList = ods;
    }

    public java.util.List<OdsResponse> getOds() {
        return odsList;
    }

    public String getTipo() {
        if (tipo != null && !tipo.isEmpty())
            return tipo;
        if (tiposList != null && !tiposList.isEmpty()) {
            return tiposList.get(0).getNombre();
        }
        return "Actividad";
    }

    public String getImageUrl() {
        // ... (resto igual)
        if (imagenActividad != null && !imagenActividad.isEmpty()) {
            return imagenActividad;
        }
        if (imgOrganizacion != null && !imgOrganizacion.isEmpty()) {
            return imgOrganizacion;
        }
        return "https://placehold.co/600x400/780000/ffffff.png?text=Error";
    }
}