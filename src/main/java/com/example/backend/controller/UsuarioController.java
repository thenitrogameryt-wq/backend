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
                "https://frontend-fdgz-phi-nu.vercel.app"
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

        System.out.println(
                "VERIFICAR SESSION ID: "
                        + session.getId()
        );

        System.out.println(
                "VERIFICAR USUARIO SESSION: "
                        + session.getAttribute("usuario")
        );


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

        System.out.println(
                "LOGIN SESSION ID: "
                        + session.getId()
        );

        System.out.println(
                "LOGIN USUARIO SESSION: "
                        + session.getAttribute("usuario")
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

    // =====================================================
    // CAMBIAR FOTO DE PERFIL
    // =====================================================

    @PutMapping("/{id}/foto")
    public ResponseEntity<?> cambiarFotoPerfil(
            @PathVariable String id,
            @RequestParam("foto") org.springframework.web.multipart.MultipartFile foto) {

        System.out.println("======================================");
        System.out.println("📸 CAMBIO DE FOTO - BACKEND");
        System.out.println("======================================");

        System.out.println("🆔 ID recibido: " + id);

        if (foto == null || foto.isEmpty()) {

            System.out.println("❌ No se recibió ningún archivo.");

            return ResponseEntity
                    .badRequest()
                    .body("No se recibió ninguna imagen.");
        }

        System.out.println("📄 Nombre: " + foto.getOriginalFilename());
        System.out.println("📦 Tamaño: " + foto.getSize());
        System.out.println("🖼️ Tipo: " + foto.getContentType());

        try {

            Usuario usuarioActualizado =
                    service.actualizarFotoPerfil(id, foto);

            System.out.println("✅ Foto actualizada correctamente.");
            System.out.println("🆔 Usuario: " + usuarioActualizado.getId());
            System.out.println("👤 Usuario: " + usuarioActualizado.getUsuario());

            System.out.println("======================================");

            return ResponseEntity.ok(usuarioActualizado);

        } catch (Exception error) {

            System.out.println("❌ ERROR ACTUALIZANDO FOTO");
            error.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body("No se pudo actualizar la foto de perfil.");
        }
    }


}