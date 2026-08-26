package com.example.backend.controller;

import com.example.backend.entidades.Noticia;
import com.example.backend.service.ActividadService;
import com.example.backend.service.AuthService;
import com.example.backend.service.NoticiaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(
        origins = {"http://localhost:5173",
                "https://frontend-fdgz-phi.vercel.app"
        },
        allowCredentials = "true"
)
@RestController
@RequestMapping("/noticias")
public class NoticiaController {

    private final NoticiaService service;
    private ActividadService actividadService;

    @Autowired
    private AuthService authService;

    public NoticiaController(
            NoticiaService service,
            ActividadService actividadService) {

        this.service = service;
        this.actividadService = actividadService;
    }

    @GetMapping
    public List<Noticia> listar(){

        return service.listarTodas();

    }

    @GetMapping("/{id}")
    public Noticia obtener(
            @PathVariable String id){

        return service.obtenerPorId(id);

    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Noticia> crearNoticia(

            @RequestParam("titulo")
            String titulo,

            @RequestParam("resumen")
            String resumen,

            @RequestParam("contenido")
            String contenido,

            @RequestParam("categoria")
            String categoria,

            @RequestParam("fechaPublicacion")
            String fechaPublicacion,

            @RequestParam(value = "imagen", required = false)
            MultipartFile imagen,

            @RequestParam(value = "urlImagen", required = false)
            String urlImagen,

            HttpSession session
    ) {

        try {

            Noticia noticia = new Noticia();

            noticia.setTitulo(titulo);
            noticia.setResumen(resumen);
            noticia.setContenido(contenido);
            noticia.setCategoria(categoria);

            noticia.setFechaPublicacion(
                    LocalDate.parse(fechaPublicacion)
            );


            // ==========================================
            // IMAGEN
            // ==========================================

            if (imagen != null && !imagen.isEmpty()) {

                noticia =
                        service.guardar(
                                noticia,
                                imagen
                        );

            } else if (
                    urlImagen != null &&
                            !urlImagen.trim().isEmpty()
            ) {

                noticia.setImagen(
                        urlImagen.trim()
                );

                noticia =
                        service.guardar(
                                noticia,
                                null
                        );

            } else {

                return ResponseEntity
                        .badRequest()
                        .build();
            }


            // ==========================================
            // ACTIVIDAD
            // ==========================================

            String usuario =
                    (String) session.getAttribute("usuario");

            System.out.println(
                    "USUARIO SESION: " + usuario
            );

            if (usuario != null) {

                actividadService.registrar(
                        usuario,
                        "Registró una noticia",
                        "Noticias"
                );

                System.out.println(
                        "ACTIVIDAD REGISTRADA"
                );
            }


            return ResponseEntity.ok(noticia);


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable String id,
            HttpSession session) {

        authService.verificarSesion(session);

        service.eliminar(id);

        String usuario =
                (String) session.getAttribute("usuario");

        actividadService.registrar(
                usuario,
                "Eliminó una noticia",
                "Noticias"
        );
    }

}