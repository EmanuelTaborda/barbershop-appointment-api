package com.barbearia_silva.s.agendador_barbearia.models.entities;

import com.barbearia_silva.s.agendador_barbearia.models.enums.TipoServico;
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
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime atendimentoInicio;

    private LocalDateTime atendimentoFim;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private User cliente;

    @ManyToOne
    @JoinColumn(name = "barbeiro_id")
    private User barbeiro;

    @ElementCollection(targetClass = TipoServico.class)
    @Enumerated(EnumType.STRING)
    private Set<TipoServico> servico;

}
