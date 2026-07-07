package com.barbershop_appointment_api.repositories;

import com.barbershop_appointment_api.models.entities.Appointment;
import com.barbershop_appointment_api.models.entities.User;
import com.barbershop_appointment_api.models.projections.AppointmentBarberProjection;
import com.barbershop_appointment_api.models.projections.AppointmentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    //Consulta para verificar se não existem conflitos na agenda do barbeiro
    @Query("SELECT a FROM Appointment a WHERE a.barber = :barber " +
            "AND (:appointmentId IS NULL OR a.id <> :appointmentId) " +
            "AND a.startTime < :requestedEnd " +
            "AND a.endTime > :requestedStart")
    List<Appointment> findAppointmentConflicts(
            @Param("barber") User barbeiro,
            @Param("appointmentId") Long appointmentId,
            @Param("requestedStart") LocalDateTime requestedStart,
            @Param("requestedEnd") LocalDateTime requestedEnd
    );

    //Consulta para verificar conflitos de agenda para criar/atualizar bloqueios
    @Query("SELECT a FROM Appointment a WHERE a.barber = :barber " +
            "AND a.startTime < :requestedEnd " +
            "AND a.endTime > :requestedStart")
    List<Appointment> findAppointmentConflictsforBlocks(
            @Param("barber") User barbeiro,
            @Param("requestedStart") LocalDateTime requestedStart,
            @Param("requestedEnd") LocalDateTime requestedEnd
    );

    List<AppointmentProjection> findByClient(User cliente);

    @Query("SELECT a FROM Appointment a WHERE a.barber.id = :barberId " +
            "AND CAST(a.startTime AS date) = :date " +
            "ORDER BY a.startTime ASC")
    List<AppointmentBarberProjection> findByBarberAndDate(@Param("barberId") Long barberId,
                                                          @Param("date") LocalDate date);
}
