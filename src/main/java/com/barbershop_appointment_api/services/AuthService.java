package com.barbershop_appointment_api.services;

import com.barbershop_appointment_api.exceptions.ForbiddenException;
import com.barbershop_appointment_api.models.entities.User;
import com.barbershop_appointment_api.models.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    UserService userService;

    public void validateSelfOrAdmin(Long id) {
        User user = userService.authenticated();
        if (!user.hasRole(UserType.ROLE_ADMIN) && !user.getId().equals(id)){
            throw new ForbiddenException("Acesso negado");
        };
    }
}
