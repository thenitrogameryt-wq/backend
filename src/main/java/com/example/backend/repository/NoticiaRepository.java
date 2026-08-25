package com.example.backend.repository;

import com.example.backend.entidades.Noticia;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoticiaRepository
        extends MongoRepository<Noticia, String> {
}