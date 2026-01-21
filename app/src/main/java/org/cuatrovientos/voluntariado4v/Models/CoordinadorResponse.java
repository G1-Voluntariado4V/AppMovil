package org.cuatrovientos.voluntariado4v.Models;

public class CoordinadorResponse {
    private int id;
    private String nombre;
    private String apellidos; // Nuevo campo
    private String telefono;  // Nuevo campo
    private String rol;
    private String correo;
    private String estado_cuenta;

    // Constructor vacío necesario para deserialización
    public CoordinadorResponse() {}

    public CoordinadorResponse(int id, String nombre, String apellidos, String telefono, String rol, String correo, String estado_cuenta) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.rol = rol;
        this.correo = correo;
        this.estado_cuenta = estado_cuenta;
    }

    // --- GETTERS ---
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getRol() {
        return rol;
    }

    public String getCorreo() {
        return correo;
    }

    public String getEstadoCuenta() {
        return estado_cuenta;
    }
}