package com.barbearia_silva.s.agendador_barbearia.repositories;

import com.barbearia_silva.s.agendador_barbearia.entities.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
}
