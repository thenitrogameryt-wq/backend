package com.example.backend.controller;

import com.example.backend.entidades.Actividad;
import com.example.backend.repository.ActividadRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/actividades")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "https://frontend-fdgz-phi.vercel.app",
                "https://hoppscotch.io"
        },
        allowCredentials = "true"
)
public class ActividadController {

    private final ActividadRepository repository;

    public ActividadController(
            ActividadRepository repository) {

        this.repository = repository;
    }

    @GetMapping
    public List<Actividad> listarActividades() {

        return repository.findAll();
    }

    @GetMapping("/recientes")
    public List<Actividad> listarActividadesRecientes() {

        return repository.findTop10ByOrderByFechaDesc();
    }
}