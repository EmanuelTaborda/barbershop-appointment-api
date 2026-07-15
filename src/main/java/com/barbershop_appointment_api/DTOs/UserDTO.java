package com.barbershop_appointment_api.DTOs;

import com.barbershop_appointment_api.models.entities.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;

    private List<String> roles = new ArrayList<>();

    public UserDTO(User entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.phone = entity.getPhone();
        for (GrantedAuthority role : entity.getRoles()) {
            this.roles.add(role.getAuthority().toString());
        }
    }

}
