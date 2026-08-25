package com.example.backend.dto;

import java.util.List;

public class EstadisticasDTO {

    private long usuariosRegistrados;
    private long usuariosActivos;
    private long visitasRegistradas;
    private long actividadesProgramadas;
    private long documentosPublicados;
    private long noticiasPublicadas;

    // Actividades de la plataforma
    private List<RegistroMensualDTO> actividadesMensuales;

    // Actividades de la agenda
    private List<RegistroMensualDTO> agendaMensual;

    // Otros registros mensuales
    private List<RegistroMensualDTO> registrosMensuales;
    private List<RegistroMensualDTO> documentosMensuales;

    private List<VisitaLugarDTO> visitasPorLugar;

    private ResumenTotalDTO resumenTotal;

    public EstadisticasDTO() {
    }

    // =========================
    // GETTERS Y SETTERS
    // =========================

    public long getUsuariosRegistrados() {
        return usuariosRegistrados;
    }

    public void setUsuariosRegistrados(long usuariosRegistrados) {
        this.usuariosRegistrados = usuariosRegistrados;
    }

    public long getUsuariosActivos() {
        return usuariosActivos;
    }

    public void setUsuariosActivos(long usuariosActivos) {
        this.usuariosActivos = usuariosActivos;
    }

    public long getVisitasRegistradas() {
        return visitasRegistradas;
    }

    public void setVisitasRegistradas(long visitasRegistradas) {
        this.visitasRegistradas = visitasRegistradas;
    }

    public long getActividadesProgramadas() {
        return actividadesProgramadas;
    }

    public void setActividadesProgramadas(long actividadesProgramadas) {
        this.actividadesProgramadas = actividadesProgramadas;
    }

    public long getDocumentosPublicados() {
        return documentosPublicados;
    }

    public void setDocumentosPublicados(long documentosPublicados) {
        this.documentosPublicados = documentosPublicados;
    }

    public long getNoticiasPublicadas() {
        return noticiasPublicadas;
    }

    public void setNoticiasPublicadas(long noticiasPublicadas) {
        this.noticiasPublicadas = noticiasPublicadas;
    }

    public List<RegistroMensualDTO> getActividadesMensuales() {
        return actividadesMensuales;
    }

    public void setActividadesMensuales(
            List<RegistroMensualDTO> actividadesMensuales) {
        this.actividadesMensuales = actividadesMensuales;
    }

    public List<RegistroMensualDTO> getAgendaMensual() {
        return agendaMensual;
    }

    public void setAgendaMensual(
            List<RegistroMensualDTO> agendaMensual) {
        this.agendaMensual = agendaMensual;
    }

    public List<RegistroMensualDTO> getRegistrosMensuales() {
        return registrosMensuales;
    }

    public void setRegistrosMensuales(
            List<RegistroMensualDTO> registrosMensuales) {
        this.registrosMensuales = registrosMensuales;
    }

    public List<RegistroMensualDTO> getDocumentosMensuales() {
        return documentosMensuales;
    }

    public void setDocumentosMensuales(
            List<RegistroMensualDTO> documentosMensuales) {
        this.documentosMensuales = documentosMensuales;
    }

    public List<VisitaLugarDTO> getVisitasPorLugar() {
        return visitasPorLugar;
    }

    public void setVisitasPorLugar(
            List<VisitaLugarDTO> visitasPorLugar) {
        this.visitasPorLugar = visitasPorLugar;
    }

    public ResumenTotalDTO getResumenTotal() {
        return resumenTotal;
    }

    public void setResumenTotal(
            ResumenTotalDTO resumenTotal) {
        this.resumenTotal = resumenTotal;
    }

    // =====================================================
    // REGISTRO MENSUAL
    // =====================================================

    public static class RegistroMensualDTO {

        private int mes;
        private String nombreMes;
        private long usuarios;
        private long visitas;
        private long documentos;
        private long noticias;
        private long actividades;

        public RegistroMensualDTO() {
        }

        public RegistroMensualDTO(
                int mes,
                String nombreMes,
                long usuarios,
                long visitas,
                long documentos,
                long noticias,
                long actividades) {

            this.mes = mes;
            this.nombreMes = nombreMes;
            this.usuarios = usuarios;
            this.visitas = visitas;
            this.documentos = documentos;
            this.noticias = noticias;
            this.actividades = actividades;
        }

        public int getMes() {
            return mes;
        }

        public void setMes(int mes) {
            this.mes = mes;
        }

        public String getNombreMes() {
            return nombreMes;
        }

        public void setNombreMes(String nombreMes) {
            this.nombreMes = nombreMes;
        }

        public long getUsuarios() {
            return usuarios;
        }

        public void setUsuarios(long usuarios) {
            this.usuarios = usuarios;
        }

        public long getVisitas() {
            return visitas;
        }

        public void setVisitas(long visitas) {
            this.visitas = visitas;
        }

        public long getDocumentos() {
            return documentos;
        }

        public void setDocumentos(long documentos) {
            this.documentos = documentos;
        }

        public long getNoticias() {
            return noticias;
        }

        public void setNoticias(long noticias) {
            this.noticias = noticias;
        }

        public long getActividades() {
            return actividades;
        }

        public void setActividades(long actividades) {
            this.actividades = actividades;
        }
    }

    // =====================================================
    // VISITAS POR LUGAR
    // =====================================================

    public static class VisitaLugarDTO {

        private String lugar;
        private long cantidad;

        public VisitaLugarDTO() {
        }

        public VisitaLugarDTO(
                String lugar,
                long cantidad) {

            this.lugar = lugar;
            this.cantidad = cantidad;
        }

        public String getLugar() {
            return lugar;
        }

        public void setLugar(String lugar) {
            this.lugar = lugar;
        }

        public long getCantidad() {
            return cantidad;
        }

        public void setCantidad(long cantidad) {
            this.cantidad = cantidad;
        }
    }

    // =====================================================
    // RESUMEN TOTAL
    // =====================================================

    public static class ResumenTotalDTO {

        private long totalUsuarios;
        private long totalVisitas;
        private long totalDocumentos;
        private long totalNoticias;

        // Total de actividades de la plataforma
        private long totalActividades;

        // Total de actividades de la agenda
        private long totalAgenda;

        public ResumenTotalDTO() {
        }

        public ResumenTotalDTO(
                long totalUsuarios,
                long totalVisitas,
                long totalDocumentos,
                long totalNoticias,
                long totalActividades,
                long totalAgenda) {

            this.totalUsuarios = totalUsuarios;
            this.totalVisitas = totalVisitas;
            this.totalDocumentos = totalDocumentos;
            this.totalNoticias = totalNoticias;
            this.totalActividades = totalActividades;
            this.totalAgenda = totalAgenda;
        }

        public long getTotalUsuarios() {
            return totalUsuarios;
        }

        public void setTotalUsuarios(long totalUsuarios) {
            this.totalUsuarios = totalUsuarios;
        }

        public long getTotalVisitas() {
            return totalVisitas;
        }

        public void setTotalVisitas(long totalVisitas) {
            this.totalVisitas = totalVisitas;
        }

        public long getTotalDocumentos() {
            return totalDocumentos;
        }

        public void setTotalDocumentos(long totalDocumentos) {
            this.totalDocumentos = totalDocumentos;
        }

        public long getTotalNoticias() {
            return totalNoticias;
        }

        public void setTotalNoticias(long totalNoticias) {
            this.totalNoticias = totalNoticias;
        }

        public long getTotalActividades() {
            return totalActividades;
        }

        public void setTotalActividades(long totalActividades) {
            this.totalActividades = totalActividades;
        }

        public long getTotalAgenda() {
            return totalAgenda;
        }

        public void setTotalAgenda(long totalAgenda) {
            this.totalAgenda = totalAgenda;
        }
    }
}