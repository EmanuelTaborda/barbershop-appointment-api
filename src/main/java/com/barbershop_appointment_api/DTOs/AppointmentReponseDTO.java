package com.barbershop_appointment_api.DTOs;

import com.barbershop_appointment_api.models.entities.Appointment;
import com.barbershop_appointment_api.models.enums.AppointmentStatus;
import com.barbershop_appointment_api.models.enums.ServiceType;
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
