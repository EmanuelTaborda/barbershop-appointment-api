package com.barbearia_silva.s.agendador_barbearia.DTOs;

import com.barbearia_silva.s.agendador_barbearia.models.entities.Bloqueio;
import com.barbearia_silva.s.agendador_barbearia.models.entities.User;
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
public class BloqueioDTO {

    private Long id;

    @NotNull(message = "O barbeiro é obrigatório")
    private Long idBarbeiro;

    @NotNull(message = "O início do bloqueio é obrigatório")
    @FutureOrPresent(message = "O início do bloqueio deve ser no presente ou futuro")
    private LocalDateTime inicioBloqueio;

    @NotNull(message = "O fim do bloqueio é obrigatório")
    @FutureOrPresent(message = "Fim do bloqueio deve ser no presente ou futuro")
    private LocalDateTime fimBloqueio;

    public BloqueioDTO(Bloqueio entity) {
        this.id = entity.getId();
        this.idBarbeiro = entity.getBarbeiro().getId();
        this.inicioBloqueio = entity.getInicioBloqueio();
        this.fimBloqueio = entity.getFimBloqueio();
    }
}
