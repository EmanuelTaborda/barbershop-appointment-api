package com.barbearia_silva.s.agendador_barbearia.model.enums;

public enum TipoServico {

    CABELO(30),
    BARBA(30),
    SOBRANCELHA(0);

    private final int TempoMinutos;

    TipoServico(int TempoMinutos) {
        this.TempoMinutos = TempoMinutos;
    }

    public int getDuracaoMinutos() {
        return TempoMinutos;
    }
}

