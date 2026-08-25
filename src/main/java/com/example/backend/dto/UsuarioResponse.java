package com.example.backend.dto;

import java.time.LocalDateTime;
import com.example.backend.entidades.Usuario;
import com.example.backend.entidades.Rol;

public class UsuarioResponse {

    private String id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String usuario;
    private Rol rol;
    private boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaUltimoAcceso;
    private String fotoPerfil;

    public UsuarioResponse() {
    }

    public UsuarioResponse(
            String id,
            String nombres,
            String apellidos,
            String correo,
            String usuario,
            Rol rol,
            boolean activo,
            LocalDateTime fechaRegistro,
            LocalDateTime fechaActualizacion,
            LocalDateTime fechaUltimoAcceso,
            String fotoPerfil) {

        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.usuario = usuario;
        this.rol = rol;
        this.activo = activo;
        this.fechaRegistro = fechaRegistro;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaUltimoAcceso = fechaUltimoAcceso;
        this.fotoPerfil = fotoPerfil;
    }

    public static UsuarioResponse fromUsuario(Usuario u) {

        return new UsuarioResponse(
                u.getId(),
                u.getNombres(),
                u.getApellidos(),
                u.getCorreo(),
                u.getUsuario(),
                u.getRol(),
                u.isActivo(),
                u.getFechaRegistro(),
                u.getFechaActualizacion(),
                u.getFechaUltimoAcceso(),
                u.getFotoPerfil()
        );
    }

    public String getId() {
        return id;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public String getUsuario() {
        return usuario;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public LocalDateTime getFechaUltimoAcceso() {
        return fechaUltimoAcceso;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }
}