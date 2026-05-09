package com.barbearia_silva.s.agendador_barbearia.DTOs;

import com.barbearia_silva.s.agendador_barbearia.entities.Agendamento;
import com.barbearia_silva.s.agendador_barbearia.entities.TipoUsuario;
import com.barbearia_silva.s.agendador_barbearia.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgendamentoDTO {
    private Long id;
    private LocalDateTime dataHora;
    private String servico;
    private String cliente;
    private String atendente;

    public AgendamentoDTO(Agendamento entity) {
        id = entity.getId();
        dataHora = entity.getDataHora();
        servico = entity.getServico();
        cliente = entity.getCliente().getEmail();
        atendente = entity.getAtendente().getEmail();
    }
}
