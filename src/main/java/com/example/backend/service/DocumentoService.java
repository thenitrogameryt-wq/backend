package com.example.backend.service;

import com.example.backend.entidades.Documento;
import com.example.backend.repository.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepository repository;

    private final Path carpeta =
            Paths.get("uploads/documentos");

    public List<Documento> listar() {

        return repository.findAll();

    }

    public Documento obtener(String id) {

        return repository.findById(id)
                .orElse(null);

    }

    private String obtenerExtension(String nombre) {

        int punto = nombre.lastIndexOf(".");

        if (punto == -1) {
            return "";
        }

        return nombre
                .substring(punto + 1)
                .toLowerCase();

    }

    private void validarArchivo(MultipartFile archivo) {

        if (archivo == null || archivo.isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un documento."
            );

        }

        String nombreOriginal =
                archivo.getOriginalFilename();

        if (nombreOriginal == null ||
                nombreOriginal.isBlank()) {

            throw new IllegalArgumentException(
                    "Nombre de archivo inválido."
            );

        }

        String extension =
                obtenerExtension(nombreOriginal);

        if (!extension.equals("pdf")
                && !extension.equals("doc")
                && !extension.equals("docx")) {

            throw new IllegalArgumentException(
                    "Solo se permiten archivos PDF, DOC y DOCX."
            );

        }

    }


    // =========================================================
    // AGREGAR
    // =========================================================

    public Documento guardar(
            Documento documento,
            MultipartFile archivo
    ) throws IOException {

        validarArchivo(archivo);

        Files.createDirectories(carpeta);

        String nombreOriginal =
                archivo.getOriginalFilename();

        String extension =
                obtenerExtension(nombreOriginal);

        String nombreArchivo =
                UUID.randomUUID()
                        + "_"
                        + nombreOriginal;

        Path ruta =
                carpeta.resolve(nombreArchivo);

        Files.copy(
                archivo.getInputStream(),
                ruta,
                StandardCopyOption.REPLACE_EXISTING
        );

        documento.setArchivo(nombreArchivo);

        documento.setTipo(
                extension.toUpperCase()
        );

        return repository.save(documento);

    }


    // =========================================================
    // MODIFICAR
    // =========================================================

    public Documento actualizar(
            String id,
            String nombre,
            String categoria,
            String fechaPublicacion,
            MultipartFile nuevoArchivo
    ) throws IOException {

        Documento actual =
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Documento no encontrado."
                                )
                        );


        /*
         * Actualizar información básica
         */

        actual.setNombre(nombre);

        actual.setCategoria(categoria);

        actual.setFechaPublicacion(
                java.time.LocalDate.parse(
                        fechaPublicacion
                )
        );


        /*
         * Si el usuario seleccionó
         * un nuevo archivo:
         *
         * 1. eliminar archivo anterior
         * 2. guardar archivo nuevo
         * 3. actualizar ruta/nombre
         */

        if (nuevoArchivo != null &&
                !nuevoArchivo.isEmpty()) {

            validarArchivo(nuevoArchivo);

            Files.createDirectories(carpeta);

            /*
             * Eliminar archivo anterior
             */

            String archivoAnterior =
                    actual.getArchivo();

            if (archivoAnterior != null &&
                    !archivoAnterior.isBlank()) {

                Path rutaAnterior =
                        carpeta.resolve(
                                archivoAnterior
                        );

                Files.deleteIfExists(
                        rutaAnterior
                );

            }


            /*
             * Guardar nuevo archivo
             */

            String nombreOriginal =
                    nuevoArchivo.getOriginalFilename();

            String extension =
                    obtenerExtension(
                            nombreOriginal
                    );

            String nuevoNombre =
                    UUID.randomUUID()
                            + "_"
                            + nombreOriginal;

            Path nuevaRuta =
                    carpeta.resolve(nuevoNombre);

            Files.copy(
                    nuevoArchivo.getInputStream(),
                    nuevaRuta,
                    StandardCopyOption.REPLACE_EXISTING
            );


            /*
             * Actualizar datos del archivo
             */

            actual.setArchivo(nuevoNombre);

            actual.setTipo(
                    extension.toUpperCase()
            );

        }


        return repository.save(actual);

    }


    // =========================================================
    // ELIMINAR
    // =========================================================

    public void eliminar(String id)
            throws IOException {

        Documento documento =
                repository.findById(id)
                        .orElse(null);

        if (documento == null) {
            return;
        }


        /*
         * Primero eliminar archivo físico
         */

        String nombreArchivo =
                documento.getArchivo();

        if (nombreArchivo != null &&
                !nombreArchivo.isBlank()) {

            Path ruta =
                    carpeta.resolve(
                            nombreArchivo
                    );

            Files.deleteIfExists(ruta);

        }


        /*
         * Luego eliminar registro MongoDB
         */

        repository.deleteById(id);

    }

}