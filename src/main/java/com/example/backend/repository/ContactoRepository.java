package com.example.backend.repository;

import com.example.backend.entidades.Contacto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactoRepository extends MongoRepository<Contacto, String> {
}
