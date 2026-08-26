package com.example.backend.controller;

import com.example.backend.entidades.Agenda;
import com.example.backend.service.ActividadService;
import com.example.backend.service.AgendaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agenda")
@CrossOrigin(
        origins = {"http://localhost:5173",
                "https://frontend-fdgz-phi-nu.vercel.app"
        },
        allowCredentials = "true"
)
public class AgendaController {

    private final AgendaService service;
    private final ActividadService actividadService;

    public AgendaController(
            AgendaService service,
            ActividadService actividadService) {

        this.service = service;
        this.actividadService = actividadService;
    }

    // ==============================
    // LISTAR
    // ==============================

    @GetMapping
    public List<Agenda> listarAgenda() {

        return service.obtenerAgenda();
    }


    // ==============================
    // AGREGAR
    // ==============================

    @PostMapping
    public Agenda crearAgenda(
            @RequestBody Agenda agenda,
            HttpSession session) {

        Agenda resultado =
                service.guardarAgenda(agenda);

        registrarActividad(
                session,
                "Registró una actividad",
                "Agenda"
        );

        return resultado;
    }


    // ==============================
    // MODIFICAR
    // ==============================

    @PutMapping("/{id}")
    public Agenda modificarAgenda(
            @PathVariable String id,
            @RequestBody Agenda agenda,
            HttpSession session) {

        agenda.setId(id);

        Agenda resultado =
                service.guardarAgenda(agenda);

        registrarActividad(
                session,
                "Modificó una actividad",
                "Agenda"
        );

        return resultado;
    }


    // ==============================
    // ELIMINAR
    // ==============================

    @DeleteMapping("/{id}")
    public void eliminarAgenda(
            @PathVariable String id,
            HttpSession session) {

        service.eliminarAgenda(id);

        registrarActividad(
                session,
                "Eliminó una actividad",
                "Agenda"
        );
    }


    // ==============================
    // REGISTRAR ACTIVIDAD
    // ==============================

    private void registrarActividad(
            HttpSession session,
            String accion,
            String modulo) {

        String usuario =
                (String) session.getAttribute("usuario");

        if (usuario != null) {

            actividadService.registrar(
                    usuario,
                    accion,
                    modulo
            );
        }
    }
}