package com.example.backend.service;

import com.example.backend.entidades.Actividad;
import com.example.backend.repository.ActividadRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActividadService {

    private final ActividadRepository repository;

    public ActividadService(ActividadRepository repository) {
        this.repository = repository;
    }

    public Actividad registrar(
            String usuario,
            String accion,
            String modulo) {

        Actividad actividad = new Actividad();

        actividad.setFecha(LocalDateTime.now());
        actividad.setUsuario(usuario);
        actividad.setAccion(accion);
        actividad.setModulo(modulo);

        return repository.save(actividad);
    }

    public List<Actividad> obtenerRecientes() {

        return repository.findTop10ByOrderByFechaDesc();

    }
}