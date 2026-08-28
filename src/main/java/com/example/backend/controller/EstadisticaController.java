package com.example.backend.controller;

import com.example.backend.dto.EstadisticasDTO;
import com.example.backend.service.EstadisticaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/estadisticas")
@CrossOrigin(
        origins = {"http://localhost:5173",
                "https://frontend-fdgz-phi-nu.vercel.app",
                "https://hoppscotch.io"
        },
        allowCredentials = "true"
)
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    public EstadisticaController(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    // =====================================================
    // OBTENER ESTADÍSTICAS
    // =====================================================

    @GetMapping
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticas(
            @RequestParam(defaultValue = "2026") int anio,
            @RequestParam(defaultValue = "todo") String periodo) {

        EstadisticasDTO dto = estadisticaService.obtenerEstadisticas(anio, periodo);
        return ResponseEntity.ok(dto);
    }
}