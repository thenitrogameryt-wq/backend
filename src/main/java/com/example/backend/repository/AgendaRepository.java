package com.example.backend.repository;

import com.example.backend.entidades.Agenda;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendaRepository extends MongoRepository<Agenda, String> {

    // Contar total entre dos fechas
    long countByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    // Obtener lista para agrupar por mes en Java
    List<Agenda> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}