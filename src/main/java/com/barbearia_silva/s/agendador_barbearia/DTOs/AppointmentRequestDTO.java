package com.barbearia_silva.s.agendador_barbearia.DTOs;

import com.barbearia_silva.s.agendador_barbearia.models.enums.ServiceType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class AppointmentRequestDTO {

    @NotNull(message = "Hora de inicio é obrigatória")
    @FutureOrPresent(message = "Hora de inicio deve ser no presente ou futuro")
    private LocalDateTime startTime;

    @NotEmpty(message = "Deve conter ao menos um servico")
    private Set<ServiceType> services;

    @NotBlank(message = "Email do cliente é obrigatório")
    private String clientEmail;

    @NotBlank(message = "Email do atendente é obrigatório")
    private String barberEmail;
}
