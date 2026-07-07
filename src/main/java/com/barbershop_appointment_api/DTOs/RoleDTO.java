package com.barbershop_appointment_api.DTOs;

import com.barbershop_appointment_api.models.entities.Role;
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
