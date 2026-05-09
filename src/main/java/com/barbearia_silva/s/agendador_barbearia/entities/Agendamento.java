package com.barbearia_silva.s.agendador_barbearia.entities;

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
@Table(name = "tb_agendamento")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String servico;
    private LocalDateTime dataHora;

    @OneToOne
    @JoinColumn(name = "cliente_id")
    private User cliente;

    @OneToOne
    @JoinColumn(name = "barbeiro_id")
    private User atendente;

}
