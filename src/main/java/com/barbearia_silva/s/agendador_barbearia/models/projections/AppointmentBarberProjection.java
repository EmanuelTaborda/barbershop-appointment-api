package com.barbearia_silva.s.agendador_barbearia.models.projections;

import com.barbearia_silva.s.agendador_barbearia.models.enums.ServiceType;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Set;

public interface AppointmentBarberProjection {

    Long getId();
    @Value("#{target.client.name}")
    String getClient();
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    Set<ServiceType> getServices();


}
