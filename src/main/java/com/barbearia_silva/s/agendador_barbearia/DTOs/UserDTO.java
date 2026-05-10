package com.barbearia_silva.s.agendador_barbearia.DTOs;

import com.barbearia_silva.s.agendador_barbearia.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;

    public UserDTO(User entity) {
        id = entity.getId();
        nome = entity.getNome();
        email = entity.getEmail();
        telefone = entity.getTelefone();
    }
}
