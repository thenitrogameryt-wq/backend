package com.example.backend.service;

import com.example.backend.entidades.Noticia;
import com.example.backend.repository.NoticiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import java.util.List;

@Service
public class NoticiaService {

    private final NoticiaRepository repository;

    public NoticiaService(NoticiaRepository repository) {
        this.repository = repository;
    }

    public List<Noticia> listarTodas() {
        return repository.findAll();
    }

    public Noticia obtenerPorId(String id) {
        return repository.findById(id).orElse(null);
    }

    public void eliminar(String id) {
        repository.deleteById(id);
    }

    public Noticia guardar(
            Noticia noticia,
            MultipartFile imagen
    ) throws IOException {

        if (imagen != null && !imagen.isEmpty()) {

            Path carpeta =
                    Paths.get("uploads/noticias");

            if (!Files.exists(carpeta)) {

                Files.createDirectories(carpeta);

            }

            String nombreArchivo =
                    UUID.randomUUID()
                            + "_"
                            + imagen.getOriginalFilename();

            Path ruta =
                    carpeta.resolve(nombreArchivo);

            Files.copy(
                    imagen.getInputStream(),
                    ruta,
                    StandardCopyOption.REPLACE_EXISTING
            );

            noticia.setImagen(
                    "/uploads/noticias/"
                            + nombreArchivo
            );
        }

        return repository.save(noticia);
    }
}