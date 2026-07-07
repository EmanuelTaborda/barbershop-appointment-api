package com.barbershop_appointment_api.repositories;

import com.barbershop_appointment_api.models.entities.Block;
import com.barbershop_appointment_api.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {


    //consulta para verificar se não há conflitos com bloqueios na agenda
    @Query("SELECT a FROM Block a WHERE a.barber = :barber " +
            "AND a.startTime < :requestedEnd " +
            "AND a.endTime > :requestedStart")
    List<Block> findBlockConflicts(
            @Param("barber") User barber,
            @Param("requestedStart") LocalDateTime requestedStart,
            @Param("requestedEnd") LocalDateTime requestedEnd
    );
}
