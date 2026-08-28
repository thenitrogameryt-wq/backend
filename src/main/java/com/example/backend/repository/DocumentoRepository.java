package com.example.backend.repository;

import com.example.backend.entidades.Documento;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface DocumentoRepository extends MongoRepository<Documento, String> {

    List<Documento> findByFechaPublicacionBetween(LocalDateTime inicio, LocalDateTime fin);
    long countByFechaPublicacionBetween(LocalDateTime inicio, LocalDateTime fin);
}