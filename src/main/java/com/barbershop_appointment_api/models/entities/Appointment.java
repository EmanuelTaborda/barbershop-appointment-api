package com.barbershop_appointment_api.models.entities;

import com.barbershop_appointment_api.models.enums.AppointmentStatus;
import com.barbershop_appointment_api.models.enums.ServiceType;
import jakarta.persistence.*;
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
@Entity
@Table(name = "tb_agendamento")
public class  Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private User client;

    @ManyToOne
    @JoinColumn(name = "barbeiro_id")
    private User barber;

    @ElementCollection(targetClass = ServiceType.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<ServiceType> services;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
}
