package com.barbearia_silva.s.agendador_barbearia.services;

import com.barbearia_silva.s.agendador_barbearia.DTOs.AgendamentoDTO;
import com.barbearia_silva.s.agendador_barbearia.entities.Agendamento;
import com.barbearia_silva.s.agendador_barbearia.entities.User;
import com.barbearia_silva.s.agendador_barbearia.repositories.AgendamentoRepository;
import com.barbearia_silva.s.agendador_barbearia.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public AgendamentoDTO AgendarHorario(AgendamentoDTO agendamentoDTO) {
        // Lógica para agendar um horário
        Agendamento entity = new Agendamento();
        copyDTOtoEntity(agendamentoDTO, entity);

        entity = agendamentoRepository.save(entity);

        // Verificar disponibilidade do barbeiro, criar o agendamento e salvar no banco de dados

        return new AgendamentoDTO(entity);
    }

    private void copyDTOtoEntity(AgendamentoDTO DTO, Agendamento entity) {
        entity.setServico(DTO.getServico());
        entity.setDataHora(DTO.getDataHora());

        User cliente = userRepository.findByEmail(DTO.getCliente());
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado: " + DTO.getCliente());
        }

        User atendente = userRepository.findByEmail(DTO.getAtendente());
        if (atendente == null) {
            throw new IllegalArgumentException("Atendente não encontrado: " + DTO.getAtendente());
        }

        entity.setCliente(cliente);
        entity.setAtendente(atendente);
    }
}
