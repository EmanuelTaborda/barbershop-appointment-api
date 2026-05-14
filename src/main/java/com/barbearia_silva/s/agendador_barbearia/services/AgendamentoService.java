package com.barbearia_silva.s.agendador_barbearia.services;

import com.barbearia_silva.s.agendador_barbearia.DTOs.AgendamentoDTO;
import com.barbearia_silva.s.agendador_barbearia.DTOs.AgendamentoMinDTO;
import com.barbearia_silva.s.agendador_barbearia.DTOs.AgendamentoReponseDTO;
import com.barbearia_silva.s.agendador_barbearia.exceptions.ConflitoDeAgendamentoException;
import com.barbearia_silva.s.agendador_barbearia.models.entities.Agendamento;
import com.barbearia_silva.s.agendador_barbearia.models.entities.BloqueioAgenda;
import com.barbearia_silva.s.agendador_barbearia.models.entities.User;
import com.barbearia_silva.s.agendador_barbearia.models.enums.StatusAgendamento;
import com.barbearia_silva.s.agendador_barbearia.models.enums.TipoServico;
import com.barbearia_silva.s.agendador_barbearia.models.enums.TipoUsuario;
import com.barbearia_silva.s.agendador_barbearia.models.projections.AgendamentoProjection;
import com.barbearia_silva.s.agendador_barbearia.repositories.AgendamentoRepository;
import com.barbearia_silva.s.agendador_barbearia.repositories.BloqueioAgendaRepository;
import com.barbearia_silva.s.agendador_barbearia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.barbearia_silva.s.agendador_barbearia.config.RegrasBarbearia;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BloqueioAgendaRepository bloqueioAgendaRepository;

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

        validarBarbeiro(entity);
        validarDataAgendamento(entity);
        validarHorarioDeFuncionamento(entity);
        validarConflitosComBloqueios(entity);
        validarConflitoDeAgendamentos(entity);

        entity = agendamentoRepository.save(entity);

        return new AgendamentoReponseDTO(entity);
    }

    @Transactional
    public AgendamentoReponseDTO atualizarAgendamento(Long id, AgendamentoMinDTO agendamentoMinDTO){
        try {
            Agendamento entity = agendamentoRepository.getReferenceById(id);
            if (!entity.getCliente().getEmail().equals(agendamentoMinDTO.getClienteEmail())) {
                throw new ConflitoDeAgendamentoException("Atuzalização de um agendamento deve ser feita pelo cliente " +
                        "que o agendou primeiramente.");
            }
            copyDTOtoEntity(agendamentoMinDTO, entity);

            validarBarbeiro(entity);
            validarDataAgendamento(entity);
            validarHorarioDeFuncionamento(entity);
            validarConflitosComBloqueios(entity);
            validarConflitoDeAgendamentos(entity);

            entity = agendamentoRepository.save(entity);

            return new AgendamentoReponseDTO(entity);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao atualizar o agendamento: " + e.getMessage());
        }
    }

    private LocalDateTime calcularFimAtendimento(LocalDateTime inicio, Set<TipoServico> servico) {

        int tempototal = servico.stream().mapToInt(TipoServico::getDuracaoMinutos).sum();

        return inicio.plusMinutes(tempototal);
    }

    //Verificar User Barbeiro
    private void validarBarbeiro(Agendamento agendamento) {
        if (!agendamento.getBarbeiro().getRoles().stream().anyMatch(role -> role.getAuthority().equals(TipoUsuario.ROLE_BARBEIRO))) {
            throw new IllegalArgumentException("O usuário selecionado como barbeiro é inválido.");
        }
    }

    //Verificação se o atendimento solicitado não é no passado e limitando o agendamento para no máximo dois meses de antecedência
    private void validarDataAgendamento(Agendamento agendamento) {
        LocalDateTime agora = LocalDateTime.now();

        if (agendamento.getAtendimentoInicio().isAfter(agora.plusMonths(2))) {
            throw new ConflitoDeAgendamentoException("Agendamentos limitados a 2 meses de antecedência.");
        }
    }

    //Função para validar se não há conflitos do horário solicitado no DTO com o horário de funcionamento da barbearia
    private void validarHorarioDeFuncionamento(Agendamento agendamento) {
        LocalTime inicio = agendamento.getAtendimentoInicio().toLocalTime();
        LocalTime fim = calcularFimAtendimento(agendamento.getAtendimentoInicio(), agendamento.getServico())
                .toLocalTime();

        //Verificar conflito com dia de domingo
        if (agendamento.getAtendimentoInicio().getDayOfWeek()== DayOfWeek.SUNDAY) {
            throw new ConflitoDeAgendamentoException("A Barbearia não possui expediente aos domingos.");
        }

        //Verificar conflito com horário de almoço
        Boolean conflitoAlmoco = inicio.isBefore(RegrasBarbearia.FINAL_ALMOCO)
                && fim.isAfter(RegrasBarbearia.INICIO_ALMOCO);
        if (conflitoAlmoco) {
            throw new ConflitoDeAgendamentoException("No horário solicitado a Barbearia estará fechada para almoço.");
        }

        //Verificar conflito com horário de funcionamento
        boolean conflitoFuncionamento = inicio.isBefore(RegrasBarbearia.ABERTURA)
                || inicio.isAfter(RegrasBarbearia.FECHAMENTO);
        if (conflitoFuncionamento) {
            throw new ConflitoDeAgendamentoException("No horário solicitado a Barbearia estará fechada.");
        }

        //Verifica se o serviço solicitado não temrina após fechamento da barbearia
        if (fim.isAfter(RegrasBarbearia.FECHAMENTO)) {
            throw new ConflitoDeAgendamentoException(
                    "Devido ao tempo para realizar seu atendimento, ele precisa iniciar antes, selecione outro horário."
            );
        }
    }

    //Verificar conflito com bloqueios
    private void validarConflitosComBloqueios(Agendamento agendamento) {
        List<BloqueioAgenda> conflitosBloqueioAgenda = bloqueioAgendaRepository.findConflitosBloqueioAgenda(
                agendamento.getBarbeiro(), agendamento.getAtendimentoInicio(),
                agendamento.getAtendimentoFim());
        if (!conflitosBloqueioAgenda.isEmpty()) {
            throw new ConflitoDeAgendamentoException("O horário solicitado não está disponivél para agendamento.");
        }
    }

    // Verificar disponibilidade do barbeiro
    private void validarConflitoDeAgendamentos(Agendamento agendamento) {
        List<Agendamento> conflitosAgendamento = agendamentoRepository.findConflitosAgendamento(
                agendamento.getBarbeiro(),
                agendamento.getId(),
                agendamento.getAtendimentoInicio(),
                agendamento.getAtendimentoFim()
        );

        if (!conflitosAgendamento.isEmpty()) {
            throw new ConflitoDeAgendamentoException("O barbeiro já possui um agendamento neste horário.");
        }
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
