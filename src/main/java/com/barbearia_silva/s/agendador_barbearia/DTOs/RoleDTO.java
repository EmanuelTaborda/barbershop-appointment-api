package com.barbearia_silva.s.agendador_barbearia.DTOs;

import com.barbearia_silva.s.agendador_barbearia.models.entities.Role;
import lombok.Getter;

@Getter
public class RoleDTO {
    private Long id;
    private String authority;

    public RoleDTO(Role entity) {
        this.id = entity.getId();
        this.authority = entity.getAuthority().toString();
    }
}
