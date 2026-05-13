package com.barbearia_silva.s.agendador_barbearia.DTOs;

import com.barbearia_silva.s.agendador_barbearia.models.enums.TipoServico;
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
public class AgendamentoMinDTO {

    @NotNull(message = "Hora de inicio é obrigatória")
    @FutureOrPresent(message = "Hora de inicio deve ser no presente ou futuro")
    private LocalDateTime atendimentoInicio;

    @NotEmpty(message = "Deve conter ao menos um servico")
    private Set<TipoServico> servico;

    @NotBlank(message = "Email do cliente é obrigatório")
    private String clienteEmail;

    @NotBlank(message = "Email do atendente é obrigatório")
    private String barbeiroEmail;
}
