package com.example.backend.controller;

import java.util.List;
import java.util.Optional;

import com.example.backend.entidades.Usuario;
import com.example.backend.service.ActividadService;
import com.example.backend.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(
        origins = {"http://localhost:5173",
                "https://frontend-fdgz-phi.vercel.app"
        },
        allowCredentials = "true"
)
public class UsuarioController {

    private final UsuarioService service;
    private final ActividadService actividadService;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public UsuarioController(
            UsuarioService service,
            ActividadService actividadService) {

        this.service = service;
        this.actividadService = actividadService;
    }


    // =====================================================
    // LISTAR
    // =====================================================

    @GetMapping
    public List<Usuario> listar() {

        // Desactivar usuarios cuyo último acceso
        // supera los 10 minutos.

        service.actualizarUsuariosInactivos();

        return service.listar();
    }


    // =====================================================
    // BUSCAR
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(
            @PathVariable String id) {

        return service.buscar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // =====================================================
    // GUARDAR
    // =====================================================

    @PostMapping
    public Usuario guardar(
            @RequestBody Usuario usuario) {

        return service.guardar(usuario);
    }


    // =====================================================
    // ACTUALIZAR
    // =====================================================

    @PutMapping("/{id}")
    public Usuario actualizar(
            @PathVariable String id,
            @RequestBody Usuario usuario) {

        return service.actualizar(id, usuario);
    }


    // =====================================================
    // ELIMINAR
    // =====================================================

    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable String id) {

        service.eliminar(id);
    }


    // =====================================================
    // VERIFICAR SESIÓN
    // =====================================================

    @GetMapping("/auth/verificar")
    public ResponseEntity<?> verificar(
            HttpSession session) {

        Object usuarioSesion =
                session.getAttribute("usuario");


        // -------------------------------------------------
        // NO EXISTE SESIÓN
        // -------------------------------------------------

        if (usuarioSesion == null) {

            return ResponseEntity
                    .status(401)
                    .build();
        }


        String nombreUsuario =
                usuarioSesion.toString();


        // -------------------------------------------------
        // BUSCAR USUARIO
        // -------------------------------------------------

        Optional<Usuario> usuario =
                service.buscarPorUsuario(nombreUsuario);


        if (usuario.isEmpty()) {

            session.invalidate();

            return ResponseEntity
                    .status(401)
                    .build();
        }


        Usuario usuarioEncontrado =
                usuario.get();


        // -------------------------------------------------
        // USUARIO INACTIVO
        // -------------------------------------------------

        if (!usuarioEncontrado.isActivo()) {

            session.invalidate();

            return ResponseEntity
                    .status(401)
                    .build();
        }


        // -------------------------------------------------
        // SESIÓN VÁLIDA
        // -------------------------------------------------

        return ResponseEntity.ok(
                usuarioEncontrado
        );
    }


    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(
            @RequestBody Usuario datos,
            HttpSession session) {

        Optional<Usuario> usuario =
                service.login(
                        datos.getUsuario(),
                        datos.getPassword()
                );


        // -------------------------------------------------
        // CREDENCIALES INCORRECTAS
        // -------------------------------------------------

        if (usuario.isEmpty()) {

            return ResponseEntity
                    .status(401)
                    .build();
        }


        Usuario usuarioEncontrado =
                usuario.get();


        // -------------------------------------------------
        // GUARDAR USUARIO EN SESIÓN
        // -------------------------------------------------

        session.setAttribute(
                "usuario",
                usuarioEncontrado.getUsuario()
        );


        // -------------------------------------------------
        // GUARDAR ROL EN SESIÓN
        // -------------------------------------------------

        session.setAttribute(
                "rol",
                usuarioEncontrado.getRol()
        );


        // -------------------------------------------------
        // REGISTRAR ACTIVIDAD
        // -------------------------------------------------

        actividadService.registrar(
                usuarioEncontrado.getUsuario(),
                "Inició sesión",
                "Autenticación"
        );


        return ResponseEntity.ok(
                usuarioEncontrado
        );
    }


    // =====================================================
    // MANTENER SESIÓN ACTIVA
    // =====================================================

    @PostMapping("/actividad")
    public ResponseEntity<?> actualizarActividad(
            HttpSession session) {

        Object usuarioSesion =
                session.getAttribute("usuario");


        // -------------------------------------------------
        // NO EXISTE SESIÓN
        // -------------------------------------------------

        if (usuarioSesion == null) {

            return ResponseEntity
                    .status(401)
                    .build();
        }


        String nombreUsuario =
                usuarioSesion.toString();


        // -------------------------------------------------
        // BUSCAR USUARIO
        // -------------------------------------------------

        Optional<Usuario> usuario =
                service.buscarPorUsuario(nombreUsuario);


        if (usuario.isEmpty()) {

            session.invalidate();

            return ResponseEntity
                    .status(401)
                    .build();
        }


        Usuario usuarioEncontrado =
                usuario.get();


        // -------------------------------------------------
        // USUARIO INACTIVO
        // -------------------------------------------------

        if (!usuarioEncontrado.isActivo()) {

            session.invalidate();

            return ResponseEntity
                    .status(401)
                    .build();
        }


        // -------------------------------------------------
        // ACTUALIZAR ÚLTIMO ACCESO
        // -------------------------------------------------

        service.registrarAcceso(
                usuarioEncontrado
        );


        return ResponseEntity.ok().build();
    }


    // =====================================================
    // LOGOUT
    // =====================================================

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpSession session) {

        Object usuarioSesion =
                session.getAttribute("usuario");


        // -------------------------------------------------
        // NO EXISTE SESIÓN
        // -------------------------------------------------

        if (usuarioSesion == null) {

            session.invalidate();

            return ResponseEntity.ok().build();
        }


        String nombreUsuario =
                usuarioSesion.toString();


        // -------------------------------------------------
        // BUSCAR USUARIO
        // -------------------------------------------------

        Optional<Usuario> usuario =
                service.buscarPorUsuario(nombreUsuario);


        if (usuario.isPresent()) {

            Usuario usuarioEncontrado =
                    usuario.get();


            // ---------------------------------------------
            // MARCAR COMO INACTIVO
            // ---------------------------------------------

            service.cerrarSesion(
                    usuarioEncontrado
            );


            // ---------------------------------------------
            // REGISTRAR ACTIVIDAD
            // ---------------------------------------------

            actividadService.registrar(
                    usuarioEncontrado.getUsuario(),
                    "Cerró sesión",
                    "Seguridad"
            );
        }


        // -------------------------------------------------
        // DESTRUIR SESIÓN
        // -------------------------------------------------

        session.invalidate();


        return ResponseEntity.ok().build();
    }
}