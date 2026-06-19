package com.barbearia_silva.s.agendador_barbearia.models.projections;

import com.barbearia_silva.s.agendador_barbearia.models.enums.AppointmentStatus;
import com.barbearia_silva.s.agendador_barbearia.models.enums.ServiceType;
import org.springframework.beans.factory.annotation.Value;


import java.time.LocalDateTime;
import java.util.Set;

public interface AppointmentProjection {

    Long getId();

    LocalDateTime getStartTime();

    Set<ServiceType> getServices();

    @Value("#{target.barber.name}")
    String getBarberName();

    AppointmentStatus getStatus();
}

