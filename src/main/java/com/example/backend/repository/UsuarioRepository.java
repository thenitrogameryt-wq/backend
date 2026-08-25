package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend.entidades.Rol;
import com.example.backend.entidades.Usuario;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository
        extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByUsuario(String usuario);

    Optional<Usuario> findByCorreo(String correo);

    long countByActivoTrue();

    long countByActivoFalse();

    long countByRol(Rol rol);

    List<Usuario> findByActivo(boolean activo);

    List<Usuario> findByRol(Rol rol);

    boolean existsByUsuario(String usuario);

    boolean existsByCorreo(String correo);

    boolean existsByUsuarioAndIdNot(String usuario, String id);

    boolean existsByCorreoAndIdNot(String correo, String id);

    List<Usuario> findByActivoTrue();
}