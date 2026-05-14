package com.barbearia_silva.s.agendador_barbearia.repositories;

import com.barbearia_silva.s.agendador_barbearia.models.entities.Agendamento;
import com.barbearia_silva.s.agendador_barbearia.models.entities.User;
import com.barbearia_silva.s.agendador_barbearia.models.projections.AgendamentoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    //Consulta para verificar se não existem conflitos na agenda do atendente
    @Query("SELECT a FROM Agendamento a WHERE a.barbeiro = :barbeiro " +
            "AND a.atendimentoInicio < :fimSolicitado " +
            "AND a.atendimentoFim > :inicioSolicitado")
    List<Agendamento> findConflitosAgendamento(
            @Param("barbeiro") User barbeiro,
            @Param("inicioSolicitado") LocalDateTime inicioSolicitado,
            @Param("fimSolicitado") LocalDateTime fimSolicitado
    );

    List<AgendamentoProjection> findByCliente(User cliente);
}
