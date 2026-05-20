package com.barbearia_silva.s.agendador_barbearia.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_bloqueio")
public class Bloqueio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "barbeiro_id", nullable = false)
    private User barbeiro;
    private LocalDateTime inicioBloqueio;
    private LocalDateTime fimBloqueio;
}
