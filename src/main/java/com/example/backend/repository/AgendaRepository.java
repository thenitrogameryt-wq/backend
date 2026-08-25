package com.example.backend.repository;

import com.example.backend.entidades.Agenda;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AgendaRepository extends MongoRepository<Agenda, String> {

}