package com.example.backend.controller;

import com.example.backend.entidades.Visita;
import com.example.backend.service.ActividadService;
import com.example.backend.service.VisitaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/visitas")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "https://hoppscotch.io"
        },
        allowCredentials = "true"
)
public class VisitaController {

    private final VisitaService service;
    private final ActividadService actividadService;

    public VisitaController(
            VisitaService service,
            ActividadService actividadService) {

        this.service = service;
        this.actividadService = actividadService;
    }

    @GetMapping
    public List<Visita> listarVisitas() {

        return service.obtenerVisitas();

    }

    @PostMapping
    public Visita crearVisita(
            @RequestBody Visita visita,
            HttpSession session) {

        Visita resultado = service.guardarVisita(visita);

        String usuario =
                (String) session.getAttribute("usuario");

        if (usuario != null) {

            actividadService.registrar(
                    usuario,
                    "Registró una visita",
                    "Visitas"
            );

        }

        return resultado;
    }

    @PostMapping("/lote")
    public List<Visita> crearVisitas(
            @RequestBody List<Visita> visitas,
            HttpSession session) {

        List<Visita> resultado =
                service.guardarTodas(visitas);

        String usuario =
                (String) session.getAttribute("usuario");

        if (usuario != null) {

            actividadService.registrar(
                    usuario,
                    "Registró visitas",
                    "Visitas"
            );

        }

        return resultado;
    }
}