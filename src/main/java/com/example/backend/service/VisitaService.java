package com.example.backend.service;

import com.example.backend.entidades.Visita;
import com.example.backend.repository.VisitaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitaService {

    private final VisitaRepository repository;

    public VisitaService(VisitaRepository repository) {
        this.repository = repository;
    }

    public List<Visita> obtenerVisitas() {
        return repository.findAll();
    }

    public Visita guardarVisita(Visita visita) {
        return repository.save(visita);
    }

    public List<Visita> guardarTodas(List<Visita> visitas) {
        return repository.saveAll(visitas);
    }

    public void eliminarTodo() {
        repository.deleteAll();
    }

}