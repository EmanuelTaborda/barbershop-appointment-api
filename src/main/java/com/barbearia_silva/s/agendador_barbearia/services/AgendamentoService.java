package com.barbearia_silva.s.agendador_barbearia.services;

import com.barbearia_silva.s.agendador_barbearia.DTOs.AgendamentoDTO;
import com.barbearia_silva.s.agendador_barbearia.DTOs.AgendamentoMinDTO;
import com.barbearia_silva.s.agendador_barbearia.exceptions.ConflitoDeAgendamentoException;
import com.barbearia_silva.s.agendador_barbearia.models.entities.Agendamento;
import com.barbearia_silva.s.agendador_barbearia.models.entities.User;
import com.barbearia_silva.s.agendador_barbearia.models.enums.TipoServico;
import com.barbearia_silva.s.agendador_barbearia.repositories.AgendamentoRepository;
import com.barbearia_silva.s.agendador_barbearia.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public AgendamentoDTO AgendarHorario(AgendamentoMinDTO agendamentoMinDTO) {
        // Lógica para agendar um horário
        Agendamento entity = new Agendamento();
        copyDTOtoEntity(agendamentoMinDTO, entity);

        // Verificar disponibilidade do barbeiro, criar o agendamento e salvar no banco de dados
        List<Agendamento> conflitos = agendamentoRepository.findConflitosAgendamento(
                entity.getAtendente(),
                entity.getAtendimentoInicio(),
                entity.getAtendimentoFim()
        );

        if (!conflitos.isEmpty()) {
            throw new ConflitoDeAgendamentoException("O atendente já possui um agendamento nesse horário.");
        } else {
            entity = agendamentoRepository.save(entity);
        }

        return new AgendamentoDTO(entity);
    }

    private LocalDateTime calcularFimAtendimento(LocalDateTime inicio, Set<TipoServico> servico) {

        int tempototal = servico.stream().mapToInt(TipoServico::getDuracaoMinutos).sum();


        return inicio.plusMinutes(tempototal);
    }

    private void copyDTOtoEntity(AgendamentoMinDTO DTO, Agendamento entity) {
        entity.setServico(DTO.getServico());
        entity.setAtendimentoInicio(DTO.getAtendimentoInicio());

        //Calculando horário do fim do atendimento para criar a entity
        LocalDateTime atendimentoFim = calcularFimAtendimento(DTO.getAtendimentoInicio(), DTO.getServico());
        entity.setAtendimentoFim(atendimentoFim);

        User cliente = userRepository.findByEmail(DTO.getClienteEmail());
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado: " + DTO.getClienteEmail());
        }

        User atendente = userRepository.findByEmail(DTO.getAtendenteEmail());
        if (atendente == null) {
            throw new IllegalArgumentException("Atendente não encontrado: " + DTO.getAtendenteEmail());
        }

        entity.setCliente(cliente);
        entity.setAtendente(atendente);
    }
}
