package com.barbearia_silva.s.agendador_barbearia.DTOs;

import com.barbearia_silva.s.agendador_barbearia.models.entities.Appointment;
import com.barbearia_silva.s.agendador_barbearia.models.enums.AppointmentStatus;
import com.barbearia_silva.s.agendador_barbearia.models.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentReponseDTO {

    private LocalDateTime startTime;
    private Set<ServiceType> services;
    private String barberName;
    private AppointmentStatus status;

    public AppointmentReponseDTO(Appointment entity) {
        startTime = entity.getStartTime();
        services = entity.getServices();
        barberName = entity.getBarber().getName();
        status = entity.getStatus();
    }
}
