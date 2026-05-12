package com.barbearia_silva.s.agendador_barbearia.repositories;

import com.barbearia_silva.s.agendador_barbearia.models.entities.BloqueioAgenda;
import com.barbearia_silva.s.agendador_barbearia.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BloqueioAgendaRepository extends JpaRepository<BloqueioAgenda, Long> {


    //consulta para verificar se não há conflitos com bloqueios na agenda
    @Query("SELECT a FROM BloqueioAgenda a WHERE a.barbeiro = :barbeiro " +
            "AND a.inicioBloqueio < :fimSolicitado " +
            "AND a.fimBloqueio > :inicioSolicitado")
    List<BloqueioAgenda> findConflitosBloqueioAgenda(
            @Param("barbeiro") User barbeiro,
            @Param("inicioSolicitado") LocalDateTime inicioSolicitado,
            @Param("fimSolicitado") LocalDateTime fimSolicitado
    );
}
