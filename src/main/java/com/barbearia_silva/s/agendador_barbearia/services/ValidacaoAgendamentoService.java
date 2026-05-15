package com.barbearia_silva.s.agendador_barbearia.services;

import com.barbearia_silva.s.agendador_barbearia.config.RegrasBarbearia;
import com.barbearia_silva.s.agendador_barbearia.exceptions.ConflitoDeAgendamentoException;
import com.barbearia_silva.s.agendador_barbearia.models.entities.Agendamento;
import com.barbearia_silva.s.agendador_barbearia.models.entities.BloqueioAgenda;
import com.barbearia_silva.s.agendador_barbearia.models.enums.TipoServico;
import com.barbearia_silva.s.agendador_barbearia.models.enums.TipoUsuario;
import com.barbearia_silva.s.agendador_barbearia.repositories.AgendamentoRepository;
import com.barbearia_silva.s.agendador_barbearia.repositories.BloqueioAgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
public class ValidacaoAgendamentoService {

    @Autowired
    AgendamentoRepository agendamentoRepository;

    @Autowired
    BloqueioAgendaRepository bloqueioAgendaRepository;

    public void validarAgendamento(Agendamento agendamento) {
        validarBarbeiro(agendamento);
        validarAntecedencia(agendamento);
        validarHorarioDeFuncionamento(agendamento);
        validarConflitosComBloqueios(agendamento);
        validarConflitoDeAgendamentos(agendamento);
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
    private void validarAntecedencia(Agendamento agendamento) {
        LocalDateTime agora = LocalDateTime.now();

        if (agendamento.getAtendimentoInicio().isAfter(agora.plusMonths(2))) {
            throw new ConflitoDeAgendamentoException("É permitido agendar horários com no máximo 2 meses de antecedência.");
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
}
