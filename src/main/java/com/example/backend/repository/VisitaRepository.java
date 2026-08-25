package com.example.backend.repository;

import com.example.backend.entidades.Visita;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VisitaRepository extends MongoRepository<Visita, String> {

}
