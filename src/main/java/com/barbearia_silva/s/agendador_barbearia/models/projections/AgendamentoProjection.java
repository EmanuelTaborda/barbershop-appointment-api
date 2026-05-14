package com.barbearia_silva.s.agendador_barbearia.models.projections;

import com.barbearia_silva.s.agendador_barbearia.models.enums.StatusAgendamento;
import com.barbearia_silva.s.agendador_barbearia.models.enums.TipoServico;
import org.springframework.beans.factory.annotation.Value;


import java.time.LocalDateTime;
import java.util.Set;

public interface AgendamentoProjection {

    Long getId();

    LocalDateTime getAtendimentoInicio();

    Set<TipoServico> getServico();

    @Value("#{target.barbeiro.nome}")
    String getBarbeiroNome();

    StatusAgendamento getStatus();
}

