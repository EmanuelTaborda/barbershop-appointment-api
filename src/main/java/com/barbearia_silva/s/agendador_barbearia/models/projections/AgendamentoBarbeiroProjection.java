package com.barbearia_silva.s.agendador_barbearia.models.projections;

import com.barbearia_silva.s.agendador_barbearia.models.enums.TipoServico;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Set;

public interface AgendamentoBarbeiroProjection {

    Long getId();
    @Value("#{target.cliente.nome}")
    String getCliente();
    LocalDateTime getAtendimentoInicio();
    LocalDateTime getAtendimentoFim();
    Set<TipoServico> getServico();


}
