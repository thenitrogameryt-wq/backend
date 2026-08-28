package com.example.backend.service;

import com.example.backend.dto.EstadisticasDTO;
import com.example.backend.dto.EstadisticasDTO.RegistroMensualDTO;
import com.example.backend.dto.EstadisticasDTO.ResumenTotalDTO;
import com.example.backend.dto.EstadisticasDTO.VisitaLugarDTO;
import com.example.backend.entidades.*;
import com.example.backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EstadisticaService {

    private final ActividadRepository actividadRepository;
    private final AgendaRepository agendaRepository;
    private final DocumentoRepository documentoRepository;
    private final NoticiaRepository noticiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VisitaRepository visitaRepository;

    public EstadisticaService(
            ActividadRepository actividadRepository,
            AgendaRepository agendaRepository,
            DocumentoRepository documentoRepository,
            NoticiaRepository noticiaRepository,
            UsuarioRepository usuarioRepository,
            VisitaRepository visitaRepository) {

        this.actividadRepository = actividadRepository;
        this.agendaRepository = agendaRepository;
        this.documentoRepository = documentoRepository;
        this.noticiaRepository = noticiaRepository;
        this.usuarioRepository = usuarioRepository;
        this.visitaRepository = visitaRepository;
    }

    public EstadisticasDTO obtenerEstadisticas(int anio, String periodo) {

        EstadisticasDTO resultado = new EstadisticasDTO();

        // 1. OBTENER LISTAS COMPLETAS DESDE MONGODB
        List<Actividad> actividades = actividadRepository.findAll();
        List<Agenda> agendas = agendaRepository.findAll();
        List<Documento> documentos = documentoRepository.findAll();
        List<Noticia> noticias = noticiaRepository.findAll();
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Visita> visitas = visitaRepository.findAll();

        // 2. FILTRAR POR PERIODO SELECCIONADO (TARJETAS SUPERIORES)
        List<Usuario> usuariosPeriodo = usuarios.stream()
                .filter(u -> u.getFechaRegistro() != null && perteneceAlPeriodo(u.getFechaRegistro(), anio, periodo))
                .toList();

        List<Visita> visitasPeriodo = visitas.stream()
                .filter(v -> obtenerFechaVisita(v) != null && perteneceAlPeriodo(obtenerFechaVisita(v), anio, periodo))
                .toList();

        // Eventos en Agenda = Actividades programadas
        List<Agenda> agendasPeriodo = agendas.stream()
                .filter(a -> a.getFecha() != null && perteneceAlPeriodo(a.getFecha(), anio, periodo))
                .toList();

        List<Documento> documentosPeriodo = documentos.stream()
                .filter(d -> d.getFechaPublicacion() != null && perteneceAlPeriodo(d.getFechaPublicacion(), anio, periodo))
                .toList();

        List<Noticia> noticiasPeriodo = noticias.stream()
                .filter(n -> n.getFechaPublicacion() != null && perteneceAlPeriodo(n.getFechaPublicacion(), anio, periodo))
                .toList();

        // 3. SETEAR TARJETAS SUPERIORES
        resultado.setUsuariosRegistrados(usuariosPeriodo.size());
        resultado.setUsuariosActivos(usuarios.stream().filter(Usuario::isActivo).count());
        resultado.setVisitasRegistradas(visitasPeriodo.size());
        resultado.setActividadesProgramadas(agendasPeriodo.size()); // Asigna conteo de Agendas
        resultado.setDocumentosPublicados(documentosPeriodo.size());
        resultado.setNoticiasPublicadas(noticiasPeriodo.size());

        // 4. GENERAR DATOS PARA GRÁFICOS
        resultado.setRegistrosMensuales(generarRegistrosMensuales(anio, usuarios, visitas, documentos, noticias));
        resultado.setDocumentosMensuales(generarDocumentosMensuales(anio, documentos));
        resultado.setAgendaMensual(generarAgendaMensual(anio, agendas));
        resultado.setActividadesMensuales(generarActividadesMensuales(anio, actividades));

        // 5. VISITAS POR LUGAR
        resultado.setVisitasPorLugar(generarVisitasPorLugar(visitasPeriodo));

        // 6. RESUMEN DEL SISTEMA (ACUMULADO HISTÓRICO)
        resultado.setResumenTotal(new ResumenTotalDTO(
                usuarioRepository.count(),
                visitaRepository.count(),
                documentoRepository.count(),
                noticiaRepository.count(),
                actividadRepository.count(),
                agendaRepository.count()
        ));

        return resultado;
    }

    private List<RegistroMensualDTO> generarRegistrosMensuales(
            int anio, List<Usuario> usuarios, List<Visita> visitas, List<Documento> documentos, List<Noticia> noticias) {

        List<RegistroMensualDTO> lista = new ArrayList<>();
        for (int mes = 1; mes <= 12; mes++) {
            final int m = mes;

            long cantUsuarios = usuarios.stream()
                    .filter(u -> u.getFechaRegistro() != null && u.getFechaRegistro().getYear() == anio && u.getFechaRegistro().getMonthValue() == m)
                    .count();

            long cantVisitas = visitas.stream()
                    .map(this::obtenerFechaVisita)
                    .filter(Objects::nonNull)
                    .filter(f -> f.getYear() == anio && f.getMonthValue() == m)
                    .count();

            long cantDocs = documentos.stream()
                    .filter(d -> d.getFechaPublicacion() != null)
                    .filter(d -> d.getFechaPublicacion().getYear() == anio && d.getFechaPublicacion().getMonthValue() == m)
                    .count();

            long cantNoticias = noticias.stream()
                    .filter(n -> n.getFechaPublicacion() != null)
                    .filter(n -> n.getFechaPublicacion().getYear() == anio && n.getFechaPublicacion().getMonthValue() == m)
                    .count();

            lista.add(new RegistroMensualDTO(mes, nombreMes(mes), cantUsuarios, cantVisitas, cantDocs, cantNoticias, 0));
        }
        return lista;
    }

    private List<RegistroMensualDTO> generarDocumentosMensuales(int anio, List<Documento> documentos) {
        List<RegistroMensualDTO> lista = new ArrayList<>();
        for (int mes = 1; mes <= 12; mes++) {
            final int m = mes;
            long cantDocs = documentos.stream()
                    .filter(d -> d.getFechaPublicacion() != null)
                    .filter(d -> d.getFechaPublicacion().getYear() == anio && d.getFechaPublicacion().getMonthValue() == m)
                    .count();

            lista.add(new RegistroMensualDTO(mes, nombreMes(mes), 0, 0, cantDocs, 0, 0));
        }
        return lista;
    }

    private List<RegistroMensualDTO> generarAgendaMensual(int anio, List<Agenda> agendas) {
        List<RegistroMensualDTO> lista = new ArrayList<>();
        for (int mes = 1; mes <= 12; mes++) {
            final int m = mes;
            long cantAgenda = agendas.stream()
                    .filter(a -> a.getFecha() != null && a.getFecha().getYear() == anio && a.getFecha().getMonthValue() == m)
                    .count();

            lista.add(new RegistroMensualDTO(mes, nombreMes(mes), 0, 0, 0, 0, cantAgenda));
        }
        return lista;
    }

    private List<RegistroMensualDTO> generarActividadesMensuales(int anio, List<Actividad> actividades) {
        List<RegistroMensualDTO> lista = new ArrayList<>();
        for (int mes = 1; mes <= 12; mes++) {
            final int m = mes;
            long cantActividades = actividades.stream()
                    .filter(a -> a.getFecha() != null && a.getFecha().getYear() == anio && a.getFecha().getMonthValue() == m)
                    .count();

            lista.add(new RegistroMensualDTO(mes, nombreMes(mes), 0, 0, 0, 0, cantActividades));
        }
        return lista;
    }

    private List<VisitaLugarDTO> generarVisitasPorLugar(List<Visita> visitas) {
        Map<String, Long> mapa = visitas.stream()
                .collect(Collectors.groupingBy(
                        v -> (v.getLugarEspecificoVisita() == null || v.getLugarEspecificoVisita().isBlank())
                                ? "Sin especificar" : v.getLugarEspecificoVisita(),
                        Collectors.counting()));

        return mapa.entrySet().stream()
                .map(e -> new VisitaLugarDTO(e.getKey(), e.getValue()))
                .sorted((a, b) -> Long.compare(b.getCantidad(), a.getCantidad()))
                .toList();
    }

    private boolean perteneceAlPeriodo(LocalDate fecha, int anio, String periodo) {
        if (fecha == null || fecha.getYear() != anio) return false;
        if (periodo == null || periodo.isBlank() || periodo.equalsIgnoreCase("todo")) return true;
        try {
            return fecha.getMonthValue() == Integer.parseInt(periodo);
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean perteneceAlPeriodo(LocalDateTime fecha, int anio, String periodo) {
        return fecha != null && perteneceAlPeriodo(fecha.toLocalDate(), anio, periodo);
    }

    private LocalDate obtenerFechaVisita(Visita visita) {
        if (visita == null || visita.getFechaRegistro() == null) return null;
        try {
            return LocalDate.parse(visita.getFechaRegistro(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(visita.getFechaRegistro(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException ex) {
                return null;
            }
        }
    }

    private String nombreMes(int mes) {
        return switch (Month.of(mes)) {
            case JANUARY -> "Ene"; case FEBRUARY -> "Feb"; case MARCH -> "Mar";
            case APRIL -> "Abr"; case MAY -> "May"; case JUNE -> "Jun";
            case JULY -> "Jul"; case AUGUST -> "Ago"; case SEPTEMBER -> "Sep";
            case OCTOBER -> "Oct"; case NOVEMBER -> "Nov"; case DECEMBER -> "Dic";
        };
    }
}