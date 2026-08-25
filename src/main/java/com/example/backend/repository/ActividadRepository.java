package com.example.backend.repository;

import com.example.backend.entidades.Actividad;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActividadRepository extends MongoRepository<Actividad, String> {

    List<Actividad> findTop10ByOrderByFechaDesc();

}