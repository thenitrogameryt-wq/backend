package com.example.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.dto.ActualizarPerfilRequest;
import com.example.backend.dto.CambiarPasswordRequest;
import com.example.backend.entidades.Rol;
import com.example.backend.entidades.Usuario;
import com.example.backend.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // =====================================================
    // LISTAR
    // =====================================================

    public List<Usuario> listar() {
        return repository.findAll();
    }

    // =====================================================
    // BUSCAR
    // =====================================================

    public Optional<Usuario> buscar(String id) {
        return repository.findById(id);
    }

    // =====================================================
    // BUSCAR POR USUARIO
    // =====================================================

    public Optional<Usuario> buscarPorUsuario(String usuario) {
        return repository.findByUsuario(usuario);
    }

    // =====================================================
    // CREAR
    // =====================================================

    public Usuario guardar(Usuario usuario) {

        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(LocalDateTime.now());
        }

        usuario.setFechaActualizacion(LocalDateTime.now());

        // Usuario nuevo: siempre comienza inactivo
        usuario.setActivo(false);

        if (usuario.getPassword() != null &&
                !usuario.getPassword().isBlank()) {

            usuario.setPassword(
                    passwordEncoder.encode(
                            usuario.getPassword()
                    )
            );
        }

        if (usuario.getRol() == null) {
            usuario.setRol(Rol.PERSONAL_ADMINISTRATIVO);
        }

        if (usuario.getFotoPerfil() == null ||
                usuario.getFotoPerfil().isBlank()) {

            usuario.setFotoPerfil(
                    "https://thumbs.dreamstime.com/b/default-avatar-profile-icon-vector-social-media-user-photo-183042379.jpg"
            );
        }

        return repository.save(usuario);
    }

    // =====================================================
    // ACTUALIZAR USUARIO
    // =====================================================

    public Usuario actualizar(
            String id,
            Usuario datos) {

        Usuario actual = repository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Usuario no encontrado"
                        )
                );

        actual.setNombres(datos.getNombres());
        actual.setApellidos(datos.getApellidos());
        actual.setCorreo(datos.getCorreo());
        actual.setUsuario(datos.getUsuario());
        actual.setRol(datos.getRol());

        actual.setFechaActualizacion(
                LocalDateTime.now()
        );

        if (actual.getFotoPerfil() == null ||
                actual.getFotoPerfil().isBlank()) {

            actual.setFotoPerfil(
                    "https://thumbs.dreamstime.com/b/default-avatar-profile-icon-vector-social-media-user-photo-183042379.jpg"
            );
        }

        return repository.save(actual);
    }

    // =====================================================
    // ACTUALIZAR PERFIL
    // =====================================================

    public Usuario actualizarPerfil(
            Usuario actual,
            ActualizarPerfilRequest datos) {

        actual.setNombres(datos.getNombres());
        actual.setApellidos(datos.getApellidos());
        actual.setCorreo(datos.getCorreo());
        actual.setUsuario(datos.getUsuario());

        actual.setFechaActualizacion(
                LocalDateTime.now()
        );

        return repository.save(actual);
    }

    // =====================================================
    // CAMBIAR PASSWORD
    // =====================================================

    public void cambiarPassword(
            Usuario usuario,
            CambiarPasswordRequest datos) {

        if (datos.getPasswordActual() == null ||
                datos.getPasswordNueva() == null ||
                datos.getConfirmarPassword() == null) {

            throw new RuntimeException(
                    "Todos los campos son obligatorios"
            );
        }

        if (!passwordEncoder.matches(
                datos.getPasswordActual(),
                usuario.getPassword())) {

            throw new RuntimeException(
                    "La contraseña actual es incorrecta"
            );
        }

        if (datos.getPasswordNueva().length() < 6) {

            throw new RuntimeException(
                    "La nueva contraseña debe contener al menos 6 caracteres"
            );
        }

        if (!datos.getPasswordNueva().equals(
                datos.getConfirmarPassword())) {

            throw new RuntimeException(
                    "Las contraseñas nuevas no coinciden"
            );
        }

        usuario.setPassword(
                passwordEncoder.encode(
                        datos.getPasswordNueva()
                )
        );

        usuario.setFechaActualizacion(
                LocalDateTime.now()
        );

        repository.save(usuario);
    }

    // =====================================================
    // REGISTRAR ACCESO
    // =====================================================

    public void registrarAcceso(Usuario usuario) {

        usuario.setActivo(true);

        usuario.setFechaUltimoAcceso(
                LocalDateTime.now()
        );

        usuario.setFechaActualizacion(
                LocalDateTime.now()
        );

        repository.save(usuario);
    }

    // =====================================================
    // ELIMINAR
    // =====================================================

    public void eliminar(String id) {
        repository.deleteById(id);
    }

    // =====================================================
    // LOGIN
    // =====================================================

    public Optional<Usuario> login(
            String nombreUsuario,
            String password) {

        return repository.findByUsuario(nombreUsuario)
                .filter(u ->
                        passwordEncoder.matches(
                                password,
                                u.getPassword()
                        )
                )
                .map(u -> {

                    u.setActivo(true);

                    u.setFechaUltimoAcceso(
                            LocalDateTime.now()
                    );

                    return repository.save(u);
                });
    }

    // =====================================================
    // ESTADÍSTICAS
    // =====================================================

    public long total() {
        return repository.count();
    }

    public long activos() {
        return repository.countByActivoTrue();
    }

    public long inactivos() {
        return repository.countByActivoFalse();
    }

    public long administradores() {
        return repository.countByRol(Rol.ADMIN);
    }

    public long personalAdministrativo() {
        return repository.countByRol(
                Rol.PERSONAL_ADMINISTRATIVO
        );
    }

    // =====================================================
    // FILTROS
    // =====================================================

    public List<Usuario> buscarPorEstado(
            boolean activo) {

        return repository.findByActivo(activo);
    }

    public List<Usuario> buscarPorRol(Rol rol) {

        return repository.findByRol(rol);
    }

    public void cerrarSesion(Usuario usuario) {
        usuario.setActivo(false);
        repository.save(usuario);
    }

    // =====================================================
// DESACTIVAR USUARIOS INACTIVOS
// =====================================================

    public void actualizarUsuariosInactivos() {

        LocalDateTime limite =
                LocalDateTime.now().minusMinutes(10);

        List<Usuario> usuarios =
                repository.findByActivoTrue();

        for (Usuario usuario : usuarios) {

            LocalDateTime ultimoAcceso =
                    usuario.getFechaUltimoAcceso();

            if (ultimoAcceso == null) {

                // Si está activo pero nunca tiene
                // registrado un acceso, lo dejamos
                // como inactivo para evitar usuarios
                // permanentemente activos.

                usuario.setActivo(false);

                usuario.setFechaActualizacion(
                        LocalDateTime.now()
                );

                repository.save(usuario);

                continue;
            }

            if (ultimoAcceso.isBefore(limite)) {

                usuario.setActivo(false);

                usuario.setFechaActualizacion(
                        LocalDateTime.now()
                );

                repository.save(usuario);
            }
        }
    }
}