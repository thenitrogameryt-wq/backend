package com.example.backend.controller;

import com.example.backend.entidades.Documento;
import com.example.backend.service.ActividadService;
import com.example.backend.service.AuthService;
import com.example.backend.service.DocumentoService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.servlet.http.HttpSession;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.time.LocalDate;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/documentos")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
public class DocumentoController {

    @Autowired
    private DocumentoService service;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AuthService authService;

    @Autowired
    private ActividadService actividadService;


    @GetMapping
    public List<Documento> listar(){

        return service.listar();

    }

    @GetMapping("/{id}")
    public Documento obtener(@PathVariable String id){

        return service.obtener(id);

    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Documento guardar(

            @RequestParam String nombre,
            @RequestParam String categoria,
            @RequestParam String fechaPublicacion,
            @RequestParam MultipartFile archivo,
            HttpSession session

    ) throws IOException {

        authService.verificarSesion(session);

        Documento documento = new Documento();

        documento.setNombre(nombre);
        documento.setCategoria(categoria);

        documento.setFechaPublicacion(
                LocalDate.parse(fechaPublicacion)
        );

        Documento resultado = service.guardar(
                documento,
                archivo
        );

        String usuario =
                (String) session.getAttribute("usuario");

        actividadService.registrar(
                usuario,
                "Publicó un documento",
                "Documentos"
        );

        return resultado;
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Documento actualizar(

            @PathVariable String id,

            @RequestParam String nombre,

            @RequestParam String categoria,

            @RequestParam String fechaPublicacion,

            @RequestParam(required = false)
            MultipartFile archivo,

            HttpSession session

    ) throws IOException {

        authService.verificarSesion(session);

        Documento resultado =
                service.actualizar(
                        id,
                        nombre,
                        categoria,
                        fechaPublicacion,
                        archivo
                );

        String usuario =
                (String) session.getAttribute("usuario");

        actividadService.registrar(
                usuario,
                "Modificó un documento",
                "Documentos"
        );

        return resultado;
    }

    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable String id,
            HttpSession session
    ) throws IOException {

        authService.verificarSesion(session);

        service.eliminar(id);

        String usuario =
                (String) session.getAttribute("usuario");

        actividadService.registrar(
                usuario,
                "Eliminó un documento",
                "Documentos"
        );
    }

    @GetMapping("/documentos")
    public List<Document> documentos() {
        return mongoTemplate
                .getCollection("ascope_bd")
                .find()
                .into(new ArrayList<>());
    }

    @GetMapping("/archivo/{nombre:.+}")
    public ResponseEntity<Resource> obtenerArchivo(
            @PathVariable String nombre) throws IOException {

        Path ruta = Paths.get("uploads/documentos").resolve(nombre);

        Resource recurso = new UrlResource(ruta.toUri());

        if (!recurso.exists() || !recurso.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String extension = nombre.substring(
                nombre.lastIndexOf('.') + 1
        ).toLowerCase();

        HttpHeaders headers = new HttpHeaders();

        if (extension.equals("pdf")) {

            headers.add(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + nombre + "\""
            );

            headers.setContentType(MediaType.APPLICATION_PDF);

        } else {

            headers.add(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + nombre + "\""
            );

            if (extension.equals("doc")) {

                headers.set(
                        HttpHeaders.CONTENT_TYPE,
                        "application/msword"
                );

            } else if (extension.equals("docx")) {

                headers.set(
                        HttpHeaders.CONTENT_TYPE,
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                );

            }

        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(recurso);

    }

}