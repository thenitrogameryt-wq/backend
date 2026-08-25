package com.example.backend.service;

import com.example.backend.entidades.Agenda;
import com.example.backend.repository.AgendaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaService {

    private final AgendaRepository repository;

    public AgendaService(AgendaRepository repository) {
        this.repository = repository;
    }

    // ==============================
    // LISTAR
    // ==============================

    public List<Agenda> obtenerAgenda() {
        return repository.findAll();
    }


    // ==============================
    // AGREGAR / GUARDAR
    // ==============================

    public Agenda guardarAgenda(Agenda agenda) {
        return repository.save(agenda);
    }


    // ==============================
    // GUARDAR VARIAS
    // ==============================

    public List<Agenda> guardarTodas(List<Agenda> agenda) {
        return repository.saveAll(agenda);
    }


    // ==============================
    // ELIMINAR POR ID
    // ==============================

    public void eliminarAgenda(String id) {
        repository.deleteById(id);
    }


    // ==============================
    // ELIMINAR TODO
    // ==============================

    public void eliminarTodo() {
        repository.deleteAll();
    }
}