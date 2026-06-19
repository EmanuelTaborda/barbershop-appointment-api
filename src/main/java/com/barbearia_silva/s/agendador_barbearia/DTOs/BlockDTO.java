package com.barbearia_silva.s.agendador_barbearia.DTOs;

import com.barbearia_silva.s.agendador_barbearia.models.entities.Block;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlockDTO {

    private Long id;

    @NotNull(message = "O barbeiro é obrigatório")
    private Long idBarber;

    @NotNull(message = "O início do bloqueio é obrigatório")
    @FutureOrPresent(message = "O início do bloqueio deve ser no presente ou futuro")
    private LocalDateTime startTime;

    @NotNull(message = "O fim do bloqueio é obrigatório")
    @FutureOrPresent(message = "Fim do bloqueio deve ser no presente ou futuro")
    private LocalDateTime endTime;

    public BlockDTO(Block entity) {
        this.id = entity.getId();
        this.idBarber = entity.getBarber().getId();
        this.startTime = entity.getStartTime();
        this.endTime = entity.getEndTime();
    }
}
