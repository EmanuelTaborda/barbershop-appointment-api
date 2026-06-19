package com.barbearia_silva.s.agendador_barbearia.models.entities;

import com.barbearia_silva.s.agendador_barbearia.models.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tb_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType authority;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

}
