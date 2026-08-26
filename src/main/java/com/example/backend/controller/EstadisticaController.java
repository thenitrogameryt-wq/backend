package com.example.backend.controller;

import com.example.backend.dto.EstadisticasDTO;
import com.example.backend.service.EstadisticaService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/estadisticas")
@CrossOrigin(
        origins = {"http://localhost:5173",
                "https://frontend-fdgz-phi.vercel.app",
                "https://hoppscotch.io"
        },
        allowCredentials = "true"
)
public class EstadisticaController {

    private final EstadisticaService service;

    public EstadisticaController(
            EstadisticaService service) {

        this.service = service;
    }

    // =====================================================
    // OBTENER ESTADÍSTICAS
    // =====================================================

    @GetMapping
    public EstadisticasDTO obtenerEstadisticas(

            @RequestParam(
                    defaultValue = "2026"
            )
            int anio,

            @RequestParam(
                    defaultValue = "todo"
            )
            String periodo) {

        return service.obtenerEstadisticas(
                anio,
                periodo
        );
    }
}