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
public class AppointmentDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Set<ServiceType> services;
    private UserDTO cleint;
    private UserDTO barber;
    private AppointmentStatus status;

    public AppointmentDTO(Appointment entity) {
        id = entity.getId();
        startTime = entity.getStartTime();
        endTime = entity.getEndTime();
        services = entity.getServices();
        cleint = new UserDTO(entity.getClient());
        barber = new UserDTO(entity.getBarber());
        status = entity.getStatus();
    }
}
