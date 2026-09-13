package com.example.backend.controller;

import com.example.backend.entidades.*;
import com.example.backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/diagnostico")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://localhost:8080",
                "https://frontend-fdgz-phi-nu.vercel.app"
        },
        allowCredentials = "true"
)
public class DiagnosticoController {

    private final DocumentoRepository documentoRepository;
    private final AgendaRepository agendaRepository;
    private final VisitaRepository visitaRepository;
    private final NoticiaRepository noticiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ActividadRepository actividadRepository;

    public DiagnosticoController(
            DocumentoRepository documentoRepository,
            AgendaRepository agendaRepository,
            VisitaRepository visitaRepository,
            NoticiaRepository noticiaRepository,
            UsuarioRepository usuarioRepository,
            ActividadRepository actividadRepository) {

        this.documentoRepository = documentoRepository;
        this.agendaRepository = agendaRepository;
        this.visitaRepository = visitaRepository;
        this.noticiaRepository = noticiaRepository;
        this.usuarioRepository = usuarioRepository;
        this.actividadRepository = actividadRepository;
    }

    @GetMapping("/fechas")
    public ResponseEntity<Map<String, Object>> diagnosticarFechas() {

        Map<String, Object> resultado = new LinkedHashMap<>();

        // DOCUMENTOS
        List<Map<String, Object>> docs = documentoRepository.findAll().stream().map(d -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", d.getId());
            m.put("nombre", d.getNombre());
            m.put("fechaPublicacion", d.getFechaPublicacion() != null ? d.getFechaPublicacion().toString() : "NULL");
            return m;
        }).collect(Collectors.toList());
        resultado.put("documentos_total", docs.size());
        resultado.put("documentos", docs);

        // AGENDA
        List<Map<String, Object>> agendas = agendaRepository.findAll().stream().map(a -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.getId());
            m.put("actividad", a.getActividad());
            m.put("fecha", a.getFecha() != null ? a.getFecha().toString() : "NULL");
            return m;
        }).collect(Collectors.toList());
        resultado.put("agenda_total", agendas.size());
        resultado.put("agenda", agendas);

        // VISITAS
        List<Map<String, Object>> visitas = visitaRepository.findAll().stream().map(v -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", v.getId());
            m.put("nombreVisitante", v.getNombreVisitante());
            m.put("fechaRegistro", v.getFechaRegistro());
            return m;
        }).collect(Collectors.toList());
        resultado.put("visitas_total", visitas.size());
        resultado.put("visitas", visitas);

        // NOTICIAS
        List<Map<String, Object>> noticias = noticiaRepository.findAll().stream().map(n -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", n.getId());
            m.put("titulo", n.getTitulo());
            m.put("fechaPublicacion", n.getFechaPublicacion() != null ? n.getFechaPublicacion().toString() : "NULL");
            return m;
        }).collect(Collectors.toList());
        resultado.put("noticias_total", noticias.size());
        resultado.put("noticias", noticias);

        // USUARIOS
        List<Map<String, Object>> usuarios = usuarioRepository.findAll().stream().map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("usuario", u.getUsuario());
            m.put("fechaRegistro", u.getFechaRegistro() != null ? u.getFechaRegistro().toString() : "NULL");
            return m;
        }).collect(Collectors.toList());
        resultado.put("usuarios_total", usuarios.size());
        resultado.put("usuarios", usuarios);

        // ACTIVIDADES
        List<Map<String, Object>> actividades = actividadRepository.findAll().stream().map(a -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.getId());
            m.put("modulo", a.getModulo());
            m.put("fecha", a.getFecha() != null ? a.getFecha().toString() : "NULL");
            return m;
        }).collect(Collectors.toList());
        resultado.put("actividades_total", actividades.size());
        resultado.put("actividades_muestra", actividades.stream().limit(5).collect(Collectors.toList()));

        return ResponseEntity.ok(resultado);
    }
}
