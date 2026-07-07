package com.barbershop_appointment_api.models.projections;

import com.barbershop_appointment_api.models.enums.AppointmentStatus;
import com.barbershop_appointment_api.models.enums.ServiceType;
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

