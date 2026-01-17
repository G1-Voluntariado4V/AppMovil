package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * DTO para actualizar el perfil del voluntario.
 * Corresponde a VoluntarioUpdateDTO del backend.
 */
public class VoluntarioUpdateRequest {

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("fechaNac")
    private String fechaNac;

    @SerializedName("carnet_conducir")
    private Boolean carnetConducir;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("preferencias_ids")
    private List<Integer> preferenciasIds;

    public VoluntarioUpdateRequest(String nombre, String apellidos, String telefono,
            String fechaNac, Boolean carnetConducir, String descripcion) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.fechaNac = fechaNac;
        this.carnetConducir = carnetConducir;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public Boolean getCarnetConducir() {
        return carnetConducir;
    }

    public void setCarnetConducir(Boolean carnetConducir) {
        this.carnetConducir = carnetConducir;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Integer> getPreferenciasIds() {
        return preferenciasIds;
    }

    public void setPreferenciasIds(List<Integer> preferenciasIds) {
        this.preferenciasIds = preferenciasIds;
    }
}
