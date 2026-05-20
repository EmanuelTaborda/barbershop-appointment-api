package com.barbearia_silva.s.agendador_barbearia.repositories;

import com.barbearia_silva.s.agendador_barbearia.models.entities.Agendamento;
import com.barbearia_silva.s.agendador_barbearia.models.entities.User;
import com.barbearia_silva.s.agendador_barbearia.models.projections.AgendamentoBarbeiroProjection;
import com.barbearia_silva.s.agendador_barbearia.models.projections.AgendamentoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    //Consulta para verificar se não existem conflitos na agenda do barbeiro
    @Query("SELECT a FROM Agendamento a WHERE a.barbeiro = :barbeiro " +
            "AND (:agendamentoId IS NULL OR a.id <> :agendamentoId) " +
            "AND a.atendimentoInicio < :fimSolicitado " +
            "AND a.atendimentoFim > :inicioSolicitado")
    List<Agendamento> findConflitosAgendamento(
            @Param("barbeiro") User barbeiro,
            @Param("agendamentoId") Long agendamentoId,
            @Param("inicioSolicitado") LocalDateTime inicioSolicitado,
            @Param("fimSolicitado") LocalDateTime fimSolicitado
    );

    List<AgendamentoProjection> findByCliente(User cliente);

    @Query("SELECT a FROM Agendamento a WHERE a.barbeiro.id = :barbeiroId " +
            "AND CAST(a.atendimentoInicio AS date) = :data " +
            "ORDER BY a.atendimentoInicio ASC")
    List<AgendamentoBarbeiroProjection> findByBarbeiroAndData(@Param("barbeiroId") Long barbeiroId, @Param("data") LocalDate data);
}
