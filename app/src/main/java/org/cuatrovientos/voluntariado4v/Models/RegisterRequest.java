package org.cuatrovientos.voluntariado4v.Models;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    @SerializedName("google_id")
    private String googleId;

    @SerializedName("correo")
    private String correo;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;

    // TODO: Añadir campos al formulario para capturar datos reales
    @SerializedName("dni")
    private String dni;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("fecha_nac")
    private String fechaNac;

    @SerializedName("carnet_conducir")
    private boolean carnetConducir;

    @SerializedName("id_curso_actual")
    private int idCursoActual;

    public RegisterRequest(String googleId, String correo, String nombre, String apellidos,
            String dni, String telefono, String fechaNac,
            boolean carnetConducir, int idCursoActual) {
        this.googleId = googleId;
        this.correo = correo;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.telefono = telefono;
        this.fechaNac = fechaNac;
        this.carnetConducir = carnetConducir;
        this.idCursoActual = idCursoActual;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getDni() {
        return dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public boolean isCarnetConducir() {
        return carnetConducir;
    }

    public int getIdCursoActual() {
        return idCursoActual;
    }
}
