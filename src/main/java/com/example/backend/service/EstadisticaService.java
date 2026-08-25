package com.example.backend.service;

import com.example.backend.dto.EstadisticasDTO;
import com.example.backend.entidades.Actividad;
import com.example.backend.entidades.Agenda;
import com.example.backend.entidades.Documento;
import com.example.backend.entidades.Noticia;
import com.example.backend.entidades.Usuario;
import com.example.backend.entidades.Visita;
import com.example.backend.repository.ActividadRepository;
import com.example.backend.repository.AgendaRepository;
import com.example.backend.repository.DocumentoRepository;
import com.example.backend.repository.NoticiaRepository;
import com.example.backend.repository.UsuarioRepository;
import com.example.backend.repository.VisitaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        // =========================================================
        // OBTENER DATOS
        // =========================================================

        List<Actividad> actividades = actividadRepository.findAll();

        List<Agenda> agendas = agendaRepository.findAll();

        System.out.println("=================================");
        System.out.println("TOTAL AGENDAS EN MONGODB: " + agendas.size());

        for (Agenda agenda : agendas) {

            System.out.println(
                    "Agenda -> id: " + agenda.getId()
                            + " | actividad: " + agenda.getActividad()
                            + " | fecha: " + agenda.getFecha()
                            + " | hora: " + agenda.getHora()
                            + " | lugar: " + agenda.getLugar()
            );
        }

        System.out.println("=================================");

        List<Documento> documentos = documentoRepository.findAll();

        List<Noticia> noticias = noticiaRepository.findAll();

        List<Usuario> usuarios = usuarioRepository.findAll();

        List<Visita> visitas = visitaRepository.findAll();

        // =========================================================
        // FILTRAR POR AÑO Y PERIODO
        // =========================================================

        List<Actividad> actividadesPeriodo = actividades.stream()
                .filter(a -> a.getFecha() != null)
                .filter(a -> perteneceAlPeriodo(
                        a.getFecha(),
                        anio,
                        periodo))
                .toList();

        List<Agenda> agendasPeriodo = agendas.stream()
                .filter(a -> a.getFecha() != null)
                .filter(a -> perteneceAlPeriodo(
                        a.getFecha(),
                        anio,
                        periodo))
                .toList();

        List<Documento> documentosPeriodo = documentos.stream()
                .filter(d -> d.getFechaPublicacion() != null)
                .filter(d -> perteneceAlPeriodo(
                        d.getFechaPublicacion(),
                        anio,
                        periodo))
                .toList();

        List<Noticia> noticiasPeriodo = noticias.stream()
                .filter(n -> n.getFechaPublicacion() != null)
                .filter(n -> perteneceAlPeriodo(
                        n.getFechaPublicacion(),
                        anio,
                        periodo))
                .toList();

        List<Usuario> usuariosPeriodo = usuarios.stream()
                .filter(u -> u.getFechaRegistro() != null)
                .filter(u -> perteneceAlPeriodo(
                        u.getFechaRegistro(),
                        anio,
                        periodo))
                .toList();

        List<Visita> visitasPeriodo = visitas.stream()
                .filter(v -> obtenerFechaVisita(v) != null)
                .filter(v -> perteneceAlPeriodo(
                        obtenerFechaVisita(v),
                        anio,
                        periodo))
                .toList();

        // =========================================================
        // TARJETAS DEL DASHBOARD
        // =========================================================

        resultado.setUsuariosRegistrados(
                usuariosPeriodo.size());

        resultado.setUsuariosActivos(
                usuarios.stream()
                        .filter(Usuario::isActivo)
                        .count());

        resultado.setVisitasRegistradas(
                visitasPeriodo.size());

        // IMPORTANTE:
        // Esta estadística es SOLO de Actividad.
        resultado.setActividadesProgramadas(
                actividadesPeriodo.size());

        resultado.setDocumentosPublicados(
                documentosPeriodo.size());

        resultado.setNoticiasPublicadas(
                noticiasPeriodo.size());

        // =========================================================
        // ACTIVIDADES DE LA PLATAFORMA POR MES
        // =========================================================

        List<EstadisticasDTO.RegistroMensualDTO> actividadesMensuales =
                new ArrayList<>();

        for (int mes = 1; mes <= 12; mes++) {

            final int mesActual = mes;

            long cantidadActividades = actividades.stream()
                    .filter(a -> a.getFecha() != null)
                    .filter(a -> a.getFecha().getYear() == anio)
                    .filter(a -> a.getFecha().getMonthValue() == mesActual)
                    .count();

            actividadesMensuales.add(
                    new EstadisticasDTO.RegistroMensualDTO(
                            mes,
                            nombreMes(mes),
                            0,
                            0,
                            0,
                            0,
                            cantidadActividades
                    )
            );
        }

        resultado.setActividadesMensuales(
                actividadesMensuales);

        // =========================================================
        // ACTIVIDADES DE LA AGENDA POR MES
        // =========================================================

        List<EstadisticasDTO.RegistroMensualDTO> agendaMensual =
                new ArrayList<>();

        for (int mes = 1; mes <= 12; mes++) {

            final int mesActual = mes;

            long cantidadAgenda = agendas.stream()
                    .filter(a -> a.getFecha() != null)
                    .filter(a -> a.getFecha().getYear() == anio)
                    .filter(a -> a.getFecha().getMonthValue() == mesActual)
                    .count();

            agendaMensual.add(
                    new EstadisticasDTO.RegistroMensualDTO(
                            mes,
                            nombreMes(mes),
                            0,
                            0,
                            0,
                            0,
                            cantidadAgenda
                    )
            );
        }

        resultado.setAgendaMensual(
                agendaMensual);

        // =========================================================
        // REGISTROS MENSUALES
        // USUARIOS / VISITAS / DOCUMENTOS / NOTICIAS
        // =========================================================

        List<EstadisticasDTO.RegistroMensualDTO> registrosMensuales =
                new ArrayList<>();

        for (int mes = 1; mes <= 12; mes++) {

            final int mesActual = mes;

            long cantidadUsuarios = usuarios.stream()
                    .filter(u -> u.getFechaRegistro() != null)
                    .filter(u ->
                            u.getFechaRegistro().getYear() == anio &&
                                    u.getFechaRegistro().getMonthValue() == mesActual)
                    .count();

            long cantidadVisitas = visitas.stream()
                    .map(this::obtenerFechaVisita)
                    .filter(Objects::nonNull)
                    .filter(fecha ->
                            fecha.getYear() == anio &&
                                    fecha.getMonthValue() == mesActual)
                    .count();

            long cantidadDocumentos = documentos.stream()
                    .filter(d -> d.getFechaPublicacion() != null)
                    .filter(d ->
                            d.getFechaPublicacion().getYear() == anio &&
                                    d.getFechaPublicacion().getMonthValue() == mesActual)
                    .count();

            long cantidadNoticias = noticias.stream()
                    .filter(n -> n.getFechaPublicacion() != null)
                    .filter(n ->
                            n.getFechaPublicacion().getYear() == anio &&
                                    n.getFechaPublicacion().getMonthValue() == mesActual)
                    .count();

            registrosMensuales.add(
                    new EstadisticasDTO.RegistroMensualDTO(
                            mes,
                            nombreMes(mes),
                            cantidadUsuarios,
                            cantidadVisitas,
                            cantidadDocumentos,
                            cantidadNoticias,
                            0
                    )
            );
        }

        resultado.setRegistrosMensuales(
                registrosMensuales);

        // =========================================================
        // DOCUMENTOS MENSUALES
        // =========================================================

        List<EstadisticasDTO.RegistroMensualDTO> documentosMensuales =
                new ArrayList<>();

        for (int mes = 1; mes <= 12; mes++) {

            final int mesActual = mes;

            long cantidadDocumentos = documentos.stream()
                    .filter(d -> d.getFechaPublicacion() != null)
                    .filter(d ->
                            d.getFechaPublicacion().getYear() == anio &&
                                    d.getFechaPublicacion().getMonthValue() == mesActual)
                    .count();

            documentosMensuales.add(
                    new EstadisticasDTO.RegistroMensualDTO(
                            mes,
                            nombreMes(mes),
                            0,
                            0,
                            cantidadDocumentos,
                            0,
                            0
                    )
            );
        }

        resultado.setDocumentosMensuales(
                documentosMensuales);

        // =========================================================
        // VISITAS POR LUGAR
        // =========================================================

        Map<String, Long> visitasPorLugarMap =
                visitasPeriodo.stream()
                        .collect(Collectors.groupingBy(
                                v -> {
                                    String lugar =
                                            v.getLugarEspecificoVisita();

                                    return lugar == null ||
                                            lugar.isBlank()
                                            ? "Sin especificar"
                                            : lugar;
                                },
                                Collectors.counting()
                        ));

        List<EstadisticasDTO.VisitaLugarDTO> visitasPorLugar =
                visitasPorLugarMap.entrySet()
                        .stream()
                        .map(entry ->
                                new EstadisticasDTO.VisitaLugarDTO(
                                        entry.getKey(),
                                        entry.getValue()
                                )
                        )
                        .sorted(
                                (a, b) ->
                                        Long.compare(
                                                b.getCantidad(),
                                                a.getCantidad()
                                        )
                        )
                        .toList();

        resultado.setVisitasPorLugar(
                visitasPorLugar);

        // =========================================================
        // RESUMEN TOTAL
        // =========================================================

        long totalActividades = actividades.stream()
                .filter(a -> a.getFecha() != null)
                .filter(a -> a.getFecha().getYear() == anio)
                .count();

        long totalAgenda = agendas.stream()
                .filter(a -> a.getFecha() != null)
                .filter(a -> a.getFecha().getYear() == anio)
                .count();

        EstadisticasDTO.ResumenTotalDTO resumenTotal =
                new EstadisticasDTO.ResumenTotalDTO(
                        usuarios.stream()
                                .filter(u -> u.getFechaRegistro() != null)
                                .filter(u ->
                                        u.getFechaRegistro().getYear() == anio)
                                .count(),

                        visitas.stream()
                                .map(this::obtenerFechaVisita)
                                .filter(Objects::nonNull)
                                .filter(fecha ->
                                        fecha.getYear() == anio)
                                .count(),

                        documentos.stream()
                                .filter(d -> d.getFechaPublicacion() != null)
                                .filter(d ->
                                        d.getFechaPublicacion().getYear() == anio)
                                .count(),

                        noticias.stream()
                                .filter(n -> n.getFechaPublicacion() != null)
                                .filter(n ->
                                        n.getFechaPublicacion().getYear() == anio)
                                .count(),

                        // SOLO Actividad
                        totalActividades,

                        // SOLO Agenda
                        totalAgenda
                );

        resultado.setResumenTotal(resumenTotal);

        return resultado;
    }

    // =========================================================
    // FILTRO PARA LocalDate
    // =========================================================

    private boolean perteneceAlPeriodo(
            LocalDate fecha,
            int anio,
            String periodo) {

        if (fecha == null) {
            return false;
        }

        if (fecha.getYear() != anio) {
            return false;
        }

        if (periodo == null ||
                periodo.isBlank() ||
                periodo.equalsIgnoreCase("todo")) {

            return true;
        }

        try {
            int mes = Integer.parseInt(periodo);

            return fecha.getMonthValue() == mes;

        } catch (NumberFormatException e) {
            return true;
        }
    }

    // =========================================================
    // FILTRO PARA LocalDateTime
    // =========================================================

    private boolean perteneceAlPeriodo(
            LocalDateTime fecha,
            int anio,
            String periodo) {

        if (fecha == null) {
            return false;
        }

        if (fecha.getYear() != anio) {
            return false;
        }

        if (periodo == null ||
                periodo.isBlank() ||
                periodo.equalsIgnoreCase("todo")) {

            return true;
        }

        try {
            int mes = Integer.parseInt(periodo);

            return fecha.getMonthValue() == mes;

        } catch (NumberFormatException e) {
            return true;
        }
    }

    // =========================================================
    // FECHA DE VISITA
    // =========================================================

    private LocalDate obtenerFechaVisita(Visita visita) {

        if (visita == null ||
                visita.getFechaRegistro() == null) {
            return null;
        }

        try {

            return LocalDate.parse(
                    visita.getFechaRegistro(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
            );

        } catch (DateTimeParseException e) {

            try {

                return LocalDate.parse(
                        visita.getFechaRegistro(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                );

            } catch (DateTimeParseException ex) {
                return null;
            }
        }
    }

    // =========================================================
    // NOMBRE DEL MES
    // =========================================================

    private String nombreMes(int mes) {

        return switch (Month.of(mes)) {

            case JANUARY -> "Enero";
            case FEBRUARY -> "Febrero";
            case MARCH -> "Marzo";
            case APRIL -> "Abril";
            case MAY -> "Mayo";
            case JUNE -> "Junio";
            case JULY -> "Julio";
            case AUGUST -> "Agosto";
            case SEPTEMBER -> "Septiembre";
            case OCTOBER -> "Octubre";
            case NOVEMBER -> "Noviembre";
            case DECEMBER -> "Diciembre";
        };
    }
}