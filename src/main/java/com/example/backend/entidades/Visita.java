package com.example.backend.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "visitas")
public class Visita {

    @Id
    private String id;

    private String fechaRegistro;
    private String nombreVisitante;
    private String dniVisitante;
    private String funcionarioVisitado;
    private String horaEntradaRegistrada;
    private String horaSalidaRegistrada;
    private String motivo;
    private String lugarEspecificoVisita;

    public Visita() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getNombreVisitante() {
        return nombreVisitante;
    }

    public void setNombreVisitante(String nombreVisitante) {
        this.nombreVisitante = nombreVisitante;
    }

    public String getDniVisitante() {
        return dniVisitante;
    }

    public void setDniVisitante(String dniVisitante) {
        this.dniVisitante = dniVisitante;
    }

    public String getFuncionarioVisitado() {
        return funcionarioVisitado;
    }

    public void setFuncionarioVisitado(String funcionarioVisitado) {
        this.funcionarioVisitado = funcionarioVisitado;
    }

    public String getHoraEntradaRegistrada() {
        return horaEntradaRegistrada;
    }

    public void setHoraEntradaRegistrada(String horaEntradaRegistrada) {
        this.horaEntradaRegistrada = horaEntradaRegistrada;
    }

    public String getHoraSalidaRegistrada() {
        return horaSalidaRegistrada;
    }

    public void setHoraSalidaRegistrada(String horaSalidaRegistrada) {
        this.horaSalidaRegistrada = horaSalidaRegistrada;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getLugarEspecificoVisita() {
        return lugarEspecificoVisita;
    }

    public void setLugarEspecificoVisita(String lugarEspecificoVisita) {
        this.lugarEspecificoVisita = lugarEspecificoVisita;
    }

}