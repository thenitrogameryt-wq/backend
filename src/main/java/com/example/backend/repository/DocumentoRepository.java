package com.example.backend.repository;

import com.example.backend.entidades.Documento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoRepository
        extends MongoRepository<Documento, String> {
}
