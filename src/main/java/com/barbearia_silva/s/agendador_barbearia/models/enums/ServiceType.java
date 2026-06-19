package com.barbearia_silva.s.agendador_barbearia.models.enums;

public enum ServiceType {

    CABELO(30),
    BARBA(30),
    SOBRANCELHA(0);

    private final int TempoMinutos;

    ServiceType(int TempoMinutos) {
        this.TempoMinutos = TempoMinutos;
    }

    public int getDuracaoMinutos() {
        return TempoMinutos;
    }
}

