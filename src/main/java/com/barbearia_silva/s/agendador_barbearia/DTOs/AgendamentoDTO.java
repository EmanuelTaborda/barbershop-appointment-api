package com.barbearia_silva.s.agendador_barbearia.DTOs;

import com.barbearia_silva.s.agendador_barbearia.models.entities.Agendamento;
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
public class AgendamentoDTO {
    private Long id;
    private LocalDateTime atendimentoInicio;
    private LocalDateTime atendimentoFim;
    private Set<TipoServico> servico;
    private UserDTO cliente;
    private UserDTO barbeiro;

    public AgendamentoDTO(Agendamento entity) {
        id = entity.getId();
        atendimentoInicio = entity.getAtendimentoInicio();
        atendimentoFim = entity.getAtendimentoFim();
        servico = entity.getServico();
        cliente = new UserDTO(entity.getCliente());
        barbeiro = new UserDTO(entity.getBarbeiro());
    }
}
