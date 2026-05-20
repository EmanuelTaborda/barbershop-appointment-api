package com.barbearia_silva.s.agendador_barbearia.services;

import com.barbearia_silva.s.agendador_barbearia.DTOs.AgendamentoMinDTO;
import com.barbearia_silva.s.agendador_barbearia.DTOs.AgendamentoReponseDTO;
import com.barbearia_silva.s.agendador_barbearia.exceptions.ConflitoDeAgendamentoException;
import com.barbearia_silva.s.agendador_barbearia.exceptions.DatabaseException;
import com.barbearia_silva.s.agendador_barbearia.exceptions.ResourceNotFoundException;
import com.barbearia_silva.s.agendador_barbearia.models.entities.Agendamento;
import com.barbearia_silva.s.agendador_barbearia.models.entities.User;
import com.barbearia_silva.s.agendador_barbearia.models.enums.StatusAgendamento;
import com.barbearia_silva.s.agendador_barbearia.models.enums.TipoServico;
import com.barbearia_silva.s.agendador_barbearia.models.projections.AgendamentoBarbeiroProjection;
import com.barbearia_silva.s.agendador_barbearia.models.projections.AgendamentoProjection;
import com.barbearia_silva.s.agendador_barbearia.repositories.AgendamentoRepository;
import com.barbearia_silva.s.agendador_barbearia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidacaoAgendamentoService validador;

    @Transactional(readOnly = true)
    public List<AgendamentoProjection> findApointmentsByCLientId(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
        //Recebe cada entity da consulta e transforma em DTO
        List<AgendamentoProjection> agendamentos = agendamentoRepository.findByCliente(user);

        return agendamentos;
    }

    @Transactional
    public AgendamentoReponseDTO agendarHorario(AgendamentoMinDTO agendamentoMinDTO) {
        Agendamento entity = new Agendamento();
        copyDTOtoEntity(agendamentoMinDTO, entity);

        validador.validarAgendamento(entity);
        entity = agendamentoRepository.save(entity);

        return new AgendamentoReponseDTO(entity);
    }

    @Transactional
    public AgendamentoReponseDTO atualizarAgendamento(Long id, AgendamentoMinDTO agendamentoMinDTO){
            Agendamento entity = agendamentoRepository.getReferenceById(id);
            if (!entity.getCliente().getEmail().equals(agendamentoMinDTO.getClienteEmail())
                    && !entity.getBarbeiro().getEmail().equals(agendamentoMinDTO.getBarbeiroEmail())) {
                throw new ConflitoDeAgendamentoException("Você não tem permissão para realizar este reagendamento.");
            }
            copyDTOtoEntity(agendamentoMinDTO, entity);

            validador.validarAgendamento(entity);

            entity = agendamentoRepository.save(entity);

            return new AgendamentoReponseDTO(entity);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void excluirAgendamento(Long id) {
        if (!agendamentoRepository.existsById(id)){
            throw new ResourceNotFoundException("Agendamento não encontrado: " + id);
        }

        try {
            agendamentoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    @Transactional(readOnly = true)
    public List<AgendamentoBarbeiroProjection> findByBarbeiroAndData(Long Id, LocalDate data) {
        User barbeiro = userRepository.findById(Id).orElseThrow(() -> new ResourceNotFoundException("Barbeiro não encontrado: " + Id));
        List<AgendamentoBarbeiroProjection> agendamentos = agendamentoRepository.findByBarbeiroAndData(barbeiro.getId(), data);
        return agendamentos;
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

        User barbeiro = userRepository.findByEmail(DTO.getBarbeiroEmail());
        if (barbeiro == null) {
            throw new IllegalArgumentException("Barbeiro não encontrado: " + DTO.getBarbeiroEmail());
        }

        entity.setCliente(cliente);
        entity.setBarbeiro(barbeiro);
        entity.setStatus(StatusAgendamento.AGENDADO);
    }
}
