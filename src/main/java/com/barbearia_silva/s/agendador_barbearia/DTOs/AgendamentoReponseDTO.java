package com.barbearia_silva.s.agendador_barbearia.DTOs;

import com.barbearia_silva.s.agendador_barbearia.models.entities.Agendamento;
import com.barbearia_silva.s.agendador_barbearia.models.enums.StatusAgendamento;
import com.barbearia_silva.s.agendador_barbearia.models.enums.TipoServico;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgendamentoReponseDTO {

    private LocalDateTime atendimentoInicio;
    private Set<TipoServico> servico;
    private String nomeBarbeiro;
    private StatusAgendamento status;

    public AgendamentoReponseDTO(Agendamento entity) {
        atendimentoInicio = entity.getAtendimentoInicio();
        servico = entity.getServico();
        nomeBarbeiro = entity.getBarbeiro().getNome();
        status = entity.getStatus();
    }
}
